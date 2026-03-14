package com.healthcare.claims.api.entitlements.controller;

import com.healthcare.claims.api.entitlements.command.AssignUserToGroupCommand;
import com.healthcare.claims.api.entitlements.command.CreateUserCommand;
import com.healthcare.claims.api.entitlements.command.UpdateUserCommand;
import com.healthcare.claims.api.entitlements.command.UserCommandHandler;
import com.healthcare.claims.api.entitlements.model.User;
import com.healthcare.claims.api.entitlements.model.UserGroup;
import com.healthcare.claims.common.crypto.EncryptionService;
import com.healthcare.claims.common.crypto.IdType;
import com.healthcare.claims.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/entitlements/users")
@RequiredArgsConstructor
public class UserCommandController {

    private final UserCommandHandler userCommandHandler;
    private final EncryptionService encryptionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> createUser(@Valid @RequestBody CreateUserCommand command) {
        User user = userCommandHandler.createUser(command);
        Map<String, String> data = Map.of(
                "id", encryptionService.encryptObject(user.getId(), IdType.USER),
                "username", user.getUsername()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateUser(@PathVariable String id,
                                           @Valid @RequestBody UpdateUserCommand command) {
        String decryptedId = encryptionService.decrypt(id, IdType.USER);
        command.setId(UUID.fromString(decryptedId));
        User user = userCommandHandler.updateUser(command);
        Map<String, String> data = Map.of(
                "id", encryptionService.encryptObject(user.getId(), IdType.USER),
                "username", user.getUsername()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id,
                                           @RequestHeader("X-Tenant-Id") String tenantId) {
        String decryptedId = encryptionService.decrypt(id, IdType.USER);
        String decryptedTenantId = encryptionService.decrypt(tenantId, IdType.TENANT);
        userCommandHandler.deleteUser(decryptedTenantId, UUID.fromString(decryptedId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/groups")
    public ResponseEntity<ApiResponse<Map<String, String>>> assignToGroup(@PathVariable String id,
                                                   @Valid @RequestBody AssignUserToGroupCommand command) {
        String decryptedId = encryptionService.decrypt(id, IdType.USER);
        command.setUserId(UUID.fromString(decryptedId));
        UserGroup userGroup = userCommandHandler.assignToGroup(command);
        Map<String, String> data = Map.of(
                "id", encryptionService.encryptObject(userGroup.getId(), IdType.USER),
                "userId", encryptionService.encryptObject(userGroup.getUser().getId(), IdType.USER),
                "groupId", encryptionService.encryptObject(userGroup.getGroup().getId(), IdType.GROUP)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(data));
    }
}
