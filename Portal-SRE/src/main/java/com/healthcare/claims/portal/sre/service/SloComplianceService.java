package com.healthcare.claims.portal.sre.service;

import com.healthcare.claims.portal.sre.model.TenantSloCompliance;
import com.healthcare.claims.portal.sre.model.TenantUsageMetrics;
import com.healthcare.claims.portal.sre.repository.TenantSloComplianceRepository;
import com.healthcare.claims.portal.sre.repository.TenantUsageMetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SloComplianceService {

    private final TenantSloComplianceRepository sloComplianceRepository;
    private final TenantUsageMetricsRepository usageMetricsRepository;

    /**
     * Measure current SLO compliance for a tenant based on recent usage metrics.
     * Creates new compliance records for standard SLOs.
     */
    public List<TenantSloCompliance> measureCompliance(String tenantId) {
        log.info("Measuring SLO compliance for tenant: {}", tenantId);

        List<TenantUsageMetrics> recentMetrics = usageMetricsRepository
                .findByTenantIdOrderByMetricDateDesc(tenantId);

        if (recentMetrics.isEmpty()) {
            log.warn("No usage metrics available for tenant: {}", tenantId);
            return Collections.emptyList();
        }

        TenantUsageMetrics latest = recentMetrics.get(0);
        List<TenantSloCompliance> results = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // SLO: Availability (error rate < 0.1%)
        double errorRate = latest.getApiCallCount() > 0
                ? (double) latest.getErrorCount() / latest.getApiCallCount() * 100
                : 0;
        double availability = 100 - errorRate;
        results.add(TenantSloCompliance.builder()
                .tenantId(tenantId)
                .sloName("Availability")
                .targetValue(99.9)
                .actualValue(Math.round(availability * 1000.0) / 1000.0)
                .compliant(availability >= 99.9)
                .measurementPeriod("30d")
                .measuredAt(now)
                .build());

        // SLO: Latency P50 < 200ms
        double avgLatency = latest.getAvgResponseTimeMs() != null ? latest.getAvgResponseTimeMs() : 0;
        results.add(TenantSloCompliance.builder()
                .tenantId(tenantId)
                .sloName("Latency P50")
                .targetValue(200.0)
                .actualValue(avgLatency)
                .compliant(avgLatency <= 200.0)
                .measurementPeriod("30d")
                .measuredAt(now)
                .build());

        // SLO: Claims processing throughput > 100/day
        long claimsProcessed = latest.getClaimsProcessed() != null ? latest.getClaimsProcessed() : 0;
        results.add(TenantSloCompliance.builder()
                .tenantId(tenantId)
                .sloName("Claims Throughput")
                .targetValue(100.0)
                .actualValue((double) claimsProcessed)
                .compliant(claimsProcessed >= 100)
                .measurementPeriod("1d")
                .measuredAt(now)
                .build());

        return sloComplianceRepository.saveAll(results);
    }

    /**
     * Get SLO compliance report for a tenant filtered by measurement period.
     */
    public List<TenantSloCompliance> getComplianceReport(String tenantId, String period) {
        log.info("Fetching SLO compliance report for tenant: {} period: {}", tenantId, period);
        if (period != null && !period.isEmpty()) {
            return sloComplianceRepository.findByTenantIdAndMeasurementPeriod(tenantId, period);
        }
        return sloComplianceRepository.findByTenantIdOrderByMeasuredAtDesc(tenantId);
    }

    /**
     * Get global SLO compliance summary across all tenants.
     */
    public Map<String, Object> getGlobalComplianceSummary() {
        log.info("Calculating global SLO compliance summary");

        Map<String, Object> summary = new LinkedHashMap<>();

        Object[] globalCounts = sloComplianceRepository.getGlobalComplianceSummary();
        if (globalCounts != null && globalCounts[0] != null) {
            long total = (Long) globalCounts[0];
            long compliant = globalCounts[1] != null ? (Long) globalCounts[1] : 0;
            double complianceRate = total > 0 ? (double) compliant / total * 100 : 0;

            summary.put("totalSloChecks", total);
            summary.put("compliantChecks", compliant);
            summary.put("nonCompliantChecks", total - compliant);
            summary.put("globalComplianceRate", Math.round(complianceRate * 100.0) / 100.0);
            summary.put("status", complianceRate >= 99 ? "EXCELLENT"
                    : complianceRate >= 95 ? "GOOD"
                    : complianceRate >= 90 ? "WARNING"
                    : "CRITICAL");
        } else {
            summary.put("totalSloChecks", 0);
            summary.put("compliantChecks", 0);
            summary.put("nonCompliantChecks", 0);
            summary.put("globalComplianceRate", 0);
            summary.put("status", "NO_DATA");
        }

        // Per-tenant breakdown
        List<Object[]> tenantBreakdown = sloComplianceRepository.getComplianceSummaryByTenant();
        List<Map<String, Object>> tenantSummaries = new ArrayList<>();
        for (Object[] row : tenantBreakdown) {
            Map<String, Object> tenantSummary = new LinkedHashMap<>();
            tenantSummary.put("tenantId", row[0]);
            long total = (Long) row[1];
            long compliant = row[2] != null ? (Long) row[2] : 0;
            tenantSummary.put("totalChecks", total);
            tenantSummary.put("compliantChecks", compliant);
            tenantSummary.put("complianceRate", total > 0 ? Math.round((double) compliant / total * 10000.0) / 100.0 : 0);
            tenantSummaries.add(tenantSummary);
        }
        summary.put("tenantBreakdown", tenantSummaries);

        // Non-compliant SLOs
        List<TenantSloCompliance> violations = sloComplianceRepository.findByCompliantFalseOrderByMeasuredAtDesc();
        summary.put("currentViolations", violations);

        return summary;
    }
}
