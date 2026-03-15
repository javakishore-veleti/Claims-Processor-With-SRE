package com.healthcare.claims.portal.advisor.controller;

import com.healthcare.claims.common.claims.client.ClaimsFeignClient;
import com.healthcare.claims.common.claims.dto.ClaimReqDTO;
import com.healthcare.claims.common.claims.dto.ClaimRespDTO;
import com.healthcare.claims.common.dto.ApiResponse;
import com.healthcare.claims.common.entitlements.client.EntitlementsFeignClient;
import com.healthcare.claims.common.members.client.MembersFeignClient;
import com.healthcare.claims.common.members.dto.MemberRespDTO;
import com.healthcare.claims.common.tenants.client.TenantsFeignClient;
import com.healthcare.claims.common.tenants.dto.TenantRespDTO;
import com.healthcare.claims.common.apps.model.ClaimIntakeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestrator controller for the Claims Advisor portal.
 * Composes calls to API-Claims, API-Members, API-Tenants, and API-Entitlements
 * to serve the Angular UI with aggregated views.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/advisor")
@RequiredArgsConstructor
public class ClaimsOrchestrationController {

    private final ClaimsFeignClient claimsClient;
    private final MembersFeignClient membersClient;
    private final TenantsFeignClient tenantsClient;
    private final EntitlementsFeignClient entitlementsClient;

    /**
     * Orchestrated claim intake: validates tenant, looks up the member,
     * then creates the claim via API-Claims.
     */
    @PostMapping("/claims/intake")
    public ApiResponse<?> intakeClaim(@RequestBody ClaimIntakeRequest request) {
        log.info("Starting claim intake for customer {} in tenant {}", request.getCustomerId(), request.getTenantId());

        // 1. Validate tenant
        ApiResponse<TenantRespDTO> tenantResponse = tenantsClient.getTenant(request.getTenantId());
        if (tenantResponse == null || tenantResponse.getData() == null) {
            return ApiResponse.error("Invalid tenant: " + request.getTenantId());
        }

        // 2. Lookup member
        ApiResponse<MemberRespDTO> memberResponse = membersClient.lookupMember(request.getCustomerId(), null);
        if (memberResponse == null || memberResponse.getData() == null) {
            return ApiResponse.error("Member not found: " + request.getCustomerId());
        }

        // 3. Create claim via API-Claims
        ClaimReqDTO claimReq = new ClaimReqDTO();
        claimReq.setCustomerId(request.getCustomerId());
        claimReq.setClaimNumber(request.getClaimNumber());
        claimReq.setStage(request.getStage());
        claimReq.setNotes(request.getNotes());

        ApiResponse<ClaimRespDTO> claimResponse = claimsClient.createClaim(claimReq);

        // 4. Return composed response
        Map<String, Object> result = new HashMap<>();
        result.put("claim", claimResponse != null ? claimResponse.getData() : null);
        result.put("member", memberResponse.getData());
        result.put("tenant", tenantResponse.getData());

        return ApiResponse.success(result, "Claim intake completed successfully");
    }

    /**
     * Full claim view: composes claim details with member and tenant information.
     */
    @GetMapping("/claims/{id}/full")
    public ApiResponse<?> getClaimFullView(@PathVariable String id) {
        log.info("Fetching full claim view for claim {}", id);

        // 1. Get claim from API-Claims
        ApiResponse<ClaimRespDTO> claimResponse = claimsClient.getClaim(id);
        if (claimResponse == null || claimResponse.getData() == null) {
            return ApiResponse.notFound("Claim not found: " + id);
        }

        ClaimRespDTO claimData = claimResponse.getData();

        // 2. Get member details from API-Members
        String customerId = claimData.getCustomerId();
        ApiResponse<MemberRespDTO> memberResponse = null;
        if (customerId != null) {
            memberResponse = membersClient.getMember(customerId);
        }

        // 3. Get tenant info from API-Tenants
        String tenantId = claimData.getTenantId();
        ApiResponse<TenantRespDTO> tenantResponse = null;
        if (tenantId != null) {
            tenantResponse = tenantsClient.getTenant(tenantId);
        }

        // 4. Return composed view
        Map<String, Object> fullView = new HashMap<>();
        fullView.put("claim", claimData);
        fullView.put("member", memberResponse != null ? memberResponse.getData() : null);
        fullView.put("tenant", tenantResponse != null ? tenantResponse.getData() : null);

        return ApiResponse.success(fullView, "Full claim view retrieved");
    }

    /**
     * Dashboard data: aggregates information from multiple services for the advisor dashboard.
     */
    @GetMapping("/dashboard")
    public ApiResponse<?> getDashboardData(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "default-tenant") String tenantId) {
        log.info("Fetching dashboard data for tenant {}", tenantId);

        Map<String, Object> dashboard = new HashMap<>();

        // Fetch claims (graceful fallback)
        try {
            ApiResponse<List<ClaimRespDTO>> claimsResponse = claimsClient.searchClaims(null, null, 0, 10);
            dashboard.put("recentClaims", claimsResponse != null ? claimsResponse.getData() : java.util.Collections.emptyList());
        } catch (Exception e) {
            log.warn("Failed to fetch claims for dashboard: {}", e.getMessage());
            dashboard.put("recentClaims", java.util.Collections.emptyList());
        }

        // Fetch tenant info (graceful fallback)
        try {
            ApiResponse<TenantRespDTO> tenantResponse = tenantsClient.getTenantByTenantId(tenantId);
            dashboard.put("tenant", tenantResponse != null ? tenantResponse.getData() : null);
        } catch (Exception e) {
            log.warn("Failed to fetch tenant info: {}", e.getMessage());
            dashboard.put("tenant", null);
        }

        dashboard.put("tenantId", tenantId);
        return ApiResponse.success(dashboard, "Dashboard data retrieved");
    }

    /**
     * List claims with optional filters. Used by the Angular Claims page.
     */
    @GetMapping("/claims")
    public ApiResponse<?> listClaims(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String stage,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Listing claims - customerId={}, stage={}, page={}, size={}", customerId, stage, page, size);
        try {
            return claimsClient.searchClaims(customerId, stage, page, size);
        } catch (Exception e) {
            log.warn("Failed to fetch claims from API-Claims: {}", e.getMessage());
            return ApiResponse.success(java.util.Collections.emptyList(), "API-Claims unavailable");
        }
    }

    /**
     * Search claims with optional query parameter.
     */
    @GetMapping("/claims/search")
    public ApiResponse<?> searchClaims(@RequestParam(required = false) String query) {
        log.info("Searching claims with query: {}", query);
        return claimsClient.searchClaims(query, null, 0, 20);
    }

    /**
     * List members with optional filters. Used by the Angular Members page.
     */
    @GetMapping("/members")
    public ApiResponse<?> listMembers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String memberId) {
        log.info("Listing members - firstName={}, lastName={}, memberId={}", firstName, lastName, memberId);
        try {
            return membersClient.searchMembers(firstName, lastName, memberId);
        } catch (Exception e) {
            log.warn("Failed to fetch members from API-Members: {}", e.getMessage());
            return ApiResponse.success(java.util.Collections.emptyList(), "API-Members unavailable");
        }
    }
}
