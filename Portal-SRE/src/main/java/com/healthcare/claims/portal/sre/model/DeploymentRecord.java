package com.healthcare.claims.portal.sre.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "deployment_records", indexes = {
    @Index(name = "idx_deployment_service", columnList = "serviceName"),
    @Index(name = "idx_deployment_env", columnList = "environment"),
    @Index(name = "idx_deployment_deployed_at", columnList = "deployedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeploymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private String environment;

    @Column(name = "cloud_provider")
    private String cloudProvider;

    @Column(name = "deployed_by")
    private String deployedBy;

    @Column(name = "deployed_at", nullable = false)
    private LocalDateTime deployedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeploymentStatus status;

    @Column(name = "commit_hash")
    private String commitHash;

    @Column(name = "release_notes", length = 2000)
    private String releaseNotes;

    @Column(name = "rollback_of")
    private UUID rollbackOf;

    public enum DeploymentStatus {
        DEPLOYED, ROLLED_BACK, FAILED
    }
}
