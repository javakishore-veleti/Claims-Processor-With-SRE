package com.healthcare.claims.api.tenants.repository;

import com.healthcare.claims.api.tenants.model.Tenant;
import com.healthcare.claims.api.tenants.model.TenantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findByTenantId(String tenantId);

    List<Tenant> findByStatus(TenantStatus status);

    Optional<Tenant> findByDomain(String domain);

    @Query("SELECT t FROM Tenant t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Tenant> searchByNameContaining(@Param("name") String name);
}
