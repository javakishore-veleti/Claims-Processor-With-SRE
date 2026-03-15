package com.healthcare.claims.api.tenants.controller;

import com.healthcare.claims.api.tenants.command.CreateTenantCommand;
import com.healthcare.claims.api.tenants.command.TenantCommandHandler;
import com.healthcare.claims.api.tenants.command.UpdateTenantCommand;
import com.healthcare.claims.api.tenants.model.Tenant;
import com.healthcare.claims.api.tenants.model.TenantStatus;
import com.healthcare.claims.common.tenants.dto.TenantRespDTO;
import com.healthcare.claims.common.crypto.EncryptionService;
import com.healthcare.claims.common.crypto.IdType;
import com.healthcare.claims.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tenants - Commands", description = "Tenant create, update, delete, status change")
public class TenantCommandController {

    private final TenantCommandHandler commandHandler;
    private final EncryptionService encryptionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TenantRespDTO>> createTenant(@Valid @RequestBody CreateTenantCommand command) {
        log.info("POST /api/v1/tenants - Creating tenant: {}", command.getTenantId());
        Tenant tenant = commandHandler.handleCreate(command);
        TenantRespDTO response = toResponse(tenant);
        encryptResponseIds(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantRespDTO>> updateTenant(@PathVariable String id,
                                                                    @Valid @RequestBody UpdateTenantCommand command) {
        String decryptedId = encryptionService.decrypt(id, IdType.TENANT);
        log.info("PUT /api/v1/tenants/{} - Updating tenant", decryptedId);
        Tenant tenant = commandHandler.handleUpdate(UUID.fromString(decryptedId), command);
        TenantRespDTO response = toResponse(tenant);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TenantRespDTO>> updateStatus(@PathVariable String id,
                                                                    @RequestBody Map<String, String> body) {
        String decryptedId = encryptionService.decrypt(id, IdType.TENANT);
        log.info("PATCH /api/v1/tenants/{}/status - Updating status", decryptedId);
        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }
        TenantStatus status = TenantStatus.valueOf(statusStr.toUpperCase());
        Tenant tenant = commandHandler.handleStatusChange(UUID.fromString(decryptedId), status);
        TenantRespDTO response = toResponse(tenant);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable String id) {
        String decryptedId = encryptionService.decrypt(id, IdType.TENANT);
        log.info("DELETE /api/v1/tenants/{} - Deleting tenant", decryptedId);
        commandHandler.handleDelete(UUID.fromString(decryptedId));
        return ResponseEntity.noContent().build();
    }

    private TenantRespDTO toResponse(Tenant tenant) {
        return TenantRespDTO.builder()
                .id(tenant.getId() != null ? tenant.getId().toString() : null)
                .tenantId(tenant.getTenantId())
                .name(tenant.getName())
                .displayName(tenant.getDisplayName())
                .domain(tenant.getDomain())
                .status(tenant.getStatus() != null ? tenant.getStatus().name() : null)
                .plan(tenant.getPlan())
                .contactEmail(tenant.getContactEmail())
                .contactPhone(tenant.getContactPhone())
                .address(tenant.getAddress())
                .maxUsers(tenant.getMaxUsers())
                .createdAt(tenant.getCreatedAt() != null
                        ? tenant.getCreatedAt().toString() : null)
                .updatedAt(tenant.getUpdatedAt() != null
                        ? tenant.getUpdatedAt().toString() : null)
                .build();
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
