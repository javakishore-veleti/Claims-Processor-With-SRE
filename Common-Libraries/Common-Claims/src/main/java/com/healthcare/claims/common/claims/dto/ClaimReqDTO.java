package com.healthcare.claims.common.claims.dto;

import com.healthcare.claims.common.dto.BaseReqDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClaimReqDTO extends BaseReqDTO {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Claim number is required")
    private String claimNumber;

    private String stage;

    private String submittedDate;

    private List<String> documentIds;

    private Map<String, Object> extractedData;

    private String notes;
}
