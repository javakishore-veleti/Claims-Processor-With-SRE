package com.healthcare.claims.portal.sre.service;

import com.healthcare.claims.portal.sre.model.DeploymentRecord;
import com.healthcare.claims.portal.sre.repository.DeploymentRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentService {

    private final DeploymentRecordRepository deploymentRecordRepository;

    /**
     * Record a new deployment.
     */
    public DeploymentRecord recordDeployment(DeploymentRecord record) {
        log.info("Recording deployment: {} v{} to {} on {}",
                record.getServiceName(), record.getVersion(),
                record.getEnvironment(), record.getCloudProvider());
        return deploymentRecordRepository.save(record);
    }

    /**
     * Get deployment history for a specific service.
     */
    public List<DeploymentRecord> getDeploymentHistory(String serviceName, int limit) {
        log.info("Fetching deployment history for service: {} (limit: {})", serviceName, limit);
        List<DeploymentRecord> history = deploymentRecordRepository
                .findByServiceNameOrderByDeployedAtDesc(serviceName);
        return history.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Get the currently active (deployed) version of each service in each environment.
     */
    public List<DeploymentRecord> getActiveVersions() {
        log.info("Fetching active versions for all services");
        return deploymentRecordRepository.findActiveVersions();
    }

    /**
     * Get the most recent deployments across all services.
     */
    public List<DeploymentRecord> getRecentDeployments() {
        return deploymentRecordRepository.findTop10ByOrderByDeployedAtDesc();
    }
}
