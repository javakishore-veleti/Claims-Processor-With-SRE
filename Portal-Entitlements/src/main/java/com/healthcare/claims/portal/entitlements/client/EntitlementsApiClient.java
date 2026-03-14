package com.healthcare.claims.portal.entitlements.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthcare.claims.common.client.ResilientServiceClient;
import com.healthcare.claims.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Client for the API-Entitlements microservice.
 * Provides user, group, role, and privilege management operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitlementsApiClient {

    private static final String SERVICE_NAME = "api-entitlements";
    private static final String BASE_PATH = "/api/v1/entitlements";

    private final ResilientServiceClient serviceClient;

    public ApiResponse<JsonNode> createUser(Map<String, Object> userData) {
        log.info("Creating user via API-Entitlements");
        return serviceClient.post(SERVICE_NAME, BASE_PATH + "/users", userData, JsonNode.class);
    }

    public ApiResponse<JsonNode> getUser(String id) {
        log.info("Fetching user {} from API-Entitlements", id);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/users/" + id, JsonNode.class);
    }

    public ApiResponse<JsonNode> getUserPrivileges(String userId) {
        log.info("Fetching privileges for user {} from API-Entitlements", userId);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/users/" + userId + "/privileges", JsonNode.class);
    }

    public ApiResponse<JsonNode> getUserGroups(String userId) {
        log.info("Fetching groups for user {} from API-Entitlements", userId);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/users/" + userId + "/groups", JsonNode.class);
    }

    public ApiResponse<JsonNode> assignUserToGroup(String userId, Map<String, Object> groupAssignment) {
        log.info("Assigning user {} to group via API-Entitlements", userId);
        return serviceClient.post(SERVICE_NAME, BASE_PATH + "/users/" + userId + "/groups", groupAssignment, JsonNode.class);
    }

    public ApiResponse<JsonNode> listRoles() {
        log.info("Listing all roles from API-Entitlements");
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/roles", JsonNode.class);
    }

    public ApiResponse<JsonNode> getDefaultGroup(String tenantId) {
        log.info("Fetching default group for tenant {} from API-Entitlements", tenantId);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/groups/default?tenantId=" + tenantId, JsonNode.class);
    }
}
