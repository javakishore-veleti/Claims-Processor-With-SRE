package com.healthcare.claims.portal.entitlements.controller;

import com.healthcare.claims.common.dto.ApiResponse;
import com.healthcare.claims.common.entitlements.client.EntitlementsFeignClient;
import com.healthcare.claims.common.entitlements.dto.UserEntitlementRespDTO;
import com.healthcare.claims.common.entitlements.dto.UserReqDTO;
import com.healthcare.claims.common.entitlements.dto.UserRespDTO;
import com.healthcare.claims.common.tenants.client.TenantsFeignClient;
import com.healthcare.claims.common.tenants.dto.TenantRespDTO;
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
 * Orchestrator controller for the Entitlements portal.
 * Composes calls to API-Entitlements and API-Tenants to manage
 * users, groups, roles, and privileges.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/entitlement-mgmt")
@RequiredArgsConstructor
public class EntitlementOrchestrationController {

    private final EntitlementsFeignClient entitlementsClient;
    private final TenantsFeignClient tenantsClient;

    /**
     * Create a user and auto-assign to the tenant's default group.
     */
    @PostMapping("/users")
    public ApiResponse<?> createUser(@RequestBody UserReqDTO userData,
                                     @RequestHeader("X-Tenant-Id") String tenantId) {
        log.info("Creating user for tenant {}", tenantId);

        // 1. Validate tenant
        ApiResponse<TenantRespDTO> tenantResponse = tenantsClient.getTenant(tenantId);
        if (tenantResponse == null || tenantResponse.getData() == null) {
            return ApiResponse.error("Invalid tenant: " + tenantId);
        }

        // 2. Create user via API-Entitlements
        userData.setTenantId(tenantId);
        ApiResponse<UserRespDTO> userResponse = entitlementsClient.createUser(userData);

        if (userResponse == null || userResponse.getData() == null) {
            return ApiResponse.error("Failed to create user");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("user", userResponse.getData());
        result.put("tenant", tenantResponse.getData());

        return ApiResponse.created(result);
    }

    /**
     * Get effective privileges for a user (combines user privileges and group privileges).
     */
    @GetMapping("/users/{id}/effective-privileges")
    public ApiResponse<?> getEffectivePrivileges(@PathVariable String id) {
        log.info("Fetching effective privileges for user {}", id);

        ApiResponse<UserEntitlementRespDTO> entitlements = entitlementsClient.getUserEntitlements(id);

        Map<String, Object> result = new HashMap<>();
        result.put("entitlements", entitlements != null ? entitlements.getData() : null);

        return ApiResponse.success(result, "Effective privileges retrieved");
    }

    /**
     * List all users.
     */
    @GetMapping("/users")
    public ApiResponse<?> listUsers(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "default-tenant") String tenantId) {
        log.info("Listing users for tenant {}", tenantId);
        try {
            return entitlementsClient.searchUsers(null, null, 0, 100);
        } catch (Exception e) {
            log.warn("Failed to fetch users from API-Entitlements: {}", e.getMessage());
            return ApiResponse.success(java.util.Collections.emptyList(), "API-Entitlements unavailable");
        }
    }

    /**
     * List all roles.
     */
    @GetMapping("/roles")
    public ApiResponse<?> listRoles() {
        log.info("Listing all roles");
        try {
            return entitlementsClient.listRoles();
        } catch (Exception e) {
            log.warn("Failed to fetch roles from API-Entitlements: {}", e.getMessage());
            return ApiResponse.success(java.util.Collections.emptyList(), "API-Entitlements unavailable");
        }
    }

    /**
     * List all privileges.
     */
    @GetMapping("/privileges")
    public ApiResponse<?> listPrivileges() {
        log.info("Listing all privileges");
        try {
            return entitlementsClient.listPrivileges();
        } catch (Exception e) {
            log.warn("Failed to fetch privileges from API-Entitlements: {}", e.getMessage());
            return ApiResponse.success(java.util.Collections.emptyList(), "API-Entitlements unavailable");
        }
    }
}
