package com.healthcare.claims.api.entitlements.repository;

import com.healthcare.claims.api.entitlements.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, UUID> {

    List<Privilege> findByTenantId(String tenantId);

    Optional<Privilege> findByTenantIdAndName(String tenantId, String name);

    List<Privilege> findByTenantIdAndResource(String tenantId, String resource);

    List<Privilege> findByTenantIdAndAction(String tenantId, String action);

    boolean existsByTenantIdAndName(String tenantId, String name);
}
