package com.healthcare.claims.portal.tenants.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthcare.claims.common.client.ResilientServiceClient;
import com.healthcare.claims.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Client for the API-Tenants microservice.
 * Provides full CRUD operations for tenant management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantsApiClient {

    private static final String SERVICE_NAME = "api-tenants";
    private static final String BASE_PATH = "/api/v1/tenants";

    private final ResilientServiceClient serviceClient;

    public ApiResponse<JsonNode> createTenant(Map<String, Object> tenantData) {
        log.info("Creating tenant via API-Tenants");
        return serviceClient.post(SERVICE_NAME, BASE_PATH, tenantData, JsonNode.class);
    }

    public ApiResponse<JsonNode> getTenant(String id) {
        log.info("Fetching tenant {} from API-Tenants", id);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/" + id, JsonNode.class);
    }

    public ApiResponse<JsonNode> listTenants() {
        log.info("Listing all tenants from API-Tenants");
        return serviceClient.get(SERVICE_NAME, BASE_PATH, JsonNode.class);
    }

    public ApiResponse<JsonNode> updateTenant(String id, Map<String, Object> tenantData) {
        log.info("Updating tenant {} via API-Tenants", id);
        return serviceClient.put(SERVICE_NAME, BASE_PATH + "/" + id, tenantData, JsonNode.class);
    }

    public ApiResponse<JsonNode> deleteTenant(String id) {
        log.info("Deleting tenant {} via API-Tenants", id);
        return serviceClient.delete(SERVICE_NAME, BASE_PATH + "/" + id, JsonNode.class);
    }
}
