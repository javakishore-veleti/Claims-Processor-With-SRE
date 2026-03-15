package com.healthcare.claims.api.entitlements.controller;

import com.healthcare.claims.api.entitlements.query.EntitlementQueryHandler;
import com.healthcare.claims.api.entitlements.query.UserQueryHandler;
import com.healthcare.claims.common.entitlements.dto.PrivilegeRespDTO;
import com.healthcare.claims.common.entitlements.dto.UserEntitlementRespDTO;
import com.healthcare.claims.common.entitlements.dto.UserRespDTO;
import com.healthcare.claims.common.crypto.EncryptionService;
import com.healthcare.claims.common.crypto.IdType;
import com.healthcare.claims.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/entitlements/users")
@RequiredArgsConstructor
@Tag(name = "Users - Queries", description = "User search, list, lookup, privilege resolution")
public class UserQueryController {

    private final UserQueryHandler userQueryHandler;
    private final EntitlementQueryHandler entitlementQueryHandler;
    private final EncryptionService encryptionService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserRespDTO>> getUserById(@PathVariable String id) {
        String decryptedId = encryptionService.decrypt(id, IdType.USER);
        UserRespDTO response = userQueryHandler.findById(UUID.fromString(decryptedId));
        encryptUserIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserRespDTO>>> getUsers(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam(required = false) String search) {
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        List<UserRespDTO> results;
        if (search != null && !search.isBlank()) {
            results = userQueryHandler.search(decryptedTenantId, search);
        } else {
            results = userQueryHandler.listByTenant(decryptedTenantId);
        }
        results.forEach(this::encryptUserIds);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/{id}/privileges")
    public ResponseEntity<ApiResponse<UserEntitlementRespDTO>> getUserPrivileges(
            @PathVariable String id,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        String decryptedId = encryptionService.decrypt(id, IdType.USER);
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        UserEntitlementRespDTO response = entitlementQueryHandler.getUserPrivileges(decryptedTenantId, UUID.fromString(decryptedId));
        encryptEntitlementIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private void encryptUserIds(UserRespDTO response) {
        if (response.getId() != null) {
            response.setId(encryptionService.encrypt(response.getId(), IdType.USER));
        }
        if (response.getTenantId() != null) {
            response.setTenantId(encryptionService.encrypt(response.getTenantId(), IdType.TENANT));
        }
    }

    private void encryptEntitlementIds(UserEntitlementRespDTO response) {
        if (response.getUserId() != null) {
            response.setUserId(encryptionService.encrypt(response.getUserId(), IdType.USER));
        }
        if (response.getGroups() != null) {
            response.getGroups().forEach(g -> {
                if (g.getId() != null) g.setId(encryptionService.encrypt(g.getId(), IdType.GROUP));
                if (g.getTenantId() != null) g.setTenantId(encryptionService.encrypt(g.getTenantId(), IdType.TENANT));
            });
        }
        if (response.getRoles() != null) {
            response.getRoles().forEach(r -> {
                if (r.getId() != null) r.setId(encryptionService.encrypt(r.getId(), IdType.ROLE));
                if (r.getTenantId() != null) r.setTenantId(encryptionService.encrypt(r.getTenantId(), IdType.TENANT));
            });
        }
        if (response.getPrivileges() != null) {
            response.getPrivileges().forEach(this::encryptPrivilegeIds);
        }
    }

    private void encryptPrivilegeIds(PrivilegeRespDTO p) {
        if (p.getId() != null) p.setId(encryptionService.encrypt(p.getId(), IdType.PRIVILEGE));
        if (p.getTenantId() != null) p.setTenantId(encryptionService.encrypt(p.getTenantId(), IdType.TENANT));
    }
}
