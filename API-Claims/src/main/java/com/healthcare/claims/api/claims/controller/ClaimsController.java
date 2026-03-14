package com.healthcare.claims.api.claims.controller;

import com.healthcare.claims.common.claims.dto.ClaimReqDTO;
import com.healthcare.claims.common.claims.dto.ClaimRespDTO;
import com.healthcare.claims.api.claims.model.ClaimStage;
import com.healthcare.claims.api.claims.service.ClaimService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
public class ClaimsController {

    private final ClaimService claimService;
    private final EncryptionService encryptionService;

    @PostMapping
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
    public ResponseEntity<ApiResponse<ClaimRespDTO>> getClaimById(@PathVariable String id) {
        String decryptedId = encryptionService.decrypt(id, IdType.CLAIM);
        ClaimRespDTO response = claimService.getClaimById(UUID.fromString(decryptedId));
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
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
    public ResponseEntity<ApiResponse<ClaimRespDTO>> updateClaimStage(@PathVariable String id,
                                                                      @RequestBody Map<String, String> body) {
        String decryptedId = encryptionService.decrypt(id, IdType.CLAIM);
        ClaimStage stage = ClaimStage.valueOf(body.get("stage"));
        ClaimRespDTO response = claimService.updateClaimStage(UUID.fromString(decryptedId), stage);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
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
