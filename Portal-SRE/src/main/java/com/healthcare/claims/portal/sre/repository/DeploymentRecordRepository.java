package com.healthcare.claims.portal.sre.repository;

import com.healthcare.claims.portal.sre.model.DeploymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeploymentRecordRepository extends JpaRepository<DeploymentRecord, UUID> {

    List<DeploymentRecord> findByServiceNameOrderByDeployedAtDesc(String serviceName);

    List<DeploymentRecord> findByEnvironmentOrderByDeployedAtDesc(String environment);

    @Query("SELECT d FROM DeploymentRecord d WHERE d.status = 'DEPLOYED' " +
           "AND d.deployedAt = (SELECT MAX(d2.deployedAt) FROM DeploymentRecord d2 " +
           "WHERE d2.serviceName = d.serviceName AND d2.environment = d.environment AND d2.status = 'DEPLOYED')")
    List<DeploymentRecord> findActiveVersions();

    List<DeploymentRecord> findTop10ByOrderByDeployedAtDesc();
}
