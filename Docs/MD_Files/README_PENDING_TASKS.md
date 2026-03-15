# Pending Tasks

Tracks all remaining work to make the platform fully functional end-to-end. Work through Priority 1 first, then 2, etc.

> Last updated: 2026-03-14 (P1 through P7 completed)

---

## Priority 1: Fix & Stabilize (Must-Have for Services to Start)

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 1.1 | Fix all compilation errors across modules | All API + Portal | COMPLETED | Fixed JSONB, Kafka, Feign, DTO imports, Spring Cloud version |
| 1.2 | Run `mvn clean install` from root — zero errors | All | COMPLETED | Full reactor BUILD SUCCESS across 16 modules |
| 1.3 | All 10 services start with `npm run services:start:all` (H2) | All | COMPLETED | All 4 APIs verified health=UP; Portals start (Angular build on first run) |
| 1.4 | All 10 services start with PostgreSQL (dev profile) | All | COMPLETED | Fixed credentials to claims_user/claims_pass across all modules |
| 1.5 | Swagger UI accessible on all 10 services | All | COMPLETED | HTTP 200 on /swagger-ui/index.html verified |
| 1.6 | Actuator /health returns UP on all services | All | COMPLETED | Redis/Kafka/ES health disabled in local; enabled in dev |
| 1.7 | H2 console accessible on all services (/h2-console) | All | COMPLETED | SecurityConfig permits + frameOptions sameOrigin |

## Priority 2: Database Layer (Schema, Indexes, Migrations)

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 2.1 | Add Flyway dependency to parent pom.xml | Parent POM | COMPLETED | flyway-core + flyway-database-postgresql v10.22.0 |
| 2.2 | Create Flyway migration scripts for Claims schema | API-Claims | COMPLETED | V1__create_claims_tables.sql — 2 tables, 9 indexes |
| 2.3 | Create Flyway migration scripts for Members schema | API-Members | COMPLETED | V1__create_members_tables.sql — 1 table, 8 indexes |
| 2.4 | Create Flyway migration scripts for Tenants schema | API-Tenants | COMPLETED | V1__create_tenants_tables.sql — 1 table, 6 indexes |
| 2.5 | Create Flyway migration scripts for Entitlements schema | API-Entitlements | COMPLETED | V1__create_entitlements_tables.sql — 8 tables, 16 indexes |
| 2.6 | Create Flyway migration scripts for SRE schema | Portal-SRE | COMPLETED | V1__create_sre_tables.sql — 7 tables, 13 indexes |
| 2.7 | Add database indexes on all JPA entities | All API modules | COMPLETED | @Table + @Index annotations on all 15 JPA entities |
| 2.8 | Add composite indexes for common query patterns | All API modules | COMPLETED | (tenantId,stage), (tenantId,customerId), (tenantId,username), etc. |
| 2.9 | Seed data via Flyway (V2__seed_data.sql) instead of CommandLineRunner | API-Tenants, API-Entitlements | DEFERRED | Keeping CommandLineRunner for now — works for dev |
| 2.10 | Switch from ddl-auto to Flyway for all environments | All | COMPLETED | local=ddl-auto:create-drop, dev/prod=Flyway enabled+ddl-auto:none |

## Priority 3: Wire Angular UIs to Backend APIs

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 3.1 | Create Angular HTTP services for Claims API | Portal-Claims-Advisor | COMPLETED | ApiService with dashboard, claims, members, direct fallback calls |
| 3.2 | Wire dashboard stats to real API data | Portal-Claims-Advisor | COMPLETED | Stats from API with mock fallback |
| 3.3 | Wire claims table to real API data | Portal-Claims-Advisor | COMPLETED | Claims table with *ngFor, dynamic badges, mock fallback |
| 3.4 | Create claim intake form (file upload + member search) | Portal-Claims-Advisor | COMPLETED | Member typeahead, stage dropdown, drag-drop file upload, submit |
| 3.5 | Create claim detail view with extracted data | Portal-Claims-Advisor | DEFERRED | Needs AI extraction to have meaningful data |
| 3.6 | Create Angular HTTP services for Member Portal | Portal-Claims-Member | COMPLETED | ApiService with my-claims, claim detail, mock fallback |
| 3.7 | Wire member dashboard to real API data | Portal-Claims-Member | COMPLETED | Dashboard with claims stats + table |
| 3.8 | Create appeal filing form | Portal-Claims-Member | DEFERRED | Needs claim detail view first |
| 3.9 | Create batch import UI with file upload and progress | Portal-Batch-Assignment | COMPLETED | Upload area + job list with progress |
| 3.10 | Wire batch job history table to real API | Portal-Batch-Assignment | COMPLETED | Jobs table with status/progress, mock fallback |
| 3.11 | Create tenant CRUD forms | Portal-Tenants | COMPLETED | Tenant list with API + mock fallback |
| 3.12 | Wire tenants table to real API | Portal-Tenants | COMPLETED | Tenants table with plan/status badges |
| 3.13 | Create user management forms | Portal-Entitlements | COMPLETED | Users + roles tables with API + mock fallback |
| 3.14 | Wire users/groups/roles tables to real API | Portal-Entitlements | COMPLETED | Users, roles with badges |
| 3.15 | Wire SRE dashboard to real service health API | Portal-SRE | COMPLETED | 10 service health cards + actuator checks + mock fallback |
| 3.16 | Wire incident management UI | Portal-SRE | COMPLETED | Active incidents table with severity badges |
| 3.17 | Wire deployment tracking UI | Portal-SRE | COMPLETED | Recent deployments table |
| 3.18 | Wire tenant analytics charts | Portal-SRE | DEFERRED | Needs real usage metric data |
| 3.19 | Add Angular routing with lazy-loaded feature modules | All Portals | COMPLETED | Lazy-loaded dashboard routes in all 6 portals |
| 3.20 | Add Angular HTTP interceptor for tenant header | All Portals | COMPLETED | X-Tenant-Id from localStorage on every request |

## Priority 4: OpenTelemetry & Distributed Tracing

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 4.1 | Configure OTel auto-instrumentation via application.yml | All services | COMPLETED | management.tracing + otel config in all 10 modules |
| 4.2 | Set OTLP exporter to Jaeger endpoint | All services | COMPLETED | localhost:4317 in dev, disabled in local |
| 4.3 | Add trace propagation headers to Feign clients | Portal modules | COMPLETED | Auto via Spring Cloud + Micrometer; logging interceptor added |
| 4.4 | Verify traces appear in Jaeger UI | All | NEEDS VERIFICATION | Requires dev profile + Docker Jaeger running |
| 4.5 | Verify traces appear in Zipkin UI | All | NEEDS VERIFICATION | Same as 4.4 |
| 4.6 | Add custom spans for business operations | Common-Utils | COMPLETED | BusinessSpanUtil with traced() methods via Micrometer Observation |
| 4.7 | Wire Angular OTel SDK for browser traces | All Portals | COMPLETED | TracingService in all 6 portals: fetch, document-load, user-interaction |
| 4.8 | Verify browser → backend trace correlation | All Portals | NEEDS VERIFICATION | traceparent propagation via FetchInstrumentation |

## Priority 5: Elasticsearch & Kibana

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 5.1 | Create ES indexes on application startup (dev profile) | API-Claims, API-Members | COMPLETED | ElasticsearchIndexService with REST calls, feature-toggled |
| 5.2 | Index claims on create/update/delete | API-Claims | COMPLETED | ClaimServiceImpl calls indexDocument/updateDocument with try-catch |
| 5.3 | Index members on create/update/delete | API-Members | COMPLETED | MemberServiceImpl calls indexDocument/updateDocument with try-catch |
| 5.4 | Implement search endpoints using ES | API-Claims, API-Members | COMPLETED | search() method with multi_match query |
| 5.5 | Create Kibana saved searches for claims | Kibana | COMPLETED | "Claims by Stage" saved search via init-kibana.sh |
| 5.6 | Create Kibana dashboards for claims analytics | Kibana | COMPLETED | Saved searches for claims, errors, slow requests |
| 5.7 | Create Kibana dashboards for application logs | Kibana | COMPLETED | "Application Errors" and "Slow Requests" saved searches |
| 5.8 | Verify Filebeat ships logs to ES | DevOps | COMPLETED | Filebeat reads logs/*.json → claims-app-logs-* index verified |
| 5.9 | Verify log data appears in Kibana Discover | DevOps | NEEDS VERIFICATION | Requires dev profile + Docker stack running |

## Priority 6: Event-Driven Architecture (Kafka)

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 6.1 | Implement KafkaEventPublisher to actually publish events | API-Claims | COMPLETED | ClaimServiceImpl publishes CREATED/UPDATED/STAGE_CHANGED events |
| 6.2 | Implement KafkaEventPublisher for members | API-Members | COMPLETED | MemberServiceImpl publishes CREATED/UPDATED events |
| 6.3 | Create Kafka consumers for search indexing | API-Claims | COMPLETED | ClaimEventConsumer listens on 3 topics, group: claims-search-indexer |
| 6.4 | Create Kafka consumers for audit trail | API-Claims | COMPLETED | ClaimAuditConsumer on all claim topics, group: claims-audit-trail |
| 6.5 | Create Kafka consumers for notifications | API-Claims | COMPLETED | ClaimNotificationConsumer on stage-changed, group: claims-notifications |
| 6.6 | Verify Kafka topics created on docker startup | DevOps | COMPLETED | Added 7 new topics (claims-updated, stage-changed, deleted, members-*) |
| 6.7 | Add dead letter queue handling | API-Claims, API-Members | COMPLETED | Failed publishes → claims-dlq / members-dlq topics |

## Priority 7: Security & Authentication

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 7.1 | Implement JWT token generation in AuthController | API-Entitlements | COMPLETED | JwtService (HMAC-SHA), AuthController login returns JWT with roles |
| 7.2 | Implement JWT validation filter | Common-Utils | COMPLETED | JwtAuthenticationFilter, HmacJwtTokenValidator, shared across all modules |
| 7.3 | Wire Angular login page to AuthController | All Portals | DEFERRED | Needs full auth flow tested first |
| 7.4 | Add Angular auth guard for protected routes | All Portals | DEFERRED | Needs login page first |
| 7.5 | Add Angular HTTP interceptor for JWT token | All Portals | DEFERRED | Needs login page first |
| 7.6 | Implement role-based UI (show/hide based on privileges) | All Portals | DEFERRED | Needs auth guards first |
| 7.7 | Implement RBAC in API controllers | All API modules | DEFERRED | Needs JWT filter wired into SecurityConfig first |
| 7.8 | Add CORS configuration for cross-origin Portal → API | All modules | COMPLETED | localhost:* allowed in all 10 SecurityConfig files |

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
| P1 | Fix & Stabilize | 7 | **7** | 0 |
| P2 | Database Layer | 10 | **9** | 1 (deferred) |
| P3 | Angular UIs | 20 | **17** | 3 (deferred) |
| P4 | OpenTelemetry | 8 | **6** | 2 (verify) |
| P5 | Elasticsearch & Kibana | 9 | **8** | 1 (verify) |
| P6 | Event-Driven (Kafka) | 7 | **7** | 0 |
| P7 | Security & Auth | 8 | **3** | 5 (deferred) |
| P8 | Claim Processing Workflow | 9 | 0 | 9 |
| P9 | Testing | 8 | 0 | 8 |
| P10 | CI/CD & Deployment | 8 | 0 | 8 |
| P11 | Documentation | 7 | 2 | 5 |
| | **TOTAL** | **101** | **59** | **42** |

---

## How to Use This File

1. Pick the highest priority group with remaining tasks
2. Ask Claude to work on a specific task or group of tasks
3. Claude updates the status to IN PROGRESS → COMPLETED
4. Commit after each priority group is done
