package com.healthcare.claims.common.tenants.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantRespDTO {

    private String id;
    private String tenantId;
    private String name;
    private String displayName;
    private String domain;
    private String status;
    private String plan;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private Integer maxUsers;
    private String createdAt;
    private String updatedAt;
}
