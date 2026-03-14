# Deployment & Technical Guide

Technical reference for profiles, feature toggles, Docker infrastructure, Kubernetes deployment, CI/CD, and production observability.

---

## Profile Strategy

The application uses Spring Boot profiles composed in layers: an **environment profile** (where it runs) combined with an optional **cloud profile** (which cloud provider).

### Environment Profiles

| Profile | Database | DDL Strategy | Logging | Actuator Endpoints | Feature Toggles |
|---------|----------|-------------|---------|-------------------|-----------------|
| `local` | H2 in-memory | create-drop | DEBUG | All exposed | Disabled |
| `dev` | PostgreSQL (localhost) | update | DEBUG | All exposed | Enabled |
| `test` | PostgreSQL (env vars) | validate | INFO | Limited | Enabled |
| `staging` | PostgreSQL (env vars) | validate | WARN/INFO | Limited | Enabled |
| `pre-prod` | PostgreSQL (env vars) | none | INFO | health, info, prometheus | Enabled |
| `prod` | PostgreSQL (env vars) | none | WARN | health, prometheus | Enabled |

**DDL Strategy notes**:
- `create-drop`: Database schema created on startup, dropped on shutdown. For local dev only.
- `update`: Hibernate auto-updates schema to match entities. For dev environments where schema changes frequently.
- `validate`: Hibernate validates that the schema matches entities but makes no changes. Schema managed by Flyway/Liquibase.
- `none`: No DDL operations. Schema fully managed by migration tools and DBA processes.

### Cloud Profiles

| Profile | Database | Cache | Events | Search | Secrets | AI |
|---------|----------|-------|--------|--------|---------|-----|
| `aws` | RDS PostgreSQL | ElastiCache (Redis) | MSK (Kafka) | OpenSearch | Secrets Manager | Bedrock |
| `azure` | Azure SQL | Azure Redis (SSL) | Event Hub | Cognitive Search | Key Vault | Azure AI |
| `gcp` | Cloud SQL | Memorystore | Pub/Sub | Enterprise Search | Secret Manager | Vertex AI |

### Profile Composition

Combine one environment profile with one cloud profile:

```properties
# Dev environment on AWS
spring.profiles.active=dev,aws

# Production on Azure
spring.profiles.active=prod,azure

# Staging on GCP
spring.profiles.active=staging,gcp

# Local development (H2, no cloud -- no cloud profile needed)
spring.profiles.active=local
```

Profile composition is additive. The environment profile sets the database behavior, logging, and actuator exposure. The cloud profile configures cloud-specific service implementations (which cache provider, which event bus, etc.).

### Profile Precedence

Configuration resolution order (later overrides earlier):
1. `application.yml` -- base defaults
2. `application-{environment}.yml` -- environment-specific (e.g., `application-dev.yml`)
3. `application-{cloud}.yml` -- cloud-specific (e.g., `application-aws.yml`)
4. Environment variables -- highest priority, used in containers and K8s

---

## Feature Toggles

Feature toggles allow services to run with or without external dependencies. All toggles are disabled by default in `local` profile and enabled in `dev`+ profiles.

### Configuration Reference

```yaml
claims.features:
  search:
    enabled: false
    provider: elasticsearch    # opensearch | azure-cognitive | gcp-search
  event-stream:
    enabled: false
    provider: kafka            # kinesis | eventhub | pubsub
  log-shipping:
    enabled: false
    destination: elasticsearch # cloudwatch | azure-monitor | gcp-logging
  cache:
    enabled: false
    provider: redis            # elasticache | azure-redis | memorystore
  metrics:
    enabled: true
    exporter: prometheus       # cloudwatch | azure-monitor | gcp-monitoring
```

**Behavior when disabled**:
- `search.enabled=false`: Search endpoints return empty results. No connection to Elasticsearch/OpenSearch.
- `event-stream.enabled=false`: Events are logged to console instead of published to Kafka/Kinesis/etc.
- `log-shipping.enabled=false`: Logs written to stdout/file only. No shipping to centralized logging.
- `cache.enabled=false`: No caching layer. All reads go directly to the database.
- `metrics.enabled=true`: Metrics are always enabled. The exporter determines where they are sent.

### Overriding Toggles via Environment Variables

```bash
# Enable search with OpenSearch
CLAIMS_FEATURES_SEARCH_ENABLED=true
CLAIMS_FEATURES_SEARCH_PROVIDER=opensearch

# Enable event streaming with Kafka
CLAIMS_FEATURES_EVENT_STREAM_ENABLED=true
CLAIMS_FEATURES_EVENT_STREAM_PROVIDER=kafka

# Enable caching with Redis
CLAIMS_FEATURES_CACHE_ENABLED=true
CLAIMS_FEATURES_CACHE_PROVIDER=redis
```

### Inter-Service Authentication

```yaml
claims.service-client:
  auth:
    enabled: false
    provider: none             # basic | jwt | cognito | azure-ad | gcp-iam
```

| Provider | Description | Use Case |
|----------|-------------|----------|
| `none` | No authentication between services | Local development |
| `basic` | HTTP Basic Auth with shared credentials | Dev/test environments |
| `jwt` | JWT token-based service-to-service auth | Self-managed Kubernetes |
| `cognito` | AWS Cognito machine-to-machine tokens | AWS deployments |
| `azure-ad` | Azure AD client credentials flow | Azure deployments |
| `gcp-iam` | GCP IAM service account tokens | GCP deployments |

### ID Encryption

```yaml
claims.crypto:
  enabled: false               # true for production
  master-key: "your-32-char-key"
```

| Environment | `enabled` | Behavior |
|-------------|-----------|----------|
| local, dev | `false` | NoOp encryption -- IDs pass through unchanged |
| test, staging | `true` | AES-256 encryption with test key |
| pre-prod, prod | `true` | AES-256 encryption with key from secrets manager |

When enabled, all entity IDs in API responses are encrypted. Clients send encrypted IDs back in requests, and the service decrypts them before database lookup. This prevents enumeration attacks and hides internal database sequences.

---

## Docker Infrastructure

### Local Development Stack

| Container | Image | Port | Purpose |
|-----------|-------|------|---------|
| `Claims-Processor-PostgreSQL` | postgres:15 | 5432 | Primary database |
| `Claims-Processor-Redis` | redis:7 | 6379 | Caching layer |
| `Claims-Processor-Kafka` | confluentinc/cp-kafka | 9092 | Event streaming |
| `Claims-Processor-Zookeeper` | confluentinc/cp-zookeeper | 2181 | Kafka coordination |
| `Claims-Processor-Elasticsearch` | elasticsearch:8.11 | 9200 | Search and log storage |
| `Claims-Processor-Logstash` | logstash:8.11 | 5044 | Log pipeline |
| `Claims-Processor-Kibana` | kibana:8.11 | 5601 | Log visualization |
| `Claims-Processor-Prometheus` | prom/prometheus | 9090 | Metrics collection |
| `Claims-Processor-Grafana` | grafana/grafana | 3000 | Metrics dashboards |
| `Claims-Processor-Jaeger` | jaegertracing/all-in-one | 16686 | Distributed tracing |

### Container Naming Convention

All containers are prefixed with `Claims-Processor-` and communicate over a shared Docker bridge network named `claims-processor-network`. This naming convention:
- Prevents collisions with other projects running on the same machine
- Makes it easy to identify project containers in `docker ps` output
- Allows Docker Compose to resolve service names within the network

### Data Persistence

Docker volumes are used for data persistence across container restarts:

| Volume | Container | Purpose |
|--------|-----------|---------|
| `claims-postgres-data` | PostgreSQL | Database files |
| `claims-elasticsearch-data` | Elasticsearch | Index data |
| `claims-grafana-data` | Grafana | Dashboard configs |
| `claims-prometheus-data` | Prometheus | Metrics history |

To reset all data:

```bash
npm run devops:stop:all
docker volume rm $(docker volume ls -q --filter name=claims-)
npm run devops:start:all
```

---

## Kubernetes Deployment

### Environment Variable Mapping

Non-local profiles expect configuration values from environment variables. In Kubernetes, these map to ConfigMaps and Secrets.

**ConfigMaps** (non-sensitive configuration):

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: claims-processor-config
data:
  SPRING_PROFILES_ACTIVE: "prod,aws"
  SPRING_DATASOURCE_URL: "jdbc:postgresql://claims-db.cluster.local:5432/claimsdb"
  CLAIMS_FEATURES_SEARCH_ENABLED: "true"
  CLAIMS_FEATURES_SEARCH_PROVIDER: "opensearch"
  CLAIMS_FEATURES_EVENT_STREAM_ENABLED: "true"
  CLAIMS_FEATURES_EVENT_STREAM_PROVIDER: "kafka"
  CLAIMS_FEATURES_CACHE_ENABLED: "true"
  CLAIMS_FEATURES_CACHE_PROVIDER: "elasticache"
  CLAIMS_FEATURES_METRICS_EXPORTER: "cloudwatch"
  CLAIMS_SERVICE_CLIENT_AUTH_PROVIDER: "cognito"
```

**Secrets** (sensitive values):

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: claims-processor-secrets
type: Opaque
stringData:
  SPRING_DATASOURCE_USERNAME: "claims_prod_user"
  SPRING_DATASOURCE_PASSWORD: "encrypted-password"
  CLAIMS_CRYPTO_MASTER_KEY: "32-character-encryption-key-here"
  CLAIMS_SERVICE_CLIENT_AUTH_SECRET: "service-auth-secret"
```

### External Secrets Integration

For production deployments, use External Secrets Operator to sync secrets from cloud secret managers:

```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: claims-processor-secrets
spec:
  refreshInterval: 1h
  secretStoreRef:
    name: aws-secrets-manager    # or azure-key-vault, gcp-secret-manager
    kind: ClusterSecretStore
  target:
    name: claims-processor-secrets
  data:
    - secretKey: SPRING_DATASOURCE_PASSWORD
      remoteRef:
        key: claims-processor/db-password
    - secretKey: CLAIMS_CRYPTO_MASTER_KEY
      remoteRef:
        key: claims-processor/encryption-key
```

### Resource Recommendations

| Service | CPU Request | CPU Limit | Memory Request | Memory Limit |
|---------|-------------|-----------|----------------|--------------|
| API-Gateway | 250m | 500m | 512Mi | 1Gi |
| API-Claims | 250m | 1000m | 512Mi | 1Gi |
| API-Tenants | 100m | 500m | 256Mi | 512Mi |
| API-Plans | 100m | 500m | 256Mi | 512Mi |
| API-Members | 100m | 500m | 256Mi | 512Mi |
| API-Providers | 100m | 500m | 256Mi | 512Mi |
| API-Eligibility | 100m | 500m | 256Mi | 512Mi |
| API-Adjudication | 250m | 1000m | 512Mi | 1Gi |
| API-Payments | 250m | 1000m | 512Mi | 1Gi |

### Health Probes

All services expose standard health endpoints for Kubernetes probes:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 15
  periodSeconds: 5
startupProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
  failureThreshold: 30
```

---

## CI/CD

### GitHub Actions Pipeline

The CI/CD pipeline follows a staged approach (to be fully implemented):

```
Build --> Unit Tests --> Integration Tests --> Code Quality --> Security Scan --> Docker Build --> Deploy
```

#### Pipeline Stages

| Stage | Tool | Purpose | Gate |
|-------|------|---------|------|
| Build | Maven | Compile all modules | Must succeed |
| Unit Tests | JUnit 5 + Mockito | Test business logic in isolation | 80% line coverage |
| Integration Tests | Spring Boot Test + Testcontainers | Test with real databases and services | Must succeed |
| Code Quality | SonarQube / SonarCloud | Static analysis, code smells, duplication | Quality gate pass |
| Security Scan | OWASP Dependency-Check + Trivy | CVE scanning for dependencies and images | No critical/high CVEs |
| Docker Build | Docker Buildx | Multi-arch container images (amd64, arm64) | Image builds successfully |
| Deploy | Helm + ArgoCD | GitOps deployment to target environment | Health checks pass |

#### Branch Strategy

| Branch | Pipeline | Deploy To |
|--------|----------|-----------|
| `feature/*` | Build + Unit Tests + Code Quality | None |
| `develop` | Full pipeline | Dev environment |
| `release/*` | Full pipeline + Security Scan | Staging environment |
| `main` | Full pipeline + Security Scan + Approval | Production |

#### Environment Promotion

```
Dev (automatic) --> Test (automatic) --> Staging (manual gate) --> Pre-Prod (manual gate) --> Prod (manual gate + approval)
```

---

## Observability in Production

### Metrics

All services expose Prometheus metrics at `/actuator/prometheus`. Key metrics collected:

**Application Metrics**:
- `http_server_requests_seconds` -- request latency histograms by endpoint, method, status
- `claims_processed_total` -- business metric: total claims processed
- `claims_adjudication_duration_seconds` -- business metric: adjudication processing time
- `db_connection_pool_active` -- active database connections
- `cache_hits_total` / `cache_misses_total` -- cache effectiveness

**JVM Metrics**:
- `jvm_memory_used_bytes` -- heap and non-heap memory usage
- `jvm_gc_pause_seconds` -- garbage collection pause times
- `jvm_threads_live` -- active thread count

**Infrastructure Metrics**:
- `process_cpu_usage` -- CPU utilization
- `disk_free_bytes` -- available disk space

### Structured Logging

In production, all services emit structured JSON logs to stdout for Kubernetes log collection:

```json
{
  "timestamp": "2026-03-14T10:30:00.123Z",
  "level": "INFO",
  "service": "api-claims",
  "traceId": "abc123def456",
  "spanId": "789ghi",
  "message": "Claim processed successfully",
  "claimId": "CLM-001",
  "tenantId": "T001",
  "duration_ms": 45
}
```

Log collection is handled by the Kubernetes logging stack (Fluentd/Fluent Bit) shipping to the configured destination (Elasticsearch, CloudWatch, Azure Monitor, or GCP Cloud Logging).

### Distributed Tracing

OpenTelemetry traces are exported via OTLP protocol:

- **Local**: Jaeger (http://localhost:16686)
- **AWS**: AWS X-Ray via OTLP collector
- **Azure**: Azure Application Insights
- **GCP**: Google Cloud Trace

Trace context propagation uses W3C TraceContext headers (`traceparent`, `tracestate`) for cross-service correlation.

### Grafana Dashboards

5 dashboards are auto-provisioned via Grafana provisioning:

| Dashboard | Purpose | Key Panels |
|-----------|---------|------------|
| Service Overview | High-level health of all services | Request rate, error rate, latency P50/P95/P99 |
| JVM Performance | Java runtime metrics per service | Heap usage, GC pauses, thread pools |
| Database | Connection pool and query performance | Active connections, query duration, pool wait time |
| Business Metrics | Claims processing KPIs | Claims/hour, adjudication time, approval rate |
| SLI/SLO | Service level indicators and objectives | Availability, latency budgets, error budgets |

### Alert Rules

The project includes pre-configured alert rules:

**Prometheus Alert Rules (28 rules)**:
- High error rate (>1% of requests returning 5xx)
- High latency (P95 > 2s)
- Database connection pool exhaustion (>80% utilization)
- JVM heap usage (>85%)
- Service down (target missing for >1 minute)
- Disk space low (<10% free)
- GC pause time excessive (>500ms)

**Grafana Alert Rules (14 rules)**:
- Dashboard-linked alerts with notification channels
- Business metric thresholds (claims processing backlog, adjudication SLA breach)
- Infrastructure alerts (container restart loops, pod evictions)

### Alert Notification Channels

Configurable notification targets:

| Channel | Use Case |
|---------|----------|
| Slack | Real-time team notifications |
| PagerDuty | On-call escalation for critical alerts |
| Email | Summary digests and non-urgent alerts |
| OpsGenie | Alternative on-call management |
| Webhook | Custom integrations |

### Runbook References

Each alert rule includes a `runbook_url` annotation linking to the corresponding runbook for the on-call engineer. Runbooks cover:
- Alert description and severity
- Investigation steps
- Remediation actions
- Escalation paths

---

## Security Considerations

### Production Hardening Checklist

- [ ] All feature toggles reviewed and set appropriately
- [ ] ID encryption enabled with production-grade master key
- [ ] Inter-service authentication enabled (`jwt`, `cognito`, `azure-ad`, or `gcp-iam`)
- [ ] Actuator endpoints restricted to `health` and `prometheus` only
- [ ] Database credentials sourced from secrets manager (not environment variables)
- [ ] TLS enabled for all inter-service communication
- [ ] Network policies restricting pod-to-pod communication
- [ ] OWASP dependency scan passing with no critical/high CVEs
- [ ] Container images scanned with Trivy
- [ ] Log output sanitized (no PII, no credentials in logs)
- [ ] CORS configured for production domains only
- [ ] Rate limiting enabled on API Gateway
