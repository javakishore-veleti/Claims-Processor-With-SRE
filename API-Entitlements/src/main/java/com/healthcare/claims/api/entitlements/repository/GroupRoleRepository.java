package com.healthcare.claims.api.entitlements.repository;

import com.healthcare.claims.api.entitlements.model.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRoleRepository extends JpaRepository<GroupRole, UUID> {

    List<GroupRole> findByTenantIdAndGroupId(String tenantId, UUID groupId);

    List<GroupRole> findByTenantIdAndRoleId(String tenantId, UUID roleId);

    Optional<GroupRole> findByTenantIdAndGroupIdAndRoleId(String tenantId, UUID groupId, UUID roleId);

    boolean existsByTenantIdAndGroupIdAndRoleId(String tenantId, UUID groupId, UUID roleId);
}
