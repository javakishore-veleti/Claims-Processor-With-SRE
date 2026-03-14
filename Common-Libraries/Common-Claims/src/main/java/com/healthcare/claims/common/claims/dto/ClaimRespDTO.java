package com.healthcare.claims.common.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRespDTO {

    private String id;
    private String tenantId;
    private String customerId;
    private String claimNumber;
    private String stage;
    private String submittedDate;
    private List<String> documentIds;
    private Map<String, Object> extractedData;
    private String adjudicationResult;
    private String confidenceScore;
    private String createdAt;
    private String updatedAt;
}
