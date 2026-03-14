package com.healthcare.claims.portal.sre.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cloud_resources", indexes = {
    @Index(name = "idx_cloud_resources_provider", columnList = "cloudProvider"),
    @Index(name = "idx_cloud_resources_tenant", columnList = "tenantId"),
    @Index(name = "idx_cloud_resources_type", columnList = "resourceType")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloudResource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id")
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "cloud_provider", nullable = false)
    private CloudProvider cloudProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    @Column(name = "resource_name", nullable = false)
    private String resourceName;

    @Column
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status;

    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "monthly_cost_estimate", precision = 12, scale = 2)
    private BigDecimal monthlyCostEstimate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum CloudProvider {
        AWS, AZURE, GCP, LOCAL
    }

    public enum ResourceType {
        DATABASE, CACHE, QUEUE, STORAGE, COMPUTE, AI_SERVICE, SEARCH
    }

    public enum ResourceStatus {
        HEALTHY, DEGRADED, UNAVAILABLE, UNKNOWN
    }
}
