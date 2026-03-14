package com.healthcare.claims.portal.sre.controller;

import com.healthcare.claims.portal.sre.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sre/dashboard")
@RequiredArgsConstructor
public class SreDashboardController {

    private final IncidentService incidentService;
    private final ServiceHealthAggregator serviceHealthAggregator;
    private final SloComplianceService sloComplianceService;
    private final DeploymentService deploymentService;
    private final TenantAnalyticsService tenantAnalyticsService;
    private final CloudResourceService cloudResourceService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();

        // Active incidents
        dashboard.put("activeIncidents", incidentService.getActiveIncidents());

        // Service health overview
        dashboard.put("serviceHealth", serviceHealthAggregator.getOverview());

        // Global SLO compliance
        dashboard.put("sloCompliance", sloComplianceService.getGlobalComplianceSummary());

        // Recent deployments
        dashboard.put("recentDeployments", deploymentService.getRecentDeployments());

        // Top tenant usage
        dashboard.put("topTenantUsage", tenantAnalyticsService.getTopTenantsByUsage(5));

        // Cloud resource health
        dashboard.put("cloudResourceHealth", cloudResourceService.getResourceHealth());

        // MTTR statistics
        dashboard.put("mttr", incidentService.getMTTR());

        return ResponseEntity.ok(dashboard);
    }
}
