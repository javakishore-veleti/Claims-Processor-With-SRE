package com.healthcare.claims.api.entitlements.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "group_role_privileges", indexes = {
    @Index(name = "idx_group_role_privileges_group_role_id", columnList = "groupRoleId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupRolePrivilege {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "group_role_id", insertable = false, updatable = false)
    private UUID groupRoleId;

    @Column(name = "privilege_id", insertable = false, updatable = false)
    private UUID privilegeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_role_id", nullable = false)
    private GroupRole groupRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "privilege_id", nullable = false)
    private Privilege privilege;
}
