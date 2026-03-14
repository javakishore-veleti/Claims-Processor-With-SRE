package com.healthcare.claims.common.claims.dto;

import com.healthcare.claims.common.dto.BaseReqDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClaimStageUpdateReqDTO extends BaseReqDTO {

    @NotBlank(message = "Stage is required")
    private String stage;
}
