package com.healthcare.claims.portal.member.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthcare.claims.common.client.ResilientServiceClient;
import com.healthcare.claims.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Client for the API-Claims microservice (read-only for member portal).
 * Members can view their own claims but cannot create or modify them.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimsApiClient {

    private static final String SERVICE_NAME = "api-claims";
    private static final String BASE_PATH = "/api/v1/claims";

    private final ResilientServiceClient serviceClient;

    public ApiResponse<JsonNode> getMyClaims(String memberId) {
        log.info("Fetching claims for member {} from API-Claims", memberId);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "?memberId=" + memberId, JsonNode.class);
    }

    public ApiResponse<JsonNode> getClaimStatus(String claimId) {
        log.info("Fetching status for claim {} from API-Claims", claimId);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/" + claimId + "/status", JsonNode.class);
    }

    public ApiResponse<JsonNode> getClaimTimeline(String claimId) {
        log.info("Fetching timeline for claim {} from API-Claims", claimId);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/" + claimId + "/timeline", JsonNode.class);
    }

    public ApiResponse<JsonNode> getClaim(String claimId) {
        log.info("Fetching claim {} from API-Claims", claimId);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/" + claimId, JsonNode.class);
    }
}
