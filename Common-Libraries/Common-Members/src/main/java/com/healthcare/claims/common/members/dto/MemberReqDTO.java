package com.healthcare.claims.common.members.dto;

import com.healthcare.claims.common.dto.BaseReqDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MemberReqDTO extends BaseReqDTO {

    @NotBlank(message = "Member ID is required")
    private String memberId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String dateOfBirth;

    private String email;

    private String phone;

    private String address;

    private String policyNumber;

    private String policyStatus;

    private String ssnLast4;
}
