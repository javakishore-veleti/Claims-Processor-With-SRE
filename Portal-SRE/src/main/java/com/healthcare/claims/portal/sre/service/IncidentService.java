package com.healthcare.claims.portal.sre.service;

import com.healthcare.claims.portal.sre.model.IncidentRecord;
import com.healthcare.claims.portal.sre.repository.IncidentRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncidentService {

    private final IncidentRecordRepository incidentRecordRepository;

    /**
     * Create a new incident.
     */
    public IncidentRecord createIncident(IncidentRecord incident) {
        log.info("Creating incident: {} (severity: {})", incident.getTitle(), incident.getSeverity());
        incident.setStatus(IncidentRecord.IncidentStatus.OPEN);
        if (incident.getStartedAt() == null) {
            incident.setStartedAt(LocalDateTime.now());
        }
        return incidentRecordRepository.save(incident);
    }

    /**
     * Update an existing incident.
     */
    public IncidentRecord updateIncident(UUID id, IncidentRecord updates) {
        log.info("Updating incident: {}", id);
        IncidentRecord existing = incidentRecordRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Incident not found: " + id));

        if (updates.getTitle() != null) existing.setTitle(updates.getTitle());
        if (updates.getSeverity() != null) existing.setSeverity(updates.getSeverity());
        if (updates.getStatus() != null) existing.setStatus(updates.getStatus());
        if (updates.getRootCause() != null) existing.setRootCause(updates.getRootCause());
        if (updates.getPostmortemUrl() != null) existing.setPostmortemUrl(updates.getPostmortemUrl());
        if (updates.getAffectedServices() != null) existing.setAffectedServices(updates.getAffectedServices());
        if (updates.getAffectedTenants() != null) existing.setAffectedTenants(updates.getAffectedTenants());

        return incidentRecordRepository.save(existing);
    }

    /**
     * Resolve an incident.
     */
    public IncidentRecord resolveIncident(UUID id, String rootCause) {
        log.info("Resolving incident: {}", id);
        IncidentRecord incident = incidentRecordRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Incident not found: " + id));

        incident.setStatus(IncidentRecord.IncidentStatus.RESOLVED);
        incident.setResolvedAt(LocalDateTime.now());
        if (rootCause != null) {
            incident.setRootCause(rootCause);
        }

        return incidentRecordRepository.save(incident);
    }

    /**
     * Get all active (non-resolved) incidents.
     */
    public List<IncidentRecord> getActiveIncidents() {
        return incidentRecordRepository.findActiveIncidents();
    }

    /**
     * Get all incidents as a timeline (ordered by start time descending).
     */
    public List<IncidentRecord> getIncidentTimeline() {
        return incidentRecordRepository.findAllByOrderByStartedAtDesc();
    }

    /**
     * Calculate mean time to resolution (MTTR) in minutes for resolved incidents.
     */
    public Map<String, Object> getMTTR() {
        log.info("Calculating MTTR for resolved incidents");

        Map<String, Object> mttrResult = new LinkedHashMap<>();
        List<IncidentRecord> resolved = incidentRecordRepository.findResolvedIncidents();

        if (resolved.isEmpty()) {
            mttrResult.put("mttrMinutes", 0);
            mttrResult.put("resolvedCount", 0);
            mttrResult.put("message", "No resolved incidents to calculate MTTR");
            return mttrResult;
        }

        double totalMinutes = resolved.stream()
                .filter(i -> i.getResolvedAt() != null && i.getStartedAt() != null)
                .mapToDouble(i -> Duration.between(i.getStartedAt(), i.getResolvedAt()).toMinutes())
                .sum();

        long count = resolved.stream()
                .filter(i -> i.getResolvedAt() != null && i.getStartedAt() != null)
                .count();

        double mttr = count > 0 ? totalMinutes / count : 0;

        mttrResult.put("mttrMinutes", Math.round(mttr * 100.0) / 100.0);
        mttrResult.put("mttrHours", Math.round(mttr / 60.0 * 100.0) / 100.0);
        mttrResult.put("resolvedCount", count);

        // MTTR by severity
        Map<String, Double> mttrBySeverity = new LinkedHashMap<>();
        for (IncidentRecord.Severity severity : IncidentRecord.Severity.values()) {
            double sevMinutes = resolved.stream()
                    .filter(i -> i.getSeverity() == severity && i.getResolvedAt() != null && i.getStartedAt() != null)
                    .mapToDouble(i -> Duration.between(i.getStartedAt(), i.getResolvedAt()).toMinutes())
                    .average()
                    .orElse(0);
            mttrBySeverity.put(severity.name(), Math.round(sevMinutes * 100.0) / 100.0);
        }
        mttrResult.put("mttrBySeverity", mttrBySeverity);

        return mttrResult;
    }
}
