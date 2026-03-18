# CLAUDE.md

Guidance for Claude Code when working in this repository.

> Architecture diagrams, detailed design, and project objectives are in [Docs/MD_Files/README_Architecture.md](Docs/MD_Files/README_Architecture.md).
> User instructions history is in [Instructions_To_Claude.md](Instructions_To_Claude.md).

## Project Overview

Claims-Processor-With-SRE is a healthcare claims processing microservice with SRE observability. It intakes claim artifacts (images, PDFs, EDI), extracts data via AI, adjudicates claims, and tracks SLOs via AWS CloudWatch Application Signals. Multi-cloud (AWS/Azure/GCP) and local Docker deployment.

**Stack:** Java 17, Spring Boot 3.5.6, Spring Cloud 2025.0.0, Angular 18, Maven multi-module.

**Key patterns:** Multi-tenant (all entities scoped by tenantId), CQRS (API-Tenants, API-Entitlements), Feature toggles (search, events, cache, log-shipping, metrics), OpenFeign for inter-service calls, Resilience4j (circuit breaker, bulkhead, rate limiter), AES-256-GCM ID encryption via Common-Utils.

## Module Map

| Module | Port | Role |
|---|---|---|
| Common-Libraries/Common-Utils | -- | Shared encryption, DTOs (ApiResponse, PagedResponse), JwtAuthFilter |
| Common-Libraries/Common-{Claims,Members,Tenants,Entitlements,Apps} | -- | Shared ReqDTO/RespDTO + FeignClient interfaces |
| Portal-Claims-Advisor | 8081 | Claims staff portal (orchestrator + Angular UI) |
| Portal-Claims-Member | 8082 | Customer/member portal (orchestrator + Angular UI) |
| API-Claims | 8083 | Claims CRUD, search, stage management |
| API-Members | 8084 | Member/customer CRUD, search, lookup |
| Portal-Batch-Assignment | 8085 | Batch member creation, Excel import |
| API-Tenants | 8086 | Tenant CRUD, 100 seed tenants |
| API-Entitlements | 8087 | Users, roles, privileges, groups, auth (login/signup) |
| Portal-Tenants | 8088 | Tenant management UI |
| Portal-Entitlements | 8089 | Entitlements management UI |
| Portal-SRE | 8090 | SRE dashboard (tenant SLO compliance, service health) |

## Build & Run

```bash
# Build all (skip tests)
npm run build            # or: mvn clean install -DskipTests

# Build with tests
npm run build:tests      # or: mvn clean install

# Run single test
mvn -pl API-Claims -Dtest=ApiClaimsApplicationTests test

# Start all services (H2, no Docker needed)
npm run services:start:all

# Start with PostgreSQL
npm run devops:start:all && npm run services:start:all:postgres

# Individual service
npm run services:start:api-claims
npm run services:stop:api-claims

# Docker infrastructure
npm run devops:start:all       # Start containers
npm run devops:stop:all        # Stop containers
npm run devops:status:all      # Check status
```

## Profiles

- `local` -- H2 in-memory, no Docker needed (default for `services:start:*`)
- `dev` -- PostgreSQL + full observability stack
- `dev,aws` / `dev,azure` / `dev,gcp` -- Cloud-specific config
- `dev,aws,aws-signals` -- AWS + CloudWatch Application Signals (ADOT agent + sidecar)
- Environments: local, dev, test, staging, pre-prod, prod

## AWS Blog Feature Toggles

Blog-driven features are opt-in via CloudFormation parameters and standalone GitHub Actions workflows. They don't affect core infrastructure and can be enabled/disabled independently.

| Workflow | Blog | Parameter | What It Toggles |
|---|---|---|---|
| `AWS_100_Blog_SLO_20260313.yml` | [Application Signals SLO](https://aws.amazon.com/about-aws/whats-new/2026/03/cloudwatch-application-signals-adds-slo-capabilities/) | `EnableApplicationSignals` | ADOT sidecar, `aws-signals` profile, 8 SLOs, `TenantContextSpanProcessor` |

`AWS_98` auto-detects and disables all blog toggles before destroying stacks.

## Feature Toggles (API modules)

```yaml
claims.features:  # or members.features, entitlements.features, tenants.features
  search.enabled: false       # elasticsearch | opensearch | azure-cognitive | gcp-search
  event-stream.enabled: false # kafka | kinesis | eventhub | pubsub
  log-shipping.enabled: false # elasticsearch | cloudwatch | azure-monitor | gcp-logging
  cache.enabled: false        # redis | elasticache | azure-redis | memorystore
  metrics.enabled: true       # prometheus | cloudwatch | azure-monitor | gcp-monitoring
```

## Docker Environment Variables

Optional services (default: false, set `=true` to enable):
`CLAIMS_PROCESSOR_APP_LOCAL_KAFKA`, `CLAIMS_PROCESSOR_APP_LOCAL_REDIS`, `CLAIMS_PROCESSOR_APP_LOCAL_ELASTICSEARCH`, `CLAIMS_PROCESSOR_APP_LOCAL_KIBANA`, `CLAIMS_PROCESSOR_APP_LOCAL_FILEBEAT`, `CLAIMS_PROCESSOR_APP_LOCAL_WIREMOCK`, `CLAIMS_PROCESSOR_APP_LOCAL_OLLAMA`

Always-on: Postgres, Prometheus, Alertmanager, Grafana, Jaeger, Zipkin.

## Local Credentials

| Service | User | Password | Connection |
|---|---|---|---|
| H2 | sa | (empty) | jdbc:h2:mem:claimsdb |
| PostgreSQL | claims_user | claims_pass | localhost:5432/claims_db |
| Redis | -- | claims_redis | localhost:6379 |
| Grafana | admin | claims_admin | localhost:3000 |

## Key Files

- `index.html` -- Developer portal with all links, credentials, Swagger URLs
- `.run/` -- IntelliJ run configs (6 per module: H2, Postgres, Local, Dev+AWS/Azure/GCP)
- `DevOps/Local/` -- Docker Compose files per service
- `Docs/MD_Files/` -- Architecture, developer guides, pending tasks
