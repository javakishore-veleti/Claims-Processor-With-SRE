package com.healthcare.claims.api.entitlements.controller;

import com.healthcare.claims.api.entitlements.command.EntitlementCommandHandler;
import com.healthcare.claims.api.entitlements.model.Role;
import com.healthcare.claims.common.entitlements.dto.RoleRespDTO;
import com.healthcare.claims.api.entitlements.repository.RoleRepository;
import com.healthcare.claims.common.crypto.EncryptionService;
import com.healthcare.claims.common.crypto.IdType;
import com.healthcare.claims.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/entitlements/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role CRUD and management")
public class RoleController {

    private final EntitlementCommandHandler entitlementCommandHandler;
    private final RoleRepository roleRepository;
    private final EncryptionService encryptionService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoleRespDTO>> createRole(@RequestHeader("X-Tenant-Id") String tenantId,
                                           @RequestParam String name,
                                           @RequestParam(required = false) String description,
                                           @RequestParam(defaultValue = "false") boolean systemRole) {
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        Role role = entitlementCommandHandler.createRole(decryptedTenantId, name, description, systemRole);
        RoleRespDTO response = toResponse(role);
        encryptResponseIds(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleRespDTO>>> getRoles(@RequestHeader("X-Tenant-Id") String tenantId) {
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        List<RoleRespDTO> roles = roleRepository.findByTenantId(decryptedTenantId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        roles.forEach(this::encryptResponseIds);
        return ResponseEntity.ok(ApiResponse.success(roles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleRespDTO>> getRoleById(@PathVariable String id) {
        String decryptedId = encryptionService.decrypt(id, IdType.ROLE);
        Role role = roleRepository.findById(UUID.fromString(decryptedId))
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + decryptedId));
        RoleRespDTO response = toResponse(role);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable String id,
                                           @RequestHeader("X-Tenant-Id") String tenantId) {
        String decryptedId = encryptionService.decrypt(id, IdType.ROLE);
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        entitlementCommandHandler.deleteRole(decryptedTenantId, UUID.fromString(decryptedId));
        return ResponseEntity.noContent().build();
    }

    private RoleRespDTO toResponse(Role role) {
        return RoleRespDTO.builder()
                .id(role.getId() != null ? role.getId().toString() : null)
                .tenantId(role.getTenantId())
                .name(role.getName())
                .description(role.getDescription())
                .systemRole(role.isSystemRole())
                .createdAt(role.getCreatedAt() != null
                        ? role.getCreatedAt().toString() : null)
                .updatedAt(role.getUpdatedAt() != null
                        ? role.getUpdatedAt().toString() : null)
                .build();
    }

    private void encryptResponseIds(RoleRespDTO response) {
        if (response.getId() != null) {
            response.setId(encryptionService.encrypt(response.getId(), IdType.ROLE));
        }
        if (response.getTenantId() != null) {
            response.setTenantId(encryptionService.encrypt(response.getTenantId(), IdType.TENANT));
        }
    }
}
