package com.healthcare.claims.portal.sre.controller;

import com.healthcare.claims.portal.sre.model.CloudResource;
import com.healthcare.claims.portal.sre.service.CloudResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sre/cloud-resources")
@RequiredArgsConstructor
public class CloudResourceController {

    private final CloudResourceService cloudResourceService;

    @GetMapping
    public ResponseEntity<List<CloudResource>> getAllResources() {
        return ResponseEntity.ok(cloudResourceService.discoverResources(null));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getResourceHealth() {
        return ResponseEntity.ok(cloudResourceService.getResourceHealth());
    }

    @GetMapping("/by-tenant/{tenantId}")
    public ResponseEntity<List<CloudResource>> getResourcesByTenant(@PathVariable String tenantId) {
        return ResponseEntity.ok(cloudResourceService.getResourcesByTenant(tenantId));
    }

    @GetMapping("/cost-estimate/{tenantId}")
    public ResponseEntity<Map<String, Object>> getCostEstimate(@PathVariable String tenantId) {
        return ResponseEntity.ok(cloudResourceService.estimateMonthlyCost(tenantId));
    }

    @PostMapping("/discover")
    public ResponseEntity<List<CloudResource>> discoverResources(
            @RequestParam CloudResource.CloudProvider provider) {
        return ResponseEntity.ok(cloudResourceService.discoverResources(provider));
    }
}
