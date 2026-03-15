package com.healthcare.claims.portal.sre.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant_usage_metrics", indexes = {
    @Index(name = "idx_tenant_usage_tenant_date", columnList = "tenant_id, metric_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantUsageMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "api_call_count")
    private Long apiCallCount;

    @Column(name = "claims_processed")
    private Long claimsProcessed;

    @Column(name = "claims_approved")
    private Long claimsApproved;

    @Column(name = "claims_denied")
    private Long claimsDenied;

    @Column(name = "members_count")
    private Long membersCount;

    @Column(name = "active_users_count")
    private Long activeUsersCount;

    @Column(name = "storage_used_bytes")
    private Long storageUsedBytes;

    @Column(name = "avg_response_time_ms")
    private Double avgResponseTimeMs;

    @Column(name = "error_count")
    private Long errorCount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
