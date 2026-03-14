package com.healthcare.claims.portal.sre.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceHealthAggregator {

    @Value("${claims.service-client.services.api-claims.url:http://localhost:8081}")
    private String apiClaimsUrl;

    @Value("${claims.service-client.services.api-members.url:http://localhost:8082}")
    private String apiMembersUrl;

    @Value("${claims.service-client.services.api-tenants.url:http://localhost:8086}")
    private String apiTenantsUrl;

    @Value("${claims.service-client.services.api-entitlements.url:http://localhost:8087}")
    private String apiEntitlementsUrl;

    @Value("${claims.service-client.services.portal-claims-advisor.url:http://localhost:8083}")
    private String portalClaimsAdvisorUrl;

    @Value("${claims.service-client.services.portal-claims-member.url:http://localhost:8084}")
    private String portalClaimsMemberUrl;

    @Value("${claims.service-client.services.portal-batch-assignment.url:http://localhost:8085}")
    private String portalBatchAssignmentUrl;

    @Value("${claims.service-client.services.portal-tenants.url:http://localhost:8088}")
    private String portalTenantsUrl;

    @Value("${claims.service-client.services.portal-entitlements.url:http://localhost:8089}")
    private String portalEntitlementsUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Build a service dependency map with live health status from each service's actuator endpoint.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getServiceMap() {
        log.info("Building service health map");

        Map<String, String> services = new LinkedHashMap<>();
        services.put("api-claims", apiClaimsUrl);
        services.put("api-members", apiMembersUrl);
        services.put("api-tenants", apiTenantsUrl);
        services.put("api-entitlements", apiEntitlementsUrl);
        services.put("portal-claims-advisor", portalClaimsAdvisorUrl);
        services.put("portal-claims-member", portalClaimsMemberUrl);
        services.put("portal-batch-assignment", portalBatchAssignmentUrl);
        services.put("portal-tenants", portalTenantsUrl);
        services.put("portal-entitlements", portalEntitlementsUrl);

        List<Map<String, Object>> serviceMap = new ArrayList<>();

        for (Map.Entry<String, String> entry : services.entrySet()) {
            Map<String, Object> serviceInfo = new LinkedHashMap<>();
            serviceInfo.put("serviceName", entry.getKey());
            serviceInfo.put("baseUrl", entry.getValue());
            serviceInfo.put("checkedAt", LocalDateTime.now().toString());

            try {
                String healthUrl = entry.getValue() + "/actuator/health";
                Map<String, Object> healthResponse = restTemplate.getForObject(healthUrl, Map.class);

                if (healthResponse != null) {
                    serviceInfo.put("status", healthResponse.getOrDefault("status", "UNKNOWN"));
                    serviceInfo.put("details", healthResponse.get("components"));
                } else {
                    serviceInfo.put("status", "UNKNOWN");
                }
            } catch (Exception e) {
                log.warn("Failed to check health of {}: {}", entry.getKey(), e.getMessage());
                serviceInfo.put("status", "UNAVAILABLE");
                serviceInfo.put("error", e.getMessage());
            }

            // Define known dependencies
            serviceInfo.put("dependencies", getDependencies(entry.getKey()));
            serviceMap.add(serviceInfo);
        }

        return serviceMap;
    }

    /**
     * Get a high-level overview of all service health statuses.
     */
    public Map<String, Object> getOverview() {
        log.info("Building service health overview");

        List<Map<String, Object>> serviceMap = getServiceMap();
        Map<String, Object> overview = new LinkedHashMap<>();

        long totalServices = serviceMap.size();
        long upServices = serviceMap.stream()
                .filter(s -> "UP".equals(s.get("status")))
                .count();
        long downServices = serviceMap.stream()
                .filter(s -> "UNAVAILABLE".equals(s.get("status")) || "DOWN".equals(s.get("status")))
                .count();

        overview.put("totalServices", totalServices);
        overview.put("servicesUp", upServices);
        overview.put("servicesDown", downServices);
        overview.put("servicesUnknown", totalServices - upServices - downServices);
        overview.put("overallStatus", upServices == totalServices ? "ALL_HEALTHY"
                : downServices == 0 ? "PARTIALLY_DEGRADED"
                : "CRITICAL");
        overview.put("services", serviceMap);
        overview.put("checkedAt", LocalDateTime.now().toString());

        return overview;
    }

    private List<String> getDependencies(String serviceName) {
        return switch (serviceName) {
            case "portal-claims-advisor" -> List.of("api-claims", "api-members", "api-tenants");
            case "portal-claims-member" -> List.of("api-claims", "api-members");
            case "portal-batch-assignment" -> List.of("api-claims", "api-members");
            case "portal-tenants" -> List.of("api-tenants");
            case "portal-entitlements" -> List.of("api-entitlements", "api-tenants");
            case "api-claims" -> List.of("api-members", "api-entitlements");
            case "api-members" -> List.of();
            case "api-tenants" -> List.of();
            case "api-entitlements" -> List.of("api-tenants");
            default -> List.of();
        };
    }
}
