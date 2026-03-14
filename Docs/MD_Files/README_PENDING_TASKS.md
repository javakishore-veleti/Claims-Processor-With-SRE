# Pending Tasks

Tracks all remaining work to make the platform fully functional end-to-end. Work through Priority 1 first, then 2, etc.

> Last updated: 2026-03-14

---

## Priority 1: Fix & Stabilize (Must-Have for Services to Start)

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 1.1 | Fix all compilation errors across modules | All API + Portal | NOT STARTED | Several modules have import/class mismatches from DTO refactoring |
| 1.2 | Run `mvn clean install` from root — zero errors | All | NOT STARTED | Full reactor build must pass |
| 1.3 | All 10 services start with `npm run services:start:all` (H2) | All | PARTIAL | Some services fail due to compilation or config errors |
| 1.4 | All 10 services start with PostgreSQL (dev profile) | All | PARTIAL | DB credential mismatches fixed but untested end-to-end |
| 1.5 | Swagger UI accessible on all 10 services | All | NOT VERIFIED | springdoc dependency present, needs endpoint verification |
| 1.6 | Actuator /health returns UP on all services | All | NOT VERIFIED | |
| 1.7 | H2 console accessible on all services (/h2-console) | All | NOT VERIFIED | SecurityConfig permits, needs frame-options verification |

## Priority 2: Database Layer (Schema, Indexes, Migrations)

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 2.1 | Add Flyway dependency to parent pom.xml | Parent POM | NOT STARTED | `org.flywaydb:flyway-core` + `flyway-database-postgresql` |
| 2.2 | Create Flyway migration scripts for Claims schema | API-Claims | NOT STARTED | V1__create_claims_table.sql with indexes |
| 2.3 | Create Flyway migration scripts for Members schema | API-Members | NOT STARTED | V1__create_members_table.sql with indexes |
| 2.4 | Create Flyway migration scripts for Tenants schema | API-Tenants | NOT STARTED | V1__create_tenants_table.sql with indexes |
| 2.5 | Create Flyway migration scripts for Entitlements schema | API-Entitlements | NOT STARTED | V1__create_entitlements_tables.sql (9 tables) with indexes |
| 2.6 | Create Flyway migration scripts for SRE schema | Portal-SRE | NOT STARTED | V1__create_sre_tables.sql (5 tables) |
| 2.7 | Add database indexes on all JPA entities | All API modules | NOT STARTED | tenantId, claimNumber, memberId, username, email, stage, status, createdAt |
| 2.8 | Add composite indexes for common query patterns | All API modules | NOT STARTED | (tenantId, stage), (tenantId, customerId), (tenantId, username) |
| 2.9 | Seed data via Flyway (V2__seed_data.sql) instead of CommandLineRunner | API-Tenants, API-Entitlements | NOT STARTED | More reliable than code-based seeding |
| 2.10 | Switch from ddl-auto to Flyway for all environments | All | NOT STARTED | ddl-auto=none + Flyway for all non-local profiles |

## Priority 3: Wire Angular UIs to Backend APIs

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 3.1 | Create Angular HTTP services for Claims API | Portal-Claims-Advisor | NOT STARTED | ClaimsService calling /api/v1/advisor/* |
| 3.2 | Wire dashboard stats to real API data | Portal-Claims-Advisor | NOT STARTED | Replace mock stat cards with API responses |
| 3.3 | Wire claims table to real API data | Portal-Claims-Advisor | NOT STARTED | Replace mock table rows with paginated API data |
| 3.4 | Create claim intake form (file upload + member search) | Portal-Claims-Advisor | NOT STARTED | Multi-file upload, member lookup, stage dropdown |
| 3.5 | Create claim detail view with extracted data | Portal-Claims-Advisor | NOT STARTED | Side-by-side: document viewer + extracted fields |
| 3.6 | Create Angular HTTP services for Member Portal | Portal-Claims-Member | NOT STARTED | My claims, status tracking, EOB view |
| 3.7 | Wire member dashboard to real API data | Portal-Claims-Member | NOT STARTED | |
| 3.8 | Create appeal filing form | Portal-Claims-Member | NOT STARTED | |
| 3.9 | Create batch import UI with file upload and progress | Portal-Batch-Assignment | NOT STARTED | Excel upload, validation preview, progress bar |
| 3.10 | Wire batch job history table to real API | Portal-Batch-Assignment | NOT STARTED | |
| 3.11 | Create tenant CRUD forms | Portal-Tenants | NOT STARTED | Create, edit, activate/suspend tenant |
| 3.12 | Wire tenants table to real API | Portal-Tenants | NOT STARTED | |
| 3.13 | Create user management forms | Portal-Entitlements | NOT STARTED | Create user, assign groups/roles, edit privileges |
| 3.14 | Wire users/groups/roles tables to real API | Portal-Entitlements | NOT STARTED | |
| 3.15 | Wire SRE dashboard to real service health API | Portal-SRE | NOT STARTED | Live /actuator/health calls to all 10 services |
| 3.16 | Wire incident management UI | Portal-SRE | NOT STARTED | Create, update, resolve incidents |
| 3.17 | Wire deployment tracking UI | Portal-SRE | NOT STARTED | |
| 3.18 | Wire tenant analytics charts | Portal-SRE | NOT STARTED | Usage metrics, cost estimates |
| 3.19 | Add Angular routing with lazy-loaded feature modules | All Portals | NOT STARTED | Separate routes for each sidebar section |
| 3.20 | Add Angular HTTP interceptor for tenant header | All Portals | NOT STARTED | X-Tenant-Id header on every API call |

## Priority 4: OpenTelemetry & Distributed Tracing

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 4.1 | Configure OTel auto-instrumentation via application.yml | All services | NOT STARTED | management.tracing.*, otel exporter endpoint |
| 4.2 | Set OTLP exporter to Jaeger endpoint | All services | NOT STARTED | otel.exporter.otlp.endpoint=http://localhost:4317 |
| 4.3 | Add trace propagation headers to Feign clients | Portal modules | NOT STARTED | W3C TraceContext propagation |
| 4.4 | Verify traces appear in Jaeger UI | All | NOT STARTED | End-to-end trace: Portal → API → DB |
| 4.5 | Verify traces appear in Zipkin UI | All | NOT STARTED | |
| 4.6 | Add custom spans for business operations | API-Claims | NOT STARTED | claim.intake, claim.extraction, claim.adjudication spans |
| 4.7 | Wire Angular OTel SDK for browser traces | All Portals | NOT STARTED | Document load, fetch, user interaction instrumentation |
| 4.8 | Verify browser → backend trace correlation | All Portals | NOT STARTED | traceparent header from Angular to Spring |

## Priority 5: Elasticsearch & Kibana

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 5.1 | Create ES indexes on application startup (dev profile) | API-Claims, API-Members | NOT STARTED | Using ElasticsearchIndexService |
| 5.2 | Index claims on create/update/delete | API-Claims | NOT STARTED | Feature toggle: claims.features.search.enabled |
| 5.3 | Index members on create/update/delete | API-Members | NOT STARTED | Feature toggle: members.features.search.enabled |
| 5.4 | Implement search endpoints using ES | API-Claims, API-Members | NOT STARTED | Full-text search across claim/member fields |
| 5.5 | Create Kibana saved searches for claims | Kibana | NOT STARTED | Pre-built searches: claims by stage, by provider, by amount range |
| 5.6 | Create Kibana dashboards for claims analytics | Kibana | NOT STARTED | Claims volume, stage distribution, processing time |
| 5.7 | Create Kibana dashboards for application logs | Kibana | NOT STARTED | Error rate, slow requests, service-level log analysis |
| 5.8 | Verify Filebeat ships logs to ES | DevOps | NOT STARTED | JSON logs → claims-app-logs-* index |
| 5.9 | Verify log data appears in Kibana Discover | DevOps | NOT STARTED | |

## Priority 6: Event-Driven Architecture (Kafka)

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 6.1 | Implement KafkaEventPublisher to actually publish events | API-Claims | NOT STARTED | Feature toggle: claims.features.event-stream.enabled |
| 6.2 | Implement KafkaEventPublisher for members | API-Members | NOT STARTED | |
| 6.3 | Create Kafka consumers for search indexing | API-Claims, API-Members | NOT STARTED | Consume events → index in ES |
| 6.4 | Create Kafka consumers for audit trail | API-Claims | NOT STARTED | Persist audit records |
| 6.5 | Create Kafka consumers for notifications | API-Claims | NOT STARTED | Stage change → SNS/email notification |
| 6.6 | Verify Kafka topics created on docker startup | DevOps | NOT STARTED | claims-submitted, claims-validated, etc. |
| 6.7 | Add dead letter queue handling | API-Claims | NOT STARTED | Failed messages → claims-dlq topic |

## Priority 7: Security & Authentication

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 7.1 | Implement JWT token generation in AuthController | API-Entitlements | NOT STARTED | Login → JWT with roles/privileges |
| 7.2 | Implement JWT validation filter | All services | NOT STARTED | Feature-toggled: claims.service-client.auth.provider=jwt |
| 7.3 | Wire Angular login page to AuthController | All Portals | NOT STARTED | Login form → JWT → store in localStorage |
| 7.4 | Add Angular auth guard for protected routes | All Portals | NOT STARTED | Redirect to login if no token |
| 7.5 | Add Angular HTTP interceptor for JWT token | All Portals | NOT STARTED | Authorization: Bearer {token} on every API call |
| 7.6 | Implement role-based UI (show/hide based on privileges) | All Portals | NOT STARTED | *ngIf based on user roles |
| 7.7 | Implement RBAC in API controllers | All API modules | NOT STARTED | @PreAuthorize based on privileges |
| 7.8 | Add CORS configuration for cross-origin Portal → API | All API modules | NOT STARTED | |

## Priority 8: Claim Processing Workflow

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 8.1 | Implement file upload endpoint (S3/local storage) | API-Claims | NOT STARTED | Feature-toggled: S3, Azure Blob, GCS, local filesystem |
| 8.2 | Implement document classification logic | API-Claims | NOT STARTED | Determine doc type: CMS-1500, EOB, bill, lab report |
| 8.3 | Integrate with AI for data extraction (mock first) | API-Claims | NOT STARTED | Mock AI response → real Bedrock/Vertex/Azure AI later |
| 8.4 | Implement extraction review workflow | API-Claims | NOT STARTED | Staff corrects extracted fields, advances stage |
| 8.5 | Implement eligibility check logic | API-Claims | NOT STARTED | Verify member policy covers the claim |
| 8.6 | Implement adjudication rules engine | API-Claims | NOT STARTED | Business rules → approve/deny/partial recommendation |
| 8.7 | Implement confidence-based auto-routing | API-Claims | NOT STARTED | >= 95% auto-approve, < 70% escalate |
| 8.8 | Implement EOB generation | API-Claims | NOT STARTED | Generate Explanation of Benefits PDF |
| 8.9 | Implement claim stage transition validation | API-Claims | NOT STARTED | Enforce valid stage transitions |

## Priority 9: Testing

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 9.1 | Unit tests for all service classes | All API modules | NOT STARTED | JUnit 5 + Mockito |
| 9.2 | Unit tests for all controller classes | All API modules | NOT STARTED | MockMvc tests |
| 9.3 | Integration tests with Testcontainers | All API modules | NOT STARTED | PostgreSQL, Kafka, Redis, Elasticsearch |
| 9.4 | API contract tests | All API modules | NOT STARTED | Spring Cloud Contract or Pact |
| 9.5 | Angular unit tests (Jasmine/Karma) | All Portals | NOT STARTED | Component and service tests |
| 9.6 | Angular E2E tests (Cypress or Playwright) | All Portals | NOT STARTED | Full UI workflow tests |
| 9.7 | Performance/load tests (k6 or Gatling) | All API modules | NOT STARTED | Baseline performance metrics |
| 9.8 | Chaos engineering tests (Gremlin) | All | NOT STARTED | Circuit breaker and resilience verification |

## Priority 10: CI/CD & Deployment

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 10.1 | Create GitHub Actions CI workflow | Root | NOT STARTED | Build → test → code quality → security scan |
| 10.2 | Create Dockerfiles for all 10 services | All | NOT STARTED | Multi-stage builds |
| 10.3 | Create docker-compose for full application stack | Root | NOT STARTED | All services + infrastructure |
| 10.4 | Create Kubernetes manifests (Deployment, Service, ConfigMap) | All | NOT STARTED | Helm charts or Kustomize |
| 10.5 | Create AWS deployment (CloudFormation/Terraform) | Root | NOT STARTED | RDS, MSK, ElastiCache, ECS/EKS |
| 10.6 | Create Azure deployment (ARM/Bicep) | Root | NOT STARTED | Azure SQL, Event Hub, Redis, AKS |
| 10.7 | Create GCP deployment (Terraform) | Root | NOT STARTED | Cloud SQL, Pub/Sub, Memorystore, GKE |
| 10.8 | Create GitHub Actions CD workflow | Root | NOT STARTED | Deploy to staging → smoke test → prod |

## Priority 11: Documentation & Polish

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 11.1 | API documentation with examples in Swagger | All API modules | NOT STARTED | @Operation, @ApiResponse, @Schema annotations |
| 11.2 | Postman collection for all APIs | Root | NOT STARTED | Import-ready collection with environments |
| 11.3 | Architecture decision records (ADRs) | Docs | NOT STARTED | Why CQRS, why Feign, why multi-tenant, etc. |
| 11.4 | Runbook for each alert rule | Docs | NOT STARTED | What to do when each alert fires |
| 11.5 | Update CLAUDE.md with final architecture | Root | ONGOING | Keep in sync with implementation |
| 11.6 | Update Instructions_To_Claude.md | Root | ONGOING | Track all instructions |
| 11.7 | Update index.html with any new services/tools | Root | ONGOING | |

---

## Summary

| Priority | Category | Total Tasks | Completed | Remaining |
|---|---|---|---|---|
| P1 | Fix & Stabilize | 7 | 0 | 7 |
| P2 | Database Layer | 10 | 0 | 10 |
| P3 | Angular UIs | 20 | 0 | 20 |
| P4 | OpenTelemetry | 8 | 0 | 8 |
| P5 | Elasticsearch & Kibana | 9 | 0 | 9 |
| P6 | Event-Driven (Kafka) | 7 | 0 | 7 |
| P7 | Security & Auth | 8 | 0 | 8 |
| P8 | Claim Processing Workflow | 9 | 0 | 9 |
| P9 | Testing | 8 | 0 | 8 |
| P10 | CI/CD & Deployment | 8 | 0 | 8 |
| P11 | Documentation | 7 | 2 | 5 |
| | **TOTAL** | **101** | **2** | **99** |

---

## How to Use This File

1. Pick the highest priority group with remaining tasks
2. Ask Claude to work on a specific task or group of tasks
3. Claude updates the status to IN PROGRESS → COMPLETED
4. Commit after each priority group is done
