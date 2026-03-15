package com.healthcare.claims.api.tenants.controller;

import com.healthcare.claims.api.tenants.query.TenantQueryHandler;
import com.healthcare.claims.common.tenants.dto.TenantRespDTO;
import com.healthcare.claims.common.crypto.EncryptionService;
import com.healthcare.claims.common.crypto.IdType;
import com.healthcare.claims.common.dto.ApiResponse;
import com.healthcare.claims.common.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tenants - Queries", description = "Tenant search, list, lookup")
public class TenantQueryController {

    private final TenantQueryHandler queryHandler;
    private final EncryptionService encryptionService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantRespDTO>> getTenantById(@PathVariable String id) {
        String decryptedId = encryptionService.decrypt(id, IdType.TENANT);
        log.debug("GET /api/v1/tenants/{} - Fetching tenant by id", decryptedId);
        TenantRespDTO response = queryHandler.findById(UUID.fromString(decryptedId));
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/by-tenant-id/{tenantId}")
    public ResponseEntity<ApiResponse<TenantRespDTO>> getTenantByTenantId(@PathVariable String tenantId) {
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        log.debug("GET /api/v1/tenants/by-tenant-id/{} - Fetching tenant by tenantId", decryptedTenantId);
        TenantRespDTO response = queryHandler.findByTenantId(decryptedTenantId);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TenantRespDTO>> listTenants(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.debug("GET /api/v1/tenants - Listing tenants, search={}", search);
        if (search != null && !search.isBlank()) {
            List<TenantRespDTO> results = queryHandler.search(search);
            results.forEach(this::encryptResponseIds);
            Page<TenantRespDTO> page = new org.springframework.data.domain.PageImpl<>(results, pageable, results.size());
            return ResponseEntity.ok(PagedResponse.of(page));
        }
        Page<TenantRespDTO> page = queryHandler.listAll(pageable);
        page.getContent().forEach(this::encryptResponseIds);
        return ResponseEntity.ok(PagedResponse.of(page));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<TenantRespDTO>>> getActiveTenants() {
        log.debug("GET /api/v1/tenants/active - Fetching active tenants");
        List<TenantRespDTO> results = queryHandler.findActive();
        results.forEach(this::encryptResponseIds);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    private void encryptResponseIds(TenantRespDTO response) {
        if (response.getId() != null) {
            response.setId(encryptionService.encrypt(response.getId(), IdType.TENANT));
        }
        if (response.getTenantId() != null) {
            response.setTenantId(encryptionService.encrypt(response.getTenantId(), IdType.TENANT));
        }
    }
}
