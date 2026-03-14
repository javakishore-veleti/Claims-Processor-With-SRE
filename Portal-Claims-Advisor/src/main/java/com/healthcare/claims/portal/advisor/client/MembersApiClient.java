package com.healthcare.claims.portal.advisor.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthcare.claims.common.client.ResilientServiceClient;
import com.healthcare.claims.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Client for the API-Members microservice.
 * Provides member lookup, search, and retrieval for claim intake workflows.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MembersApiClient {

    private static final String SERVICE_NAME = "api-members";
    private static final String BASE_PATH = "/api/v1/members";

    private final ResilientServiceClient serviceClient;

    public ApiResponse<JsonNode> getMember(String id) {
        log.info("Fetching member {} from API-Members", id);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/" + id, JsonNode.class);
    }

    public ApiResponse<JsonNode> searchMembers(String query) {
        log.info("Searching members with query: {}", query);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/search?q=" + query, JsonNode.class);
    }

    public ApiResponse<JsonNode> lookupMember(String memberId) {
        log.info("Looking up member by memberId: {}", memberId);
        return serviceClient.get(SERVICE_NAME, BASE_PATH + "/lookup?memberId=" + memberId, JsonNode.class);
    }
}
