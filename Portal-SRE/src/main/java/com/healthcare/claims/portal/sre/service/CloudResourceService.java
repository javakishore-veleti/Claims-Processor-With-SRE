package com.healthcare.claims.portal.sre.service;

import com.healthcare.claims.portal.sre.model.CloudResource;
import com.healthcare.claims.portal.sre.repository.CloudResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudResourceService {

    private final CloudResourceRepository cloudResourceRepository;

    /**
     * Discover resources for a given cloud provider.
     * In a real implementation, this would call cloud provider APIs.
     * Here it refreshes the last-checked timestamp on known resources.
     */
    public List<CloudResource> discoverResources(CloudResource.CloudProvider cloudProvider) {
        log.info("Discovering resources for cloud provider: {}", cloudProvider);
        List<CloudResource> resources = cloudResourceRepository.findByCloudProvider(cloudProvider);
        resources.forEach(r -> r.setLastCheckedAt(LocalDateTime.now()));
        return cloudResourceRepository.saveAll(resources);
    }

    /**
     * Get aggregated resource health summary across all cloud providers.
     */
    public Map<String, Object> getResourceHealth() {
        log.info("Aggregating resource health across all cloud providers");

        Map<String, Object> health = new LinkedHashMap<>();
        List<Object[]> statusCounts = cloudResourceRepository.countByStatusGrouped();

        long total = 0;
        Map<String, Long> statusMap = new LinkedHashMap<>();
        for (Object[] row : statusCounts) {
            String status = row[0].toString();
            Long count = (Long) row[1];
            statusMap.put(status, count);
            total += count;
        }

        health.put("totalResources", total);
        health.put("statusBreakdown", statusMap);

        long healthy = statusMap.getOrDefault("HEALTHY", 0L);
        double healthPercentage = total > 0 ? (double) healthy / total * 100 : 0;
        health.put("healthPercentage", Math.round(healthPercentage * 100.0) / 100.0);
        health.put("overallStatus", healthPercentage >= 95 ? "HEALTHY" : healthPercentage >= 80 ? "DEGRADED" : "CRITICAL");

        // Per-provider breakdown
        Map<String, Map<String, Long>> providerBreakdown = new LinkedHashMap<>();
        for (CloudResource.CloudProvider provider : CloudResource.CloudProvider.values()) {
            List<CloudResource> providerResources = cloudResourceRepository.findByCloudProvider(provider);
            if (!providerResources.isEmpty()) {
                Map<String, Long> providerStatus = providerResources.stream()
                        .collect(Collectors.groupingBy(r -> r.getStatus().name(), Collectors.counting()));
                providerBreakdown.put(provider.name(), providerStatus);
            }
        }
        health.put("providerBreakdown", providerBreakdown);

        return health;
    }

    /**
     * Get resources assigned to a specific tenant.
     */
    public List<CloudResource> getResourcesByTenant(String tenantId) {
        log.info("Fetching cloud resources for tenant: {}", tenantId);
        return cloudResourceRepository.findByTenantId(tenantId);
    }

    /**
     * Estimate monthly cost for a tenant based on assigned resources.
     */
    public Map<String, Object> estimateMonthlyCost(String tenantId) {
        log.info("Estimating monthly cost for tenant: {}", tenantId);

        Map<String, Object> costEstimate = new LinkedHashMap<>();
        costEstimate.put("tenantId", tenantId);

        List<CloudResource> tenantResources = cloudResourceRepository.findByTenantId(tenantId);
        BigDecimal directCost = cloudResourceRepository.sumMonthlyCostByTenantId(tenantId);
        if (directCost == null) directCost = BigDecimal.ZERO;

        // Also include proportional cost of shared resources
        List<CloudResource> sharedResources = cloudResourceRepository.findByTenantIdIsNull();
        // Estimate shared cost as total shared / number of unique tenants
        BigDecimal sharedTotal = sharedResources.stream()
                .map(r -> r.getMonthlyCostEstimate() != null ? r.getMonthlyCostEstimate() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long uniqueTenantCount = cloudResourceRepository.findAll().stream()
                .map(CloudResource::getTenantId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        BigDecimal sharedPerTenant = uniqueTenantCount > 0
                ? sharedTotal.divide(BigDecimal.valueOf(uniqueTenantCount), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        costEstimate.put("directResourceCost", directCost);
        costEstimate.put("sharedResourceCost", sharedPerTenant);
        costEstimate.put("totalEstimatedMonthlyCost", directCost.add(sharedPerTenant));
        costEstimate.put("resourceCount", tenantResources.size());
        costEstimate.put("currency", "USD");

        // Per-provider cost breakdown
        Map<String, BigDecimal> providerCosts = tenantResources.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCloudProvider().name(),
                        Collectors.reducing(BigDecimal.ZERO,
                                r -> r.getMonthlyCostEstimate() != null ? r.getMonthlyCostEstimate() : BigDecimal.ZERO,
                                BigDecimal::add)));
        costEstimate.put("costByProvider", providerCosts);

        return costEstimate;
    }
}
