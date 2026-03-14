package com.healthcare.claims.api.entitlements.controller;

import com.healthcare.claims.api.entitlements.command.EntitlementCommandHandler;
import com.healthcare.claims.api.entitlements.model.Privilege;
import com.healthcare.claims.common.entitlements.dto.PrivilegeRespDTO;
import com.healthcare.claims.api.entitlements.repository.PrivilegeRepository;
import com.healthcare.claims.common.crypto.EncryptionService;
import com.healthcare.claims.common.crypto.IdType;
import com.healthcare.claims.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/entitlements/privileges")
@RequiredArgsConstructor
public class PrivilegeController {

    private final EntitlementCommandHandler entitlementCommandHandler;
    private final PrivilegeRepository privilegeRepository;
    private final EncryptionService encryptionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PrivilegeRespDTO>> createPrivilege(@RequestHeader("X-Tenant-Id") String tenantId,
                                                     @RequestParam String name,
                                                     @RequestParam(required = false) String description,
                                                     @RequestParam String resource,
                                                     @RequestParam String action) {
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        Privilege privilege = entitlementCommandHandler.createPrivilege(decryptedTenantId, name, description, resource, action);
        PrivilegeRespDTO response = toResponse(privilege);
        encryptResponseIds(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PrivilegeRespDTO>>> getPrivileges(@RequestHeader("X-Tenant-Id") String tenantId) {
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        List<PrivilegeRespDTO> privileges = privilegeRepository.findByTenantId(decryptedTenantId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        privileges.forEach(this::encryptResponseIds);
        return ResponseEntity.ok(ApiResponse.success(privileges));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PrivilegeRespDTO>> getPrivilegeById(@PathVariable String id) {
        String decryptedId = encryptionService.decrypt(id, IdType.PRIVILEGE);
        Privilege privilege = privilegeRepository.findById(UUID.fromString(decryptedId))
                .orElseThrow(() -> new IllegalArgumentException("Privilege not found: " + decryptedId));
        PrivilegeRespDTO response = toResponse(privilege);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrivilege(@PathVariable String id,
                                                @RequestHeader("X-Tenant-Id") String tenantId) {
        String decryptedId = encryptionService.decrypt(id, IdType.PRIVILEGE);
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        entitlementCommandHandler.deletePrivilege(decryptedTenantId, UUID.fromString(decryptedId));
        return ResponseEntity.noContent().build();
    }

    private PrivilegeRespDTO toResponse(Privilege privilege) {
        return PrivilegeRespDTO.builder()
                .id(privilege.getId() != null ? privilege.getId().toString() : null)
                .tenantId(privilege.getTenantId())
                .name(privilege.getName())
                .description(privilege.getDescription())
                .resource(privilege.getResource())
                .action(privilege.getAction())
                .createdAt(privilege.getCreatedAt() != null
                        ? privilege.getCreatedAt().toString() : null)
                .updatedAt(privilege.getUpdatedAt() != null
                        ? privilege.getUpdatedAt().toString() : null)
                .build();
    }

    private void encryptResponseIds(PrivilegeRespDTO response) {
        if (response.getId() != null) {
            response.setId(encryptionService.encrypt(response.getId(), IdType.PRIVILEGE));
        }
        if (response.getTenantId() != null) {
            response.setTenantId(encryptionService.encrypt(response.getTenantId(), IdType.TENANT));
        }
    }
}
