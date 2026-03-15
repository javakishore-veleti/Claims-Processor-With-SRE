package com.healthcare.claims.api.entitlements.controller;

import com.healthcare.claims.api.entitlements.command.AssignPrivilegeToGroupRoleCommand;
import com.healthcare.claims.api.entitlements.command.AssignRoleToGroupCommand;
import com.healthcare.claims.api.entitlements.command.EntitlementCommandHandler;
import com.healthcare.claims.api.entitlements.model.Group;
import com.healthcare.claims.api.entitlements.model.GroupRole;
import com.healthcare.claims.api.entitlements.model.GroupRolePrivilege;
import com.healthcare.claims.common.entitlements.dto.GroupRespDTO;
import com.healthcare.claims.api.entitlements.repository.GroupRepository;
import com.healthcare.claims.api.entitlements.repository.GroupRoleRepository;
import com.healthcare.claims.common.crypto.EncryptionService;
import com.healthcare.claims.common.crypto.IdType;
import com.healthcare.claims.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/entitlements/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Group CRUD, role assignment, privilege assignment")
public class GroupController {

    private final EntitlementCommandHandler entitlementCommandHandler;
    private final GroupRepository groupRepository;
    private final GroupRoleRepository groupRoleRepository;
    private final EncryptionService encryptionService;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupRespDTO>> createGroup(@RequestHeader("X-Tenant-Id") String tenantId,
                                             @RequestParam String name,
                                             @RequestParam(required = false) String description) {
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        Group group = entitlementCommandHandler.createGroup(decryptedTenantId, name, description);
        GroupRespDTO response = toResponse(group);
        encryptResponseIds(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupRespDTO>>> getGroups(@RequestHeader("X-Tenant-Id") String tenantId) {
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        List<GroupRespDTO> groups = groupRepository.findByTenantId(decryptedTenantId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        groups.forEach(this::encryptResponseIds);
        return ResponseEntity.ok(ApiResponse.success(groups));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupRespDTO>> getGroupById(@PathVariable String id) {
        String decryptedId = encryptionService.decrypt(id, IdType.GROUP);
        Group group = groupRepository.findById(UUID.fromString(decryptedId))
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + decryptedId));
        GroupRespDTO response = toResponse(group);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable String id,
                                            @RequestHeader("X-Tenant-Id") String tenantId) {
        String decryptedId = encryptionService.decrypt(id, IdType.GROUP);
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        entitlementCommandHandler.deleteGroup(decryptedTenantId, UUID.fromString(decryptedId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<Map<String, String>>> assignRoleToGroup(@PathVariable String id,
                                                       @RequestHeader("X-Tenant-Id") String tenantId,
                                                       @RequestParam String roleId) {
        String decryptedId = encryptionService.decrypt(id, IdType.GROUP);
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        String decryptedRoleId = encryptionService.decrypt(roleId, IdType.ROLE);
        AssignRoleToGroupCommand command = AssignRoleToGroupCommand.builder()
                .tenantId(decryptedTenantId)
                .groupId(UUID.fromString(decryptedId))
                .roleId(UUID.fromString(decryptedRoleId))
                .build();
        GroupRole groupRole = entitlementCommandHandler.assignRoleToGroup(command);
        Map<String, String> data = Map.of(
                "id", encryptionService.encryptObject(groupRole.getId(), IdType.GROUP),
                "groupId", encryptionService.encrypt(decryptedId, IdType.GROUP),
                "roleId", encryptionService.encrypt(decryptedRoleId, IdType.ROLE)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(data));
    }

    @PostMapping("/{id}/roles/{roleId}/privileges")
    public ResponseEntity<ApiResponse<Map<String, String>>> assignPrivilegeToGroupRole(
            @PathVariable String id,
            @PathVariable String roleId,
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam String privilegeId) {
        String decryptedId = encryptionService.decrypt(id, IdType.GROUP);
        String decryptedRoleId = encryptionService.decrypt(roleId, IdType.ROLE);
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        String decryptedPrivilegeId = encryptionService.decrypt(privilegeId, IdType.PRIVILEGE);

        // Find the GroupRole for this group+role combination
        GroupRole groupRole = groupRoleRepository.findByTenantIdAndGroupIdAndRoleId(
                        decryptedTenantId, UUID.fromString(decryptedId), UUID.fromString(decryptedRoleId))
                .orElseThrow(() -> new IllegalArgumentException(
                        "GroupRole not found for group: " + decryptedId + " and role: " + decryptedRoleId));

        AssignPrivilegeToGroupRoleCommand command = AssignPrivilegeToGroupRoleCommand.builder()
                .tenantId(decryptedTenantId)
                .groupRoleId(groupRole.getId())
                .privilegeId(UUID.fromString(decryptedPrivilegeId))
                .build();
        GroupRolePrivilege grp = entitlementCommandHandler.assignPrivilegeToGroupRole(command);
        Map<String, String> data = Map.of(
                "id", encryptionService.encryptObject(grp.getId(), IdType.PRIVILEGE),
                "privilegeId", encryptionService.encrypt(decryptedPrivilegeId, IdType.PRIVILEGE)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(data));
    }

    private GroupRespDTO toResponse(Group group) {
        return GroupRespDTO.builder()
                .id(group.getId() != null ? group.getId().toString() : null)
                .tenantId(group.getTenantId())
                .name(group.getName())
                .description(group.getDescription())
                .createdAt(group.getCreatedAt() != null
                        ? group.getCreatedAt().toString() : null)
                .updatedAt(group.getUpdatedAt() != null
                        ? group.getUpdatedAt().toString() : null)
                .build();
    }

    private void encryptResponseIds(GroupRespDTO response) {
        if (response.getId() != null) {
            response.setId(encryptionService.encrypt(response.getId(), IdType.GROUP));
        }
        if (response.getTenantId() != null) {
            response.setTenantId(encryptionService.encrypt(response.getTenantId(), IdType.TENANT));
        }
    }
}
