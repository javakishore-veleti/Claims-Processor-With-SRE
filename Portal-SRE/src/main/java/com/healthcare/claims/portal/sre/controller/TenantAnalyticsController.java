package com.healthcare.claims.portal.sre.controller;

import com.healthcare.claims.portal.sre.model.TenantUsageMetrics;
import com.healthcare.claims.portal.sre.service.SloComplianceService;
import com.healthcare.claims.portal.sre.service.TenantAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sre/tenant-analytics")
@RequiredArgsConstructor
public class TenantAnalyticsController {

    private final TenantAnalyticsService tenantAnalyticsService;
    private final SloComplianceService sloComplianceService;

    @GetMapping("/usage/{tenantId}")
    public ResponseEntity<List<TenantUsageMetrics>> getUsageMetrics(
            @PathVariable String tenantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();
        return ResponseEntity.ok(tenantAnalyticsService.getUsageMetrics(tenantId, startDate, endDate));
    }

    @GetMapping("/usage/top")
    public ResponseEntity<List<TenantUsageMetrics>> getTopTenantsByUsage(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(tenantAnalyticsService.getTopTenantsByUsage(limit));
    }

    @GetMapping("/health-score/{tenantId}")
    public ResponseEntity<Map<String, Object>> getTenantHealthScore(@PathVariable String tenantId) {
        return ResponseEntity.ok(tenantAnalyticsService.getTenantHealthScore(tenantId));
    }

    @GetMapping("/slo-compliance/{tenantId}")
    public ResponseEntity<?> getSloCompliance(
            @PathVariable String tenantId,
            @RequestParam(required = false) String period) {
        return ResponseEntity.ok(sloComplianceService.getComplianceReport(tenantId, period));
    }
}
