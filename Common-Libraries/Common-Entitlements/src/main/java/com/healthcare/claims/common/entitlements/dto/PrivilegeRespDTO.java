package com.healthcare.claims.common.entitlements.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegeRespDTO {

    private String id;
    private String tenantId;
    private String name;
    private String description;
    private String resource;
    private String action;
}
