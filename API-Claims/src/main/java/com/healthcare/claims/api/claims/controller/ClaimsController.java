package com.healthcare.claims.api.claims.controller;

import com.healthcare.claims.common.claims.dto.ClaimReqDTO;
import com.healthcare.claims.common.claims.dto.ClaimRespDTO;
import com.healthcare.claims.api.claims.model.Claim;
import com.healthcare.claims.api.claims.model.ClaimStage;
import com.healthcare.claims.api.claims.service.ClaimService;
import com.healthcare.claims.api.claims.service.ClaimWorkflowService;
import com.healthcare.claims.api.claims.service.DocumentStorageService;
import com.healthcare.claims.common.crypto.EncryptionService;
import com.healthcare.claims.common.crypto.IdType;
import com.healthcare.claims.common.dto.ApiResponse;
import com.healthcare.claims.common.dto.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
@Tag(name = "Claims", description = "Claims CRUD, search, stage management, and processing workflow")
public class ClaimsController {

    private final ClaimService claimService;
    private final EncryptionService encryptionService;
    private final DocumentStorageService documentStorageService;
    private final ClaimWorkflowService claimWorkflowService;

    @PostMapping
    @Operation(summary = "Create a new claim", description = "Creates a claim for an existing member within a tenant")
    public ResponseEntity<ApiResponse<ClaimRespDTO>> createClaim(@Valid @RequestBody ClaimReqDTO request) {
        // Decrypt incoming encrypted IDs
        if (request.getCustomerId() != null) {
            request.setCustomerId(encryptionService.decrypt(request.getCustomerId(), IdType.CUSTOMER));
        }
        ClaimRespDTO response = claimService.createClaim(request);
        encryptResponseIds(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get claim by ID", description = "Returns full claim details including extracted data and adjudication result")
    public ResponseEntity<ApiResponse<ClaimRespDTO>> getClaimById(@PathVariable String id) {
        String decryptedId = encryptionService.decrypt(id, IdType.CLAIM);
        ClaimRespDTO response = claimService.getClaimById(UUID.fromString(decryptedId));
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Search claims", description = "Search claims by customerId, stage, claimNumber with pagination")
    public ResponseEntity<PagedResponse<ClaimRespDTO>> searchClaims(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) ClaimStage stage,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 20) Pageable pageable) {
        String decryptedCustomerId = customerId != null
                ? encryptionService.decrypt(customerId, IdType.CUSTOMER) : null;
        Page<ClaimRespDTO> results = claimService.searchClaims(decryptedCustomerId, stage, fromDate, toDate, pageable);
        results.getContent().forEach(this::encryptResponseIds);
        return ResponseEntity.ok(PagedResponse.of(results));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClaimRespDTO>> updateClaim(@PathVariable String id,
                                                                  @Valid @RequestBody ClaimReqDTO request) {
        String decryptedId = encryptionService.decrypt(id, IdType.CLAIM);
        if (request.getCustomerId() != null) {
            request.setCustomerId(encryptionService.decrypt(request.getCustomerId(), IdType.CUSTOMER));
        }
        ClaimRespDTO response = claimService.updateClaim(UUID.fromString(decryptedId), request);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/stage")
    @Operation(summary = "Update claim stage", description = "Validates and advances the claim to a new processing stage")
    public ResponseEntity<ApiResponse<ClaimRespDTO>> updateClaimStage(@PathVariable String id,
                                                                      @RequestBody Map<String, String> body) {
        String decryptedId = encryptionService.decrypt(id, IdType.CLAIM);
        ClaimStage newStage = ClaimStage.valueOf(body.get("stage"));

        // Validate stage transition
        ClaimRespDTO current = claimService.getClaimById(UUID.fromString(decryptedId));
        ClaimStage currentStage = ClaimStage.valueOf(current.getStage());
        if (!claimWorkflowService.isValidTransition(currentStage, newStage)) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Invalid stage transition from " + currentStage + " to " + newStage));
        }

        ClaimRespDTO response = claimService.updateClaimStage(UUID.fromString(decryptedId), newStage);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/documents")
    @Operation(summary = "Upload claim documents", description = "Upload one or more claim artifacts (PDF, images, EDI)")
    public ResponseEntity<ApiResponse<List<String>>> uploadDocuments(
            @PathVariable String id,
            @RequestParam("files") List<MultipartFile> files,
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "default-tenant") String tenantId) {
        String decryptedId = encryptionService.decrypt(id, IdType.CLAIM);
        List<String> storageKeys = new ArrayList<>();
        for (MultipartFile file : files) {
            String key = documentStorageService.store(tenantId, decryptedId, file);
            storageKeys.add(key);
        }
        return ResponseEntity.ok(ApiResponse.success(storageKeys, "Documents uploaded successfully"));
    }

    @PostMapping("/{id}/process-documents")
    @Operation(summary = "Process uploaded documents", description = "Runs AI extraction pipeline on uploaded documents")
    public ResponseEntity<ApiResponse<ClaimRespDTO>> processDocuments(@PathVariable String id,
            @RequestBody List<String> storageKeys) {
        String decryptedId = encryptionService.decrypt(id, IdType.CLAIM);
        Claim claim = claimWorkflowService.processDocuments(UUID.fromString(decryptedId), storageKeys);
        ClaimRespDTO response = toResponse(claim);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response, "Documents processed and data extracted"));
    }

    @PostMapping("/{id}/check-eligibility")
    public ResponseEntity<ApiResponse<ClaimRespDTO>> checkEligibility(@PathVariable String id) {
        String decryptedId = encryptionService.decrypt(id, IdType.CLAIM);
        Claim claim = claimWorkflowService.checkEligibility(UUID.fromString(decryptedId));
        ClaimRespDTO response = toResponse(claim);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response, "Eligibility check completed"));
    }

    @PostMapping("/{id}/adjudicate")
    @Operation(summary = "Run AI adjudication", description = "Confidence-based routing: >=95% auto-approve, 70-94% review, <70% escalate")
    public ResponseEntity<ApiResponse<ClaimRespDTO>> adjudicate(@PathVariable String id) {
        String decryptedId = encryptionService.decrypt(id, IdType.CLAIM);
        Claim claim = claimWorkflowService.adjudicate(UUID.fromString(decryptedId));
        ClaimRespDTO response = toResponse(claim);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response, "Adjudication completed"));
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
                .adjudicationResult(claim.getAdjudicationResult())
                .confidenceScore(claim.getConfidenceScore() != null
                        ? claim.getConfidenceScore().toString() : null)
                .createdAt(claim.getCreatedAt() != null
                        ? claim.getCreatedAt().toString() : null)
                .updatedAt(claim.getUpdatedAt() != null
                        ? claim.getUpdatedAt().toString() : null)
                .build();
    }

    private void encryptResponseIds(ClaimRespDTO response) {
        if (response.getId() != null) {
            response.setId(encryptionService.encrypt(response.getId(), IdType.CLAIM));
        }
        if (response.getCustomerId() != null) {
            response.setCustomerId(encryptionService.encrypt(response.getCustomerId(), IdType.CUSTOMER));
        }
        if (response.getTenantId() != null) {
            response.setTenantId(encryptionService.encrypt(response.getTenantId(), IdType.TENANT));
        }
    }
}
