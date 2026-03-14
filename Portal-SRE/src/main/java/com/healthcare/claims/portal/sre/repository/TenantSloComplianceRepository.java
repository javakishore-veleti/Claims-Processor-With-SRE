package com.healthcare.claims.portal.sre.repository;

import com.healthcare.claims.portal.sre.model.TenantSloCompliance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TenantSloComplianceRepository extends JpaRepository<TenantSloCompliance, UUID> {

    List<TenantSloCompliance> findByTenantIdOrderByMeasuredAtDesc(String tenantId);

    List<TenantSloCompliance> findByTenantIdAndMeasurementPeriod(String tenantId, String measurementPeriod);

    @Query("SELECT t.tenantId, COUNT(t), SUM(CASE WHEN t.compliant = true THEN 1 ELSE 0 END) " +
           "FROM TenantSloCompliance t GROUP BY t.tenantId")
    List<Object[]> getComplianceSummaryByTenant();

    @Query("SELECT COUNT(t), SUM(CASE WHEN t.compliant = true THEN 1 ELSE 0 END) FROM TenantSloCompliance t")
    Object[] getGlobalComplianceSummary();

    List<TenantSloCompliance> findByCompliantFalseOrderByMeasuredAtDesc();
}
