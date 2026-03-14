package com.healthcare.claims.common.apps.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimIntakeRequest {

    private String tenantId;
    private String customerId;
    private String claimNumber;
    private String stage;
    private List<FileUploadInfo> files;
    private String notes;
}
