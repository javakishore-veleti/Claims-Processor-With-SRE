package com.healthcare.claims.api.entitlements.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignUserToGroupCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Group ID is required")
    private UUID groupId;
}
