package com.healthcare.claims.api.entitlements.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_role_privileges", indexes = {
    @Index(name = "idx_user_role_privileges_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRolePrivilege {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    @Column(name = "role_id", insertable = false, updatable = false)
    private UUID roleId;

    @Column(name = "privilege_id", insertable = false, updatable = false)
    private UUID privilegeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "privilege_id", nullable = false)
    private Privilege privilege;
}
