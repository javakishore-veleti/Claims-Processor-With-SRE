package com.healthcare.claims.portal.sre.repository;

import com.healthcare.claims.portal.sre.model.TenantUsageMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TenantUsageMetricsRepository extends JpaRepository<TenantUsageMetrics, UUID> {

    List<TenantUsageMetrics> findByTenantIdAndMetricDateBetweenOrderByMetricDateAsc(
            String tenantId, LocalDate startDate, LocalDate endDate);

    List<TenantUsageMetrics> findByTenantIdOrderByMetricDateDesc(String tenantId);

    @Query("SELECT t FROM TenantUsageMetrics t WHERE t.metricDate = :date ORDER BY t.apiCallCount DESC")
    List<TenantUsageMetrics> findTopTenantsByApiCallCount(@Param("date") LocalDate date);

    @Query("SELECT t FROM TenantUsageMetrics t WHERE t.metricDate = :date ORDER BY t.claimsProcessed DESC")
    List<TenantUsageMetrics> findTopTenantsByClaimsProcessed(@Param("date") LocalDate date);

    List<TenantUsageMetrics> findByTenantIdAndMetricDate(String tenantId, LocalDate metricDate);
}
