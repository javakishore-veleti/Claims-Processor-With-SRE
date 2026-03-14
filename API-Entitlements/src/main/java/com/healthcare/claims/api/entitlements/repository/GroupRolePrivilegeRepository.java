package com.healthcare.claims.api.entitlements.repository;

import com.healthcare.claims.api.entitlements.model.GroupRolePrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRolePrivilegeRepository extends JpaRepository<GroupRolePrivilege, UUID> {

    List<GroupRolePrivilege> findByTenantIdAndGroupRoleId(String tenantId, UUID groupRoleId);

    boolean existsByTenantIdAndGroupRoleIdAndPrivilegeId(String tenantId, UUID groupRoleId, UUID privilegeId);
}
