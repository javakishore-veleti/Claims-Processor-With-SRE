package com.healthcare.claims.portal.sre.repository;

import com.healthcare.claims.portal.sre.model.IncidentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IncidentRecordRepository extends JpaRepository<IncidentRecord, UUID> {

    List<IncidentRecord> findByStatusNotOrderByStartedAtDesc(IncidentRecord.IncidentStatus status);

    List<IncidentRecord> findByStatusInOrderBySeverityAscStartedAtDesc(List<IncidentRecord.IncidentStatus> statuses);

    @Query("SELECT i FROM IncidentRecord i WHERE i.status != 'RESOLVED' ORDER BY i.severity ASC, i.startedAt DESC")
    List<IncidentRecord> findActiveIncidents();

    @Query("SELECT i FROM IncidentRecord i WHERE i.status = 'RESOLVED' AND i.resolvedAt IS NOT NULL ORDER BY i.resolvedAt DESC")
    List<IncidentRecord> findResolvedIncidents();

    List<IncidentRecord> findAllByOrderByStartedAtDesc();
}
