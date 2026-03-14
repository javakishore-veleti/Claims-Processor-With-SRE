package com.healthcare.claims.portal.sre.repository;

import com.healthcare.claims.portal.sre.model.CloudResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface CloudResourceRepository extends JpaRepository<CloudResource, UUID> {

    List<CloudResource> findByCloudProvider(CloudResource.CloudProvider cloudProvider);

    List<CloudResource> findByTenantId(String tenantId);

    List<CloudResource> findByStatus(CloudResource.ResourceStatus status);

    List<CloudResource> findByTenantIdIsNull();

    @Query("SELECT SUM(c.monthlyCostEstimate) FROM CloudResource c WHERE c.tenantId = :tenantId")
    BigDecimal sumMonthlyCostByTenantId(String tenantId);

    @Query("SELECT c.status, COUNT(c) FROM CloudResource c GROUP BY c.status")
    List<Object[]> countByStatusGrouped();
}
