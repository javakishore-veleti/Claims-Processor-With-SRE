package com.healthcare.claims.api.claims.service;

import com.healthcare.claims.api.claims.model.Claim;
import com.healthcare.claims.api.claims.model.ClaimStage;
import com.healthcare.claims.api.claims.repository.ClaimRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimWorkflowService {

    private final ClaimRepository claimRepository;
    private final DocumentClassificationService classificationService;
    private final AiExtractionService aiExtractionService;
    private final ObjectMapper objectMapper;

    /**
     * Process uploaded documents through AI extraction pipeline.
     * Called after documents are uploaded for a claim.
     */
    @Transactional
    public Claim processDocuments(UUID claimId, List<String> storageKeys) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        log.info("Processing {} documents for claim {}", storageKeys.size(), claim.getClaimNumber());

        // Update stage to DATA_EXTRACTION
        claim.setStage(ClaimStage.DATA_EXTRACTION);
        claimRepository.save(claim);

        // Extract data from each document
        Map<String, Object> allExtractedData = new LinkedHashMap<>();
        List<Map<String, Object>> documentExtractions = new ArrayList<>();
        double totalConfidence = 0;

        for (String storageKey : storageKeys) {
            String fileName = storageKey.substring(storageKey.lastIndexOf('/') + 1);
            String docType = classificationService.classify(fileName, null).name();
            Map<String, Object> extracted = aiExtractionService.extractData(docType, storageKey);
            documentExtractions.add(extracted);
            totalConfidence += (double) extracted.getOrDefault("confidenceScore", 0.0);
        }

        allExtractedData.put("documents", documentExtractions);
        allExtractedData.put("documentCount", storageKeys.size());

        // Average confidence across all documents
        double avgConfidence = storageKeys.isEmpty() ? 0 : totalConfidence / storageKeys.size();
        allExtractedData.put("averageConfidence", avgConfidence);

        try {
            claim.setExtractedData(objectMapper.writeValueAsString(allExtractedData));
        } catch (Exception e) {
            log.error("Failed to serialize extracted data", e);
        }
        claim.setConfidenceScore(java.math.BigDecimal.valueOf(avgConfidence));
        claim.setStage(ClaimStage.EXTRACTION_REVIEW);

        return claimRepository.save(claim);
    }

    /**
     * Run eligibility check on a claim.
     */
    @Transactional
    public Claim checkEligibility(UUID claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        log.info("Running eligibility check for claim {}", claim.getClaimNumber());
        claim.setStage(ClaimStage.ELIGIBILITY_CHECK);

        // Mock eligibility check - in production, verify against member policy
        boolean eligible = true; // Always eligible in mock

        if (eligible) {
            claim.setStage(ClaimStage.ADJUDICATION);
            log.info("Claim {} passed eligibility check", claim.getClaimNumber());
        } else {
            claim.setStage(ClaimStage.DENIED);
            claim.setAdjudicationResult("DENIED: Member not eligible");
            log.warn("Claim {} failed eligibility check", claim.getClaimNumber());
        }

        return claimRepository.save(claim);
    }

    /**
     * Run AI-assisted adjudication on a claim.
     */
    @Transactional
    public Claim adjudicate(UUID claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        log.info("Running adjudication for claim {}", claim.getClaimNumber());

        double confidence = claim.getConfidenceScore() != null ?
            claim.getConfidenceScore().doubleValue() : 0.5;

        // Confidence-based routing
        if (confidence >= 0.95) {
            // Auto-approve high confidence claims
            claim.setStage(ClaimStage.APPROVED);
            claim.setAdjudicationResult("AUTO_APPROVED: High confidence (" +
                String.format("%.1f%%", confidence * 100) + ")");
            log.info("Claim {} auto-approved (confidence: {}%)", claim.getClaimNumber(),
                String.format("%.1f", confidence * 100));
        } else if (confidence >= 0.70) {
            // Queue for staff review
            claim.setStage(ClaimStage.ADJUDICATION_REVIEW);
            claim.setAdjudicationResult("PENDING_REVIEW: Medium confidence (" +
                String.format("%.1f%%", confidence * 100) + ") - requires staff review");
            log.info("Claim {} queued for review (confidence: {}%)", claim.getClaimNumber(),
                String.format("%.1f", confidence * 100));
        } else {
            // Low confidence - escalate
            claim.setStage(ClaimStage.ADJUDICATION_REVIEW);
            claim.setAdjudicationResult("ESCALATED: Low confidence (" +
                String.format("%.1f%%", confidence * 100) + ") - requires senior review");
            log.warn("Claim {} escalated (confidence: {}%)", claim.getClaimNumber(),
                String.format("%.1f", confidence * 100));
        }

        return claimRepository.save(claim);
    }

    /**
     * Validate that a stage transition is allowed.
     */
    public boolean isValidTransition(ClaimStage from, ClaimStage to) {
        Map<ClaimStage, Set<ClaimStage>> allowed = Map.ofEntries(
            Map.entry(ClaimStage.INTAKE_RECEIVED, Set.of(ClaimStage.DOCUMENT_VERIFICATION, ClaimStage.DATA_EXTRACTION)),
            Map.entry(ClaimStage.DOCUMENT_VERIFICATION, Set.of(ClaimStage.DATA_EXTRACTION, ClaimStage.DENIED)),
            Map.entry(ClaimStage.DATA_EXTRACTION, Set.of(ClaimStage.EXTRACTION_REVIEW)),
            Map.entry(ClaimStage.EXTRACTION_REVIEW, Set.of(ClaimStage.ELIGIBILITY_CHECK, ClaimStage.DATA_EXTRACTION)),
            Map.entry(ClaimStage.ELIGIBILITY_CHECK, Set.of(ClaimStage.ADJUDICATION, ClaimStage.DENIED)),
            Map.entry(ClaimStage.ADJUDICATION, Set.of(ClaimStage.ADJUDICATION_REVIEW, ClaimStage.APPROVED, ClaimStage.DENIED, ClaimStage.PARTIAL_APPROVED)),
            Map.entry(ClaimStage.ADJUDICATION_REVIEW, Set.of(ClaimStage.APPROVED, ClaimStage.DENIED, ClaimStage.PARTIAL_APPROVED, ClaimStage.ADJUDICATION)),
            Map.entry(ClaimStage.APPROVED, Set.of(ClaimStage.SETTLEMENT)),
            Map.entry(ClaimStage.PARTIAL_APPROVED, Set.of(ClaimStage.SETTLEMENT, ClaimStage.APPEAL)),
            Map.entry(ClaimStage.DENIED, Set.of(ClaimStage.APPEAL)),
            Map.entry(ClaimStage.SETTLEMENT, Set.of(ClaimStage.CLOSED)),
            Map.entry(ClaimStage.APPEAL, Set.of(ClaimStage.ADJUDICATION_REVIEW))
        );
        Set<ClaimStage> validTargets = allowed.getOrDefault(from, Set.of());
        return validTargets.contains(to);
    }
}
