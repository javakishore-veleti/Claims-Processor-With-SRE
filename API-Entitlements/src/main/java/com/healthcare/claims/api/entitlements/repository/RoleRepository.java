package com.healthcare.claims.api.entitlements.repository;

import com.healthcare.claims.api.entitlements.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    List<Role> findByTenantId(String tenantId);

    Optional<Role> findByTenantIdAndName(String tenantId, String name);

    List<Role> findByTenantIdAndSystemRole(String tenantId, boolean systemRole);

    boolean existsByTenantIdAndName(String tenantId, String name);
}
