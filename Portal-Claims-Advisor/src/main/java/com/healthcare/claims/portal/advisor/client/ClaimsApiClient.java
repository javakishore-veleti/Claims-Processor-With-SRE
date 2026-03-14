package com.healthcare.claims.portal.advisor.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthcare.claims.common.client.ResilientServiceClient;
import com.healthcare.claims.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Client for the API-Claims microservice.
 * Provides claim creation, retrieval, stage transitions, and search.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimsApiClient {

    private static final String SERVICE_NAME = "api-claims";
    private static final String BASE_PATH = "/api/v1/claims";

    private final ResilientServiceClient serviceClient;

    public ApiResponse<JsonNode> createClaim(Map<String, Object> request) {
        log.info("Creating claim via API-Claims");
        return serviceClient.post(SERVICE_NAME, BASE_PATH, request, JsonNode.class);
    }

    public ApiResponse<JsonNode> getClaim(String id) {
        log.info("Fetching claim {} from API-Claims", id);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/" + id, JsonNode.class);
    }

    public ApiResponse<JsonNode> updateClaimStage(String id, String stage) {
        log.info("Updating claim {} to stage {} via API-Claims", id, stage);
        Map<String, String> body = Map.of("stage", stage);
        return serviceClient.post(SERVICE_NAME, BASE_PATH + "/" + id + "/stage", body, JsonNode.class);
    }

    public ApiResponse<JsonNode> searchClaims(String query) {
        log.info("Searching claims with query: {}", query);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/search?q=" + query, JsonNode.class);
    }

    public ApiResponse<JsonNode> listClaims(int page, int size) {
        log.info("Listing claims page={} size={}", page, size);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "?page=" + page + "&size=" + size, JsonNode.class);
    }

    public ApiResponse<JsonNode> listClaimsByTenant(String tenantId, int page, int size) {
        log.info("Listing claims for tenant {} page={} size={}", tenantId, page, size);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "?tenantId=" + tenantId + "&page=" + page + "&size=" + size, JsonNode.class);
    }
}
