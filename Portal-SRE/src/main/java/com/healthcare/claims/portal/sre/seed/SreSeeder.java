package com.healthcare.claims.portal.sre.seed;

import com.healthcare.claims.portal.sre.model.*;
import com.healthcare.claims.portal.sre.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class SreSeeder implements CommandLineRunner {

    private final CloudResourceRepository cloudResourceRepository;
    private final DeploymentRecordRepository deploymentRecordRepository;
    private final TenantUsageMetricsRepository tenantUsageMetricsRepository;
    private final TenantSloComplianceRepository tenantSloComplianceRepository;
    private final IncidentRecordRepository incidentRecordRepository;

    @Override
    public void run(String... args) {
        if (cloudResourceRepository.count() > 0) {
            log.info("SRE seed data already exists, skipping seeder");
            return;
        }

        log.info("Seeding SRE data...");
        seedCloudResources();
        seedDeploymentRecords();
        seedTenantUsageMetrics();
        seedSloComplianceRecords();
        seedIncidentRecords();
        log.info("SRE seed data complete");
    }

    private void seedCloudResources() {
        log.info("Seeding cloud resources...");

        // AWS resources
        seedResourcesForProvider(CloudResource.CloudProvider.AWS, "us-east-1", new String[][]{
                {"DATABASE", "rds-claims-primary", "arn:aws:rds:us-east-1:123456:db/claims-primary", "450.00"},
                {"CACHE", "elasticache-claims", "arn:aws:elasticache:us-east-1:123456:cluster/claims", "180.00"},
                {"QUEUE", "sqs-claims-processing", "arn:aws:sqs:us-east-1:123456:claims-processing", "25.00"},
                {"STORAGE", "s3-claims-documents", "arn:aws:s3:::claims-documents", "120.00"},
                {"COMPUTE", "ecs-claims-api", "arn:aws:ecs:us-east-1:123456:service/claims-api", "380.00"}
        });

        // Azure resources
        seedResourcesForProvider(CloudResource.CloudProvider.AZURE, "eastus", new String[][]{
                {"DATABASE", "azure-sql-members", "azure-sql-members-eastus", "400.00"},
                {"CACHE", "redis-members-cache", "redis-members-eastus", "150.00"},
                {"QUEUE", "servicebus-members-queue", "sb-members-eastus", "30.00"},
                {"STORAGE", "blob-member-docs", "stmemberdocs", "95.00"},
                {"COMPUTE", "aks-members-api", "aks-members-eastus", "420.00"}
        });

        // GCP resources
        seedResourcesForProvider(CloudResource.CloudProvider.GCP, "us-central1", new String[][]{
                {"DATABASE", "cloudsql-tenants", "projects/claims/instances/tenants-db", "350.00"},
                {"CACHE", "memorystore-tenants", "projects/claims/locations/us-central1/instances/tenants-cache", "130.00"},
                {"QUEUE", "pubsub-tenant-events", "projects/claims/topics/tenant-events", "20.00"},
                {"STORAGE", "gcs-tenant-configs", "projects/claims/buckets/tenant-configs", "45.00"},
                {"COMPUTE", "gke-tenants-api", "projects/claims/locations/us-central1/clusters/tenants", "360.00"}
        });

        // Local resources
        seedResourcesForProvider(CloudResource.CloudProvider.LOCAL, "local", new String[][]{
                {"DATABASE", "h2-dev-database", "jdbc:h2:mem:claimsdb", "0.00"},
                {"CACHE", "local-redis", "localhost:6379", "0.00"},
                {"QUEUE", "local-kafka", "localhost:9092", "0.00"},
                {"STORAGE", "local-filesystem", "/tmp/claims-storage", "0.00"},
                {"COMPUTE", "local-jvm", "localhost:8080-8090", "0.00"}
        });
    }

    private void seedResourcesForProvider(CloudResource.CloudProvider provider, String region, String[][] resources) {
        for (String[] res : resources) {
            CloudResource.ResourceStatus status = ThreadLocalRandom.current().nextInt(10) < 8
                    ? CloudResource.ResourceStatus.HEALTHY
                    : CloudResource.ResourceStatus.DEGRADED;

            cloudResourceRepository.save(CloudResource.builder()
                    .cloudProvider(provider)
                    .resourceType(CloudResource.ResourceType.valueOf(res[0]))
                    .resourceName(res[1])
                    .resourceId(res[2])
                    .region(region)
                    .status(status)
                    .lastCheckedAt(LocalDateTime.now())
                    .monthlyCostEstimate(new BigDecimal(res[3]))
                    .metadata("{\"provider\":\"" + provider.name() + "\",\"region\":\"" + region + "\"}")
                    .build());
        }
    }

    private void seedDeploymentRecords() {
        log.info("Seeding deployment records...");

        String[] services = {
                "api-claims", "api-members", "api-tenants", "api-entitlements",
                "portal-claims-advisor", "portal-claims-member", "portal-batch-assignment",
                "portal-tenants", "portal-entitlements", "portal-sre"
        };

        String[] environments = {"dev", "staging", "prod"};
        LocalDateTime baseTime = LocalDateTime.now().minusDays(30);

        for (String service : services) {
            for (int i = 0; i < 3; i++) {
                String version = "0.1." + i;
                for (String env : environments) {
                    deploymentRecordRepository.save(DeploymentRecord.builder()
                            .serviceName(service)
                            .version(version)
                            .environment(env)
                            .cloudProvider("AWS")
                            .deployedBy("ci-pipeline")
                            .deployedAt(baseTime.plusDays(i * 10L).plusHours(environments.length))
                            .status(DeploymentRecord.DeploymentStatus.DEPLOYED)
                            .commitHash("abc" + i + service.hashCode())
                            .releaseNotes("Release " + version + " of " + service + " to " + env)
                            .build());
                }
            }
        }
    }

    private void seedTenantUsageMetrics() {
        log.info("Seeding tenant usage metrics...");

        for (int tenantNum = 1; tenantNum <= 10; tenantNum++) {
            String tenantId = "tenant-" + String.format("%03d", tenantNum);

            for (int dayOffset = 0; dayOffset < 30; dayOffset++) {
                LocalDate date = LocalDate.now().minusDays(dayOffset);
                int baseApiCalls = ThreadLocalRandom.current().nextInt(500, 5000);
                int baseClaims = ThreadLocalRandom.current().nextInt(50, 500);

                tenantUsageMetricsRepository.save(TenantUsageMetrics.builder()
                        .tenantId(tenantId)
                        .metricDate(date)
                        .apiCallCount((long) baseApiCalls)
                        .claimsProcessed((long) baseClaims)
                        .claimsApproved((long) (baseClaims * 0.75))
                        .claimsDenied((long) (baseClaims * 0.15))
                        .membersCount((long) (tenantNum * 1000 + ThreadLocalRandom.current().nextInt(500)))
                        .activeUsersCount((long) (tenantNum * 50 + ThreadLocalRandom.current().nextInt(100)))
                        .storageUsedBytes((long) (tenantNum * 1024L * 1024L * 100 + ThreadLocalRandom.current().nextInt(1024 * 1024)))
                        .avgResponseTimeMs(50.0 + ThreadLocalRandom.current().nextDouble(200))
                        .errorCount((long) ThreadLocalRandom.current().nextInt(0, baseApiCalls / 100))
                        .build());
            }
        }
    }

    private void seedSloComplianceRecords() {
        log.info("Seeding SLO compliance records...");

        String[] sloNames = {"Availability", "Latency P50", "Latency P99", "Error Rate", "Claims Throughput"};
        double[] targets = {99.9, 200.0, 1000.0, 0.1, 100.0};

        for (int tenantNum = 1; tenantNum <= 10; tenantNum++) {
            String tenantId = "tenant-" + String.format("%03d", tenantNum);

            for (int i = 0; i < sloNames.length; i++) {
                double actual;
                boolean compliant;

                switch (sloNames[i]) {
                    case "Availability":
                        actual = 99.5 + ThreadLocalRandom.current().nextDouble(0.5);
                        compliant = actual >= targets[i];
                        break;
                    case "Latency P50":
                        actual = 80 + ThreadLocalRandom.current().nextDouble(200);
                        compliant = actual <= targets[i];
                        break;
                    case "Latency P99":
                        actual = 500 + ThreadLocalRandom.current().nextDouble(800);
                        compliant = actual <= targets[i];
                        break;
                    case "Error Rate":
                        actual = ThreadLocalRandom.current().nextDouble(0.3);
                        compliant = actual <= targets[i];
                        break;
                    case "Claims Throughput":
                        actual = 50 + ThreadLocalRandom.current().nextDouble(200);
                        compliant = actual >= targets[i];
                        break;
                    default:
                        actual = 0;
                        compliant = false;
                }

                tenantSloComplianceRepository.save(TenantSloCompliance.builder()
                        .tenantId(tenantId)
                        .sloName(sloNames[i])
                        .targetValue(targets[i])
                        .actualValue(Math.round(actual * 1000.0) / 1000.0)
                        .compliant(compliant)
                        .measurementPeriod("30d")
                        .measuredAt(LocalDateTime.now())
                        .build());
            }
        }
    }

    private void seedIncidentRecords() {
        log.info("Seeding incident records...");

        // Resolved P2 incident
        incidentRecordRepository.save(IncidentRecord.builder()
                .title("Database connection pool exhaustion on api-claims")
                .severity(IncidentRecord.Severity.P2)
                .status(IncidentRecord.IncidentStatus.RESOLVED)
                .affectedServices(List.of("api-claims", "portal-claims-advisor"))
                .affectedTenants(List.of("tenant-001", "tenant-002", "tenant-003"))
                .startedAt(LocalDateTime.now().minusDays(5).minusHours(3))
                .resolvedAt(LocalDateTime.now().minusDays(5))
                .rootCause("Connection pool max size too low for peak traffic; increased from 20 to 50")
                .createdBy("oncall-engineer")
                .build());

        // Resolved P3 incident
        incidentRecordRepository.save(IncidentRecord.builder()
                .title("Elevated error rate on api-members health checks")
                .severity(IncidentRecord.Severity.P3)
                .status(IncidentRecord.IncidentStatus.RESOLVED)
                .affectedServices(List.of("api-members"))
                .affectedTenants(List.of("tenant-004"))
                .startedAt(LocalDateTime.now().minusDays(2).minusHours(1))
                .resolvedAt(LocalDateTime.now().minusDays(2))
                .rootCause("Downstream dependency timeout; adjusted circuit breaker thresholds")
                .createdBy("monitoring-bot")
                .build());

        // Active P3 incident
        incidentRecordRepository.save(IncidentRecord.builder()
                .title("Intermittent latency spikes on portal-tenants")
                .severity(IncidentRecord.Severity.P3)
                .status(IncidentRecord.IncidentStatus.INVESTIGATING)
                .affectedServices(List.of("portal-tenants"))
                .affectedTenants(List.of("tenant-005", "tenant-006"))
                .startedAt(LocalDateTime.now().minusHours(2))
                .createdBy("sre-team")
                .build());

        // Active P4 incident
        incidentRecordRepository.save(IncidentRecord.builder()
                .title("Non-critical: Swagger UI intermittently unavailable on api-entitlements")
                .severity(IncidentRecord.Severity.P4)
                .status(IncidentRecord.IncidentStatus.OPEN)
                .affectedServices(List.of("api-entitlements"))
                .affectedTenants(List.of())
                .startedAt(LocalDateTime.now().minusHours(6))
                .createdBy("dev-team")
                .build());
    }
}
