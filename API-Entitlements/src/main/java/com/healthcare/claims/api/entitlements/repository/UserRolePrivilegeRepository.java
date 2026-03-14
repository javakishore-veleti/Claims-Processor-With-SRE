package com.healthcare.claims.api.entitlements.repository;

import com.healthcare.claims.api.entitlements.model.UserRolePrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRolePrivilegeRepository extends JpaRepository<UserRolePrivilege, UUID> {

    List<UserRolePrivilege> findByTenantIdAndUserId(String tenantId, UUID userId);

    boolean existsByTenantIdAndUserIdAndRoleIdAndPrivilegeId(String tenantId, UUID userId, UUID roleId, UUID privilegeId);
}
