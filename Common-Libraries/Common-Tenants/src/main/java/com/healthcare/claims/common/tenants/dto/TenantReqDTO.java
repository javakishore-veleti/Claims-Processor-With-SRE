package com.healthcare.claims.common.tenants.dto;

import com.healthcare.claims.common.dto.BaseReqDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TenantReqDTO extends BaseReqDTO {

    @NotBlank(message = "Name is required")
    private String name;

    private String displayName;

    private String domain;

    private String plan;

    private String contactEmail;

    private String contactPhone;

    private String address;

    private Integer maxUsers;
}
