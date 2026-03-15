package com.healthcare.claims.portal.sre.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant_slo_compliance", indexes = {
    @Index(name = "idx_slo_compliance_tenant", columnList = "tenant_id"),
    @Index(name = "idx_slo_compliance_slo", columnList = "slo_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantSloCompliance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "slo_name", nullable = false)
    private String sloName;

    @Column(name = "target_value", nullable = false)
    private Double targetValue;

    @Column(name = "actual_value", nullable = false)
    private Double actualValue;

    @Column(nullable = false)
    private boolean compliant;

    @Column(name = "measurement_period", nullable = false)
    private String measurementPeriod;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;
}
