package com.healthcare.claims.common.entitlements.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRespDTO {

    private String id;
    private String tenantId;
    private String name;
    private String description;
    private boolean systemRole;
    private String createdAt;
    private String updatedAt;
}
