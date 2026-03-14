package com.healthcare.claims.common.tenants.client;

import com.healthcare.claims.common.dto.ApiResponse;
import com.healthcare.claims.common.tenants.dto.TenantReqDTO;
import com.healthcare.claims.common.tenants.dto.TenantRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "api-tenants", url = "${claims.service-client.services.api-tenants.url:http://localhost:8085}")
public interface TenantsFeignClient {

    @PostMapping("/api/v1/tenants")
    ApiResponse<TenantRespDTO> createTenant(@RequestBody TenantReqDTO request);

    @GetMapping("/api/v1/tenants/{id}")
    ApiResponse<TenantRespDTO> getTenant(@PathVariable String id);

    @GetMapping("/api/v1/tenants")
    ApiResponse<List<TenantRespDTO>> searchTenants(@RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String status,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size);

    @PutMapping("/api/v1/tenants/{id}")
    ApiResponse<TenantRespDTO> updateTenant(@PathVariable String id, @RequestBody TenantReqDTO request);

    @PatchMapping("/api/v1/tenants/{id}/status")
    ApiResponse<TenantRespDTO> updateTenantStatus(@PathVariable String id, @RequestBody Map<String, String> statusUpdate);
}
