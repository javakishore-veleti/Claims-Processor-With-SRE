package com.healthcare.claims.api.claims.service;

import com.healthcare.claims.common.claims.dto.ClaimReqDTO;
import com.healthcare.claims.common.claims.dto.ClaimRespDTO;
import com.healthcare.claims.api.claims.model.ClaimStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface ClaimService {

    ClaimRespDTO createClaim(ClaimReqDTO request);

    ClaimRespDTO getClaimById(UUID id);

    Page<ClaimRespDTO> searchClaims(String customerId, ClaimStage stage,
                                     LocalDate fromDate, LocalDate toDate,
                                     Pageable pageable);

    ClaimRespDTO updateClaim(UUID id, ClaimReqDTO request);

    ClaimRespDTO updateClaimStage(UUID id, ClaimStage stage);
}
