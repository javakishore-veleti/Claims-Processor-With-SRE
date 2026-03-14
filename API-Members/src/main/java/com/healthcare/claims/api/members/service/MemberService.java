package com.healthcare.claims.api.members.service;

import com.healthcare.claims.common.members.dto.MemberReqDTO;
import com.healthcare.claims.common.members.dto.MemberRespDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface MemberService {

    MemberRespDTO createMember(MemberReqDTO request);

    MemberRespDTO getMemberById(UUID id);

    Page<MemberRespDTO> searchMembers(String firstName, String lastName, String memberId,
                                       LocalDate dateOfBirth, String policyNumber, Pageable pageable);

    MemberRespDTO updateMember(UUID id, MemberReqDTO request);

    MemberRespDTO lookupMember(String memberId, String ssnLast4);
}
