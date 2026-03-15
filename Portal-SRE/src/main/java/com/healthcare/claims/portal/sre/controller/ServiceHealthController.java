package com.healthcare.claims.portal.sre.controller;

import com.healthcare.claims.portal.sre.service.ServiceHealthAggregator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sre/service-health")
@RequiredArgsConstructor
public class ServiceHealthController {

    private final ServiceHealthAggregator serviceHealthAggregator;

    @GetMapping("/map")
    public ResponseEntity<List<Map<String, Object>>> getServiceMap() {
        return ResponseEntity.ok(serviceHealthAggregator.getServiceMap());
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        return ResponseEntity.ok(serviceHealthAggregator.getOverview());
    }

    /**
     * Proxy actuator health check for a specific service port.
     * This allows the Angular UI to check service health through the BFF
     * instead of making cross-origin calls directly.
     */
    @GetMapping("/check/{port}")
    public ResponseEntity<Map<String, Object>> checkServiceHealth(@PathVariable int port) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            @SuppressWarnings("unchecked")
            Map<String, Object> health = restTemplate.getForObject(
                    "http://localhost:" + port + "/actuator/health", Map.class);
            return ResponseEntity.ok(health != null ? health : Map.of("status", "UNKNOWN"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("status", "DOWN"));
        }
    }
}
