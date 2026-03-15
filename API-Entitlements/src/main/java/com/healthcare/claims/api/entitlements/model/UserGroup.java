package com.healthcare.claims.api.entitlements.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_groups", indexes = {
    @Index(name = "idx_user_groups_user_id", columnList = "user_id"),
    @Index(name = "idx_user_groups_group_id", columnList = "group_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    @Column(name = "group_id", insertable = false, updatable = false)
    private UUID groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
}
