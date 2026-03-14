package com.healthcare.claims.common.claims.client;

import com.healthcare.claims.common.claims.dto.ClaimReqDTO;
import com.healthcare.claims.common.claims.dto.ClaimRespDTO;
import com.healthcare.claims.common.claims.dto.ClaimStageUpdateReqDTO;
import com.healthcare.claims.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "api-claims", url = "${claims.service-client.services.api-claims.url:http://localhost:8083}")
public interface ClaimsFeignClient {

    @PostMapping("/api/v1/claims")
    ApiResponse<ClaimRespDTO> createClaim(@RequestBody ClaimReqDTO request);

    @GetMapping("/api/v1/claims/{id}")
    ApiResponse<ClaimRespDTO> getClaim(@PathVariable String id);

    @GetMapping("/api/v1/claims")
    ApiResponse<List<ClaimRespDTO>> searchClaims(@RequestParam(required = false) String customerId,
                                                  @RequestParam(required = false) String stage,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size);

    @PutMapping("/api/v1/claims/{id}")
    ApiResponse<ClaimRespDTO> updateClaim(@PathVariable String id, @RequestBody ClaimReqDTO request);

    @PatchMapping("/api/v1/claims/{id}/stage")
    ApiResponse<ClaimRespDTO> updateClaimStage(@PathVariable String id, @RequestBody ClaimStageUpdateReqDTO request);
}
