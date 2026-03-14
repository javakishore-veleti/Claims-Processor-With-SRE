package com.healthcare.claims.portal.sre.service;

import com.healthcare.claims.portal.sre.model.TenantSloCompliance;
import com.healthcare.claims.portal.sre.model.TenantUsageMetrics;
import com.healthcare.claims.portal.sre.repository.TenantSloComplianceRepository;
import com.healthcare.claims.portal.sre.repository.TenantUsageMetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantAnalyticsService {

    private final TenantUsageMetricsRepository usageMetricsRepository;
    private final TenantSloComplianceRepository sloComplianceRepository;

    /**
     * Get usage metrics for a tenant within a date range.
     */
    public List<TenantUsageMetrics> getUsageMetrics(String tenantId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching usage metrics for tenant {} from {} to {}", tenantId, startDate, endDate);
        return usageMetricsRepository.findByTenantIdAndMetricDateBetweenOrderByMetricDateAsc(
                tenantId, startDate, endDate);
    }

    /**
     * Get top tenants by usage (API call count) for a given date.
     */
    public List<TenantUsageMetrics> getTopTenantsByUsage(int limit) {
        log.info("Fetching top {} tenants by usage", limit);
        LocalDate today = LocalDate.now();
        List<TenantUsageMetrics> allMetrics = usageMetricsRepository.findTopTenantsByApiCallCount(today);
        if (allMetrics.isEmpty()) {
            // Fallback to most recent date with data
            allMetrics = usageMetricsRepository.findTopTenantsByApiCallCount(today.minusDays(1));
        }
        return allMetrics.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Calculate a composite health score for a tenant (0-100).
     * Based on error rate, average response time, and SLO compliance.
     */
    public Map<String, Object> getTenantHealthScore(String tenantId) {
        log.info("Calculating health score for tenant {}", tenantId);

        Map<String, Object> healthScore = new LinkedHashMap<>();
        healthScore.put("tenantId", tenantId);

        // Get recent usage metrics
        List<TenantUsageMetrics> recentMetrics = usageMetricsRepository
                .findByTenantIdOrderByMetricDateDesc(tenantId);

        if (recentMetrics.isEmpty()) {
            healthScore.put("score", 0);
            healthScore.put("status", "NO_DATA");
            healthScore.put("details", "No usage metrics available for this tenant");
            return healthScore;
        }

        TenantUsageMetrics latest = recentMetrics.get(0);

        // Error rate score (0-40 points)
        double errorRate = latest.getApiCallCount() > 0
                ? (double) latest.getErrorCount() / latest.getApiCallCount() * 100
                : 0;
        int errorScore = errorRate < 0.1 ? 40 : errorRate < 1.0 ? 30 : errorRate < 5.0 ? 20 : errorRate < 10.0 ? 10 : 0;

        // Latency score (0-30 points)
        double avgLatency = latest.getAvgResponseTimeMs() != null ? latest.getAvgResponseTimeMs() : 0;
        int latencyScore = avgLatency < 100 ? 30 : avgLatency < 250 ? 25 : avgLatency < 500 ? 15 : avgLatency < 1000 ? 5 : 0;

        // SLO compliance score (0-30 points)
        List<TenantSloCompliance> sloRecords = sloComplianceRepository
                .findByTenantIdOrderByMeasuredAtDesc(tenantId);
        long totalSlos = sloRecords.size();
        long compliantSlos = sloRecords.stream().filter(TenantSloCompliance::isCompliant).count();
        double complianceRate = totalSlos > 0 ? (double) compliantSlos / totalSlos * 100 : 100;
        int sloScore = complianceRate >= 99 ? 30 : complianceRate >= 95 ? 25 : complianceRate >= 90 ? 15 : complianceRate >= 80 ? 5 : 0;

        int totalScore = errorScore + latencyScore + sloScore;

        healthScore.put("score", totalScore);
        healthScore.put("status", totalScore >= 80 ? "HEALTHY" : totalScore >= 50 ? "DEGRADED" : "CRITICAL");
        healthScore.put("errorRate", Math.round(errorRate * 100.0) / 100.0);
        healthScore.put("errorScore", errorScore);
        healthScore.put("avgLatencyMs", avgLatency);
        healthScore.put("latencyScore", latencyScore);
        healthScore.put("sloComplianceRate", Math.round(complianceRate * 100.0) / 100.0);
        healthScore.put("sloScore", sloScore);
        healthScore.put("measuredAt", latest.getMetricDate());

        return healthScore;
    }
}
