package com.healthcare.claims.portal.batch.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthcare.claims.common.client.ResilientServiceClient;
import com.healthcare.claims.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Client for the API-Members microservice.
 * Supports batch member creation and bulk operations for the batch assignment portal.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MembersApiClient {

    private static final String SERVICE_NAME = "api-members";
    private static final String BASE_PATH = "/api/v1/members";

    private final ResilientServiceClient serviceClient;

    public ApiResponse<JsonNode> createMember(Map<String, Object> memberData) {
        log.info("Creating member via API-Members");
        return serviceClient.post(SERVICE_NAME, BASE_PATH, memberData, JsonNode.class);
    }

    public ApiResponse<JsonNode> createMembersBulk(List<Map<String, Object>> members) {
        log.info("Creating {} members in bulk via API-Members", members.size());
        return serviceClient.post(SERVICE_NAME, BASE_PATH + "/bulk", members, JsonNode.class);
    }

    public ApiResponse<JsonNode> getMember(String id) {
        log.info("Fetching member {} from API-Members", id);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/" + id, JsonNode.class);
    }
}
