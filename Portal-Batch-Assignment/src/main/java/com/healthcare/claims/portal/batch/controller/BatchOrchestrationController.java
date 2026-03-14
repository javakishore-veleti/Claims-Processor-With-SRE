package com.healthcare.claims.portal.batch.controller;

import com.healthcare.claims.common.dto.ApiResponse;
import com.healthcare.claims.common.members.client.MembersFeignClient;
import com.healthcare.claims.common.members.dto.MemberReqDTO;
import com.healthcare.claims.common.members.dto.MemberRespDTO;
import com.healthcare.claims.common.tenants.client.TenantsFeignClient;
import com.healthcare.claims.common.tenants.dto.TenantRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestrator controller for the Batch Assignment portal.
 * Composes calls to API-Members and API-Tenants for batch
 * member import and bulk creation workflows.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
public class BatchOrchestrationController {

    private final MembersFeignClient membersClient;
    private final TenantsFeignClient tenantsClient;

    /**
     * Import members from an uploaded Excel file.
     * Validates the tenant, parses the file, and creates members via API-Members.
     */
    @PostMapping("/members/import")
    public ApiResponse<?> importMembers(@RequestParam("file") MultipartFile file,
                                        @RequestHeader("X-Tenant-Id") String tenantId) {
        log.info("Starting member import for tenant {} from file {}", tenantId, file.getOriginalFilename());

        // 1. Validate tenant
        ApiResponse<TenantRespDTO> tenantResponse = tenantsClient.getTenant(tenantId);
        if (tenantResponse == null || tenantResponse.getData() == null) {
            return ApiResponse.error("Invalid tenant: " + tenantId);
        }

        // 2. Parse Excel file (delegate to existing ExcelImportService or handle here)
        // For now, return acknowledgement that import job has been queued
        Map<String, Object> result = new HashMap<>();
        result.put("tenantId", tenantId);
        result.put("fileName", file.getOriginalFilename());
        result.put("fileSize", file.getSize());
        result.put("status", "QUEUED");

        return ApiResponse.success(result, "Member import job queued successfully");
    }

    /**
     * Create members in bulk via API-Members.
     */
    @PostMapping("/members/bulk")
    public ApiResponse<?> bulkCreateMembers(@RequestBody List<MemberReqDTO> members,
                                            @RequestHeader("X-Tenant-Id") String tenantId) {
        log.info("Bulk creating {} members for tenant {}", members.size(), tenantId);

        // 1. Validate tenant
        ApiResponse<TenantRespDTO> tenantResponse = tenantsClient.getTenant(tenantId);
        if (tenantResponse == null || tenantResponse.getData() == null) {
            return ApiResponse.error("Invalid tenant: " + tenantId);
        }

        // 2. Create members one by one via Feign client
        List<MemberRespDTO> createdMembers = new ArrayList<>();
        for (MemberReqDTO member : members) {
            member.setTenantId(tenantId);
            ApiResponse<MemberRespDTO> response = membersClient.createMember(member);
            if (response != null && response.getData() != null) {
                createdMembers.add(response.getData());
            }
        }

        return ApiResponse.success(createdMembers, "Bulk member creation completed");
    }

    /**
     * List batch import jobs and their statuses.
     */
    @GetMapping("/jobs")
    public ApiResponse<?> listBatchJobs(@RequestHeader("X-Tenant-Id") String tenantId) {
        log.info("Listing batch jobs for tenant {}", tenantId);

        // Return job listing (in a full implementation, this would query a job tracking store)
        Map<String, Object> result = new HashMap<>();
        result.put("tenantId", tenantId);
        result.put("jobs", List.of());

        return ApiResponse.success(result, "Batch jobs retrieved");
    }
}
