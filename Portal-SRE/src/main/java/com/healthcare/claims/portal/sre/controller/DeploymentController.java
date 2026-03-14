package com.healthcare.claims.portal.sre.controller;

import com.healthcare.claims.portal.sre.model.DeploymentRecord;
import com.healthcare.claims.portal.sre.service.DeploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sre/deployments")
@RequiredArgsConstructor
public class DeploymentController {

    private final DeploymentService deploymentService;

    @PostMapping
    public ResponseEntity<DeploymentRecord> recordDeployment(@RequestBody DeploymentRecord record) {
        return ResponseEntity.ok(deploymentService.recordDeployment(record));
    }

    @GetMapping("/history/{serviceName}")
    public ResponseEntity<List<DeploymentRecord>> getDeploymentHistory(
            @PathVariable String serviceName,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(deploymentService.getDeploymentHistory(serviceName, limit));
    }

    @GetMapping("/active-versions")
    public ResponseEntity<List<DeploymentRecord>> getActiveVersions() {
        return ResponseEntity.ok(deploymentService.getActiveVersions());
    }
}
