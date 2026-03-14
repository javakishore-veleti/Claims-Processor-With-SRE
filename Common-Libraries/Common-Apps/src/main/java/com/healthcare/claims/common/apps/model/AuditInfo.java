package com.healthcare.claims.common.apps.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditInfo {

    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
}
