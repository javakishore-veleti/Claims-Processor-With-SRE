package com.healthcare.claims.api.claims.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "claims", indexes = {
    @Index(name = "idx_claims_tenant_id", columnList = "tenantId"),
    @Index(name = "idx_claims_customer_id", columnList = "customerId"),
    @Index(name = "idx_claims_stage", columnList = "stage"),
    @Index(name = "idx_claims_tenant_stage", columnList = "tenantId, stage")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "claim_number", unique = true, nullable = false)
    private String claimNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false)
    private ClaimStage stage;

    @Column(name = "submitted_date")
    private LocalDate submittedDate;

    @ElementCollection
    @CollectionTable(name = "claim_documents", joinColumns = @JoinColumn(name = "claim_id"))
    @Column(name = "document_url")
    private List<String> documents;

    @Column(name = "extracted_data", length = 10000)
    private String extractedData;

    @Column(name = "adjudication_result")
    private String adjudicationResult;

    @Column(name = "confidence_score", precision = 5, scale = 4)
    private BigDecimal confidenceScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (stage == null) {
            stage = ClaimStage.INTAKE_RECEIVED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
