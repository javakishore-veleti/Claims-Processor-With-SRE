package com.healthcare.claims.common.tenants.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSummary {

    private String id;
    private String tenantId;
    private String name;
    private String status;
    private String plan;
}
