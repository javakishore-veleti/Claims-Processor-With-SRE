package com.healthcare.claims.common.apps.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummary {

    private long totalClaims;
    private long claimsInProgress;
    private long claimsCompleted;
    private long claimsDenied;
    private long totalMembers;
    private long averageProcessingTimeMs;
}
