package com.healthcare.claims.api.claims.service;

import com.healthcare.claims.common.claims.dto.ClaimReqDTO;
import com.healthcare.claims.common.claims.dto.ClaimRespDTO;
import com.healthcare.claims.api.claims.model.Claim;
import com.healthcare.claims.api.claims.model.ClaimStage;
import com.healthcare.claims.api.claims.repository.ClaimRepository;
import com.healthcare.claims.api.claims.search.SearchIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final SearchIndexService searchIndexService;

    private static final String INDEX_NAME = "claims-data";

    @Override
    public ClaimRespDTO createClaim(ClaimReqDTO request) {
        log.info("Creating claim with number: {}", request.getClaimNumber());

        Claim claim = Claim.builder()
                .customerId(request.getCustomerId())
                .claimNumber(request.getClaimNumber())
                .submittedDate(request.getSubmittedDate() != null
                        ? LocalDate.parse(request.getSubmittedDate()) : null)
                .documents(request.getDocumentIds())
                .extractedData(request.getExtractedData() != null
                        ? request.getExtractedData().toString() : null)
                .stage(request.getStage() != null
                        ? ClaimStage.valueOf(request.getStage()) : ClaimStage.INTAKE_RECEIVED)
                .build();

        Claim saved = claimRepository.save(claim);

        try {
            searchIndexService.indexDocument(INDEX_NAME, saved.getId().toString(), claimToMap(saved));
        } catch (Exception e) {
            log.warn("Failed to index claim {} in search: {}", saved.getId(), e.getMessage());
        }

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimRespDTO getClaimById(UUID id) {
        log.info("Fetching claim by id: {}", id);

        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));
        return toResponse(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClaimRespDTO> searchClaims(String customerId, ClaimStage stage,
                                            LocalDate fromDate, LocalDate toDate,
                                            Pageable pageable) {
        log.info("Searching claims - customerId: {}, stage: {}, from: {}, to: {}",
                customerId, stage, fromDate, toDate);

        return claimRepository.searchClaims(customerId, stage, fromDate, toDate, pageable)
                .map(this::toResponse);
    }

    @Override
    public ClaimRespDTO updateClaim(UUID id, ClaimReqDTO request) {
        log.info("Updating claim with id: {}", id);

        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));

        claim.setCustomerId(request.getCustomerId());
        claim.setClaimNumber(request.getClaimNumber());
        claim.setSubmittedDate(request.getSubmittedDate() != null
                ? LocalDate.parse(request.getSubmittedDate()) : null);
        claim.setDocuments(request.getDocumentIds());
        claim.setExtractedData(request.getExtractedData() != null
                ? request.getExtractedData().toString() : null);

        Claim updated = claimRepository.save(claim);

        try {
            searchIndexService.updateDocument(INDEX_NAME, updated.getId().toString(), claimToMap(updated));
        } catch (Exception e) {
            log.warn("Failed to update claim {} in search: {}", updated.getId(), e.getMessage());
        }

        return toResponse(updated);
    }

    @Override
    public ClaimRespDTO updateClaimStage(UUID id, ClaimStage stage) {
        log.info("Updating claim stage - id: {}, new stage: {}", id, stage);

        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));

        claim.setStage(stage);
        Claim updated = claimRepository.save(claim);

        try {
            searchIndexService.updateDocument(INDEX_NAME, updated.getId().toString(), claimToMap(updated));
        } catch (Exception e) {
            log.warn("Failed to update claim stage {} in search: {}", updated.getId(), e.getMessage());
        }

        return toResponse(updated);
    }

    private Map<String, Object> claimToMap(Claim claim) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", claim.getId() != null ? claim.getId().toString() : null);
        map.put("customerId", claim.getCustomerId());
        map.put("claimNumber", claim.getClaimNumber());
        map.put("stage", claim.getStage() != null ? claim.getStage().name() : null);
        map.put("submittedDate", claim.getSubmittedDate() != null ? claim.getSubmittedDate().toString() : null);
        map.put("extractedData", claim.getExtractedData());
        map.put("adjudicationResult", claim.getAdjudicationResult());
        map.put("confidenceScore", claim.getConfidenceScore() != null ? claim.getConfidenceScore().doubleValue() : null);
        map.put("createdAt", claim.getCreatedAt() != null ? claim.getCreatedAt().toString() : null);
        map.put("updatedAt", claim.getUpdatedAt() != null ? claim.getUpdatedAt().toString() : null);
        return map;
    }

    private ClaimRespDTO toResponse(Claim claim) {
        return ClaimRespDTO.builder()
                .id(claim.getId() != null ? claim.getId().toString() : null)
                .customerId(claim.getCustomerId())
                .claimNumber(claim.getClaimNumber())
                .stage(claim.getStage() != null ? claim.getStage().name() : null)
                .submittedDate(claim.getSubmittedDate() != null
                        ? claim.getSubmittedDate().toString() : null)
                .documentIds(claim.getDocuments())
                .extractedData(null)
                .adjudicationResult(claim.getAdjudicationResult())
                .confidenceScore(claim.getConfidenceScore() != null
                        ? claim.getConfidenceScore().toString() : null)
                .createdAt(claim.getCreatedAt() != null
                        ? claim.getCreatedAt().toString() : null)
                .updatedAt(claim.getUpdatedAt() != null
                        ? claim.getUpdatedAt().toString() : null)
                .build();
    }
}
