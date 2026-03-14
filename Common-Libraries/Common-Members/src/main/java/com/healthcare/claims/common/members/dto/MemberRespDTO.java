package com.healthcare.claims.common.members.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRespDTO {

    private String id;
    private String tenantId;
    private String memberId;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String email;
    private String phone;
    private String address;
    private String policyNumber;
    private String policyStatus;
    private String createdAt;
    private String updatedAt;
}
