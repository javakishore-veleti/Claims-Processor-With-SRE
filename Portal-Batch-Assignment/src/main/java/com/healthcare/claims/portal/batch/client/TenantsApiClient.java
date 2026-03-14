package com.healthcare.claims.portal.batch.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthcare.claims.common.client.ResilientServiceClient;
import com.healthcare.claims.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Client for the API-Tenants microservice.
 * Provides tenant validation for batch member import operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantsApiClient {

    private static final String SERVICE_NAME = "api-tenants";
    private static final String BASE_PATH = "/api/v1/tenants";

    private final ResilientServiceClient serviceClient;

    public ApiResponse<JsonNode> getTenant(String id) {
        log.info("Fetching tenant {} from API-Tenants", id);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/" + id, JsonNode.class);
    }

    public ApiResponse<JsonNode> validateTenant(String tenantId) {
        log.info("Validating tenant {} via API-Tenants", tenantId);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/" + tenantId, JsonNode.class);
    }
}
