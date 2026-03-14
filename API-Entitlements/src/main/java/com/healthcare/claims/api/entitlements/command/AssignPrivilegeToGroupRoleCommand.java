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
public class AssignPrivilegeToGroupRoleCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Group Role ID is required")
    private UUID groupRoleId;

    @NotNull(message = "Privilege ID is required")
    private UUID privilegeId;
}
