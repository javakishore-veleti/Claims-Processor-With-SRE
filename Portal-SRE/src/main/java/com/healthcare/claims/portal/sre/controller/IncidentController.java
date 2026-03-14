package com.healthcare.claims.portal.sre.controller;

import com.healthcare.claims.portal.sre.model.IncidentRecord;
import com.healthcare.claims.portal.sre.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sre/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    public ResponseEntity<IncidentRecord> createIncident(@RequestBody IncidentRecord incident) {
        return ResponseEntity.ok(incidentService.createIncident(incident));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidentRecord> updateIncident(
            @PathVariable UUID id,
            @RequestBody IncidentRecord updates) {
        return ResponseEntity.ok(incidentService.updateIncident(id, updates));
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<IncidentRecord> resolveIncident(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body) {
        String rootCause = body != null ? body.get("rootCause") : null;
        return ResponseEntity.ok(incidentService.resolveIncident(id, rootCause));
    }

    @GetMapping("/active")
    public ResponseEntity<List<IncidentRecord>> getActiveIncidents() {
        return ResponseEntity.ok(incidentService.getActiveIncidents());
    }

    @GetMapping("/mttr")
    public ResponseEntity<Map<String, Object>> getMTTR() {
        return ResponseEntity.ok(incidentService.getMTTR());
    }
}
