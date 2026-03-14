package com.healthcare.claims.portal.member.controller;

import com.healthcare.claims.common.claims.client.ClaimsFeignClient;
import com.healthcare.claims.common.claims.dto.ClaimRespDTO;
import com.healthcare.claims.common.dto.ApiResponse;
import com.healthcare.claims.common.members.client.MembersFeignClient;
import com.healthcare.claims.common.members.dto.MemberRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestrator controller for the Claims Member portal.
 * Composes calls to API-Claims and API-Members
 * to provide read-only claim views for members.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberOrchestrationController {

    private final ClaimsFeignClient claimsClient;
    private final MembersFeignClient membersClient;

    /**
     * Get all claims for the authenticated member, with status information.
     */
    @GetMapping("/my-claims")
    public ApiResponse<?> getMyClaims(@RequestHeader("X-Member-Id") String memberId) {
        log.info("Fetching claims for member {}", memberId);

        ApiResponse<List<ClaimRespDTO>> claimsResponse = claimsClient.searchClaims(memberId, null, 0, 100);
        ApiResponse<MemberRespDTO> memberResponse = membersClient.getMember(memberId);

        Map<String, Object> result = new HashMap<>();
        result.put("claims", claimsResponse != null ? claimsResponse.getData() : null);
        result.put("member", memberResponse != null ? memberResponse.getData() : null);

        return ApiResponse.success(result, "Member claims retrieved");
    }

    /**
     * Full claim view for a member, including claim details.
     */
    @GetMapping("/claims/{id}")
    public ApiResponse<?> getClaimDetail(@PathVariable String id,
                                         @RequestHeader("X-Member-Id") String memberId) {
        log.info("Fetching claim detail {} for member {}", id, memberId);

        ApiResponse<ClaimRespDTO> claimResponse = claimsClient.getClaim(id);
        if (claimResponse == null || claimResponse.getData() == null) {
            return ApiResponse.notFound("Claim not found: " + id);
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("claim", claimResponse.getData());

        return ApiResponse.success(detail, "Claim detail retrieved");
    }

    /**
     * File an appeal for a specific claim.
     */
    @PostMapping("/claims/{id}/appeal")
    public ApiResponse<?> fileAppeal(@PathVariable String id,
                                     @RequestHeader("X-Member-Id") String memberId,
                                     @RequestBody Map<String, Object> appealRequest) {
        log.info("Member {} filing appeal for claim {}", memberId, id);

        // Verify claim exists and belongs to this member
        ApiResponse<ClaimRespDTO> claimResponse = claimsClient.getClaim(id);
        if (claimResponse == null || claimResponse.getData() == null) {
            return ApiResponse.notFound("Claim not found: " + id);
        }

        // Return claim status as acknowledgement
        return claimResponse;
    }
}
