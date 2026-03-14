package com.healthcare.claims.portal.sre.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "incident_records", indexes = {
    @Index(name = "idx_incidents_severity", columnList = "severity"),
    @Index(name = "idx_incidents_status", columnList = "status"),
    @Index(name = "idx_incidents_started_at", columnList = "startedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "incident_affected_services", joinColumns = @JoinColumn(name = "incident_id"))
    @Column(name = "service_name")
    private List<String> affectedServices;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "incident_affected_tenants", joinColumns = @JoinColumn(name = "incident_id"))
    @Column(name = "tenant_id")
    private List<String> affectedTenants;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "root_cause", length = 2000)
    private String rootCause;

    @Column(name = "postmortem_url")
    private String postmortemUrl;

    @Column(name = "created_by")
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Severity {
        P1, P2, P3, P4
    }

    public enum IncidentStatus {
        OPEN, INVESTIGATING, MITIGATED, RESOLVED
    }
}
