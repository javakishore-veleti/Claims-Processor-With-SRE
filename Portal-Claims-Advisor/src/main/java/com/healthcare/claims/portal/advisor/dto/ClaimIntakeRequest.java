package com.healthcare.claims.portal.advisor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Request DTO for the orchestrated claim intake flow.
 * Captures the information needed to look up a member, validate the tenant,
 * and create a new claim in a single orchestrated operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimIntakeRequest {

    private String memberId;
    private String tenantId;
    private String claimType;
    private String description;
    private BigDecimal amount;
    private String providerNpi;
    private String diagnosisCode;
    private String procedureCode;
    private String serviceDate;
    private Map<String, Object> additionalData;
}
