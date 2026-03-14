package com.healthcare.claims.portal.sre.controller;

import com.healthcare.claims.portal.sre.service.ServiceHealthAggregator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
