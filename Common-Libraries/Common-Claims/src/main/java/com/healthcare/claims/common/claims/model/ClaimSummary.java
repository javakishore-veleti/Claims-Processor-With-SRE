package com.healthcare.claims.common.claims.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSummary {

    private String id;
    private String claimNumber;
    private String stage;
    private String submittedDate;
    private String customerName;
    private String providerName;
    private String billedAmount;
    private String status;
}
