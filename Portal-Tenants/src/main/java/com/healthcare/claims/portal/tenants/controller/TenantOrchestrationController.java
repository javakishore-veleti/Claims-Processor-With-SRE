package com.healthcare.claims.portal.tenants.controller;

import com.healthcare.claims.common.dto.ApiResponse;
import com.healthcare.claims.common.tenants.client.TenantsFeignClient;
import com.healthcare.claims.common.tenants.dto.TenantReqDTO;
import com.healthcare.claims.common.tenants.dto.TenantRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Orchestrator controller for the Tenants portal.
 * Delegates tenant CRUD operations to API-Tenants via Feign client.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tenant-mgmt")
@RequiredArgsConstructor
public class TenantOrchestrationController {

    private final TenantsFeignClient tenantsClient;

    /**
     * Create a new tenant.
     */
    @PostMapping("/tenants")
    public ApiResponse<TenantRespDTO> createTenant(@RequestBody TenantReqDTO tenantData) {
        log.info("Creating new tenant");
        return tenantsClient.createTenant(tenantData);
    }

    /**
     * List all tenants.
     */
    @GetMapping("/tenants")
    public ApiResponse<List<TenantRespDTO>> listTenants() {
        log.info("Listing all tenants");
        return tenantsClient.searchTenants(null, null, 0, 100);
    }

    /**
     * Get a specific tenant by ID.
     */
    @GetMapping("/tenants/{id}")
    public ApiResponse<TenantRespDTO> getTenant(@PathVariable String id) {
        log.info("Fetching tenant {}", id);
        return tenantsClient.getTenant(id);
    }

    /**
     * Update a tenant.
     */
    @PutMapping("/tenants/{id}")
    public ApiResponse<TenantRespDTO> updateTenant(@PathVariable String id,
                                                    @RequestBody TenantReqDTO tenantData) {
        log.info("Updating tenant {}", id);
        return tenantsClient.updateTenant(id, tenantData);
    }
}
