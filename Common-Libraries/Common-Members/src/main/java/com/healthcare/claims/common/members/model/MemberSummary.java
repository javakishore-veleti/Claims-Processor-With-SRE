package com.healthcare.claims.common.members.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSummary {

    private String id;
    private String memberId;
    private String fullName;
    private String dateOfBirth;
    private String policyNumber;
    private String policyStatus;
}
