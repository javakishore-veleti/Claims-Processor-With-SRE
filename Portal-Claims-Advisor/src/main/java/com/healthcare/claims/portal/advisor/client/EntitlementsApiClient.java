package com.healthcare.claims.portal.advisor.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthcare.claims.common.client.ResilientServiceClient;
import com.healthcare.claims.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Client for the API-Entitlements microservice.
 * Provides privilege checking and access validation for the advisor portal.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitlementsApiClient {

    private static final String SERVICE_NAME = "api-entitlements";
    private static final String BASE_PATH = "/api/v1/entitlements";

    private final ResilientServiceClient serviceClient;

    public ApiResponse<JsonNode> getCurrentUserPrivileges(String userId) {
        log.info("Fetching privileges for user {} from API-Entitlements", userId);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/users/" + userId + "/privileges", JsonNode.class);
    }

    public ApiResponse<JsonNode> validateAccess(String userId, String resource, String action) {
        log.info("Validating access for user {} on resource {} action {}", userId, resource, action);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/users/" + userId + "/access?resource=" + resource + "&action=" + action, JsonNode.class);
    }
}
