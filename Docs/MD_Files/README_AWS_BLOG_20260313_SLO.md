# CloudWatch Application Signals — SLO Capabilities Testing

Testing plan for validating the three new CloudWatch Application Signals SLO features announced by AWS, using our 10-microservice healthcare claims platform as the reference implementation.

> **AWS Announcement (March 13, 2026):** [Amazon CloudWatch Application Signals adds new SLO capabilities](https://aws.amazon.com/about-aws/whats-new/2026/03/cloudwatch-application-signals-adds-slo-capabilities/)
>
> **Three new features:**
> 1. **SLO Recommendations** — Analyzes 30 days of service metrics (P99 latency, error rates) to suggest appropriate reliability targets
> 2. **Service-Level SLOs** — Holistic view of service reliability across all operations, bridging technical monitoring and business objectives
> 3. **SLO Performance Report** — Historical analysis aligned with calendar periods (daily, weekly, monthly) for trend identification
>
> **Pricing:** Based on inbound/outbound request count + SLO charges (2 Application Signals per SLI metric period per SLO)
>
> **Supported compute:** Amazon EC2, Amazon ECS, AWS Fargate, AWS Lambda
>
> **Prerequisite:** OpenTelemetry instrumentation via ADOT (AWS Distro for OpenTelemetry)

> Last updated: 2026-03-17

---

## Architecture — Application Signals Integration

### Data Flow: How Telemetry Reaches CloudWatch

```
┌─────────────────────────────────────────────────────────────────────┐
│                        AWS Fargate Task                             │
│                       (awsvpc networking)                           │
│                                                                     │
│  ┌──────────────────────────────┐   ┌────────────────────────────┐ │
│  │    Application Container     │   │   ADOT Collector Sidecar   │ │
│  │                              │   │                            │ │
│  │  Spring Boot + ADOT Agent    │   │  aws-otel-collector:latest │ │
│  │  -javaagent:/opt/aws-otel..  │   │                            │ │
│  │                              │   │  Receives:                 │ │
│  │  Profiles:                   │   │    OTLP gRPC :4317         │ │
│  │    dev,aws,aws-signals       │   │    OTLP HTTP :4318         │ │
│  │                              │   │                            │ │
│  │  Emits via OTLP:            │──▶│  Exports to:               │ │
│  │    Traces (spans)            │   │    → CloudWatch Metrics    │ │
│  │    Metrics (counters,histo)  │   │    → X-Ray Traces          │ │
│  │    Logs (structured JSON)    │   │    → Application Signals   │ │
│  │                              │   │                            │ │
│  │  OTel Attributes:            │   │  Config:                   │ │
│  │    service.name              │   │    ecs-default-config.yaml │ │
│  │    service.namespace         │   │                            │ │
│  │    deployment.environment    │   │  Essential: false          │ │
│  │    tenant.id (from header)   │   │  (app runs without it)    │ │
│  └──────────────────────────────┘   └─────────────┬──────────────┘ │
└───────────────────────────────────────────────────┼────────────────┘
                                                    │
                         ┌──────────────────────────┘
                         ▼
        ┌────────────────────────────────────────┐
        │     CloudWatch Application Signals     │
        │                                        │
        │  ┌──────────────┐  ┌────────────────┐  │
        │  │   Services   │  │  Service Map   │  │
        │  │  (10 found)  │  │  Portal→API→DB │  │
        │  └──────────────┘  └────────────────┘  │
        │                                        │
        │  ┌──────────────────────────────────┐  │
        │  │         SLO Engine               │  │
        │  │                                  │  │
        │  │  8 SLOs (CloudFormation):        │  │
        │  │    Availability 99.9% × 4 APIs   │  │
        │  │    Latency P99 <500ms × 4 APIs   │  │
        │  │                                  │  │
        │  │  Error Budget Tracking:          │  │
        │  │    Fast burn: 14.4x / 1h         │  │
        │  │    Slow burn: 6x / 6h            │  │
        │  └──────────────────────────────────┘  │
        │                                        │
        │  ┌──────────────────────────────────┐  │
        │  │    Blog Features (Day 0–30+)     │  │
        │  │                                  │  │
        │  │  1. Service-Level SLOs           │  │
        │  │     Holistic per-service view    │  │
        │  │                                  │  │
        │  │  2. SLO Performance Reports      │  │
        │  │     Daily/weekly/monthly trends  │  │
        │  │                                  │  │
        │  │  3. SLO Recommendations (Day30+) │  │
        │  │     Data-driven target proposals │  │
        │  └──────────────────────────────────┘  │
        │                                        │
        │  ┌──────────────────────────────────┐  │
        │  │    Per-Tenant Filtering           │  │
        │  │    tenant.id dimension from       │  │
        │  │    X-Tenant-Id request header     │  │
        │  │    via TenantContextSpanProcessor │  │
        │  └──────────────────────────────────┘  │
        └────────────────────────────────────────┘
                         │
                         ▼
        ┌────────────────────────────────────────┐
        │           SNS Alert Topic              │
        │    SLO breach → email/webhook/Slack    │
        └────────────────────────────────────────┘
```

### Feature Toggle Flow: How It Gets Enabled

```
Developer runs AWS_100 workflow (GitHub Actions UI)
         │
         ▼
┌─ AWS_100_Blog_SLO_20260313.yml ─────────────────────┐
│                                                       │
│  1. Pre-flight: verify AWS_99 stacks exist            │
│  2. Human-in-the-loop approval (GitHub Environment)   │
│  3. Update observability stack:                       │
│       EnableApplicationSignals = true                 │
│       → Creates 8 AWS::CloudWatch::SLO resources      │
│  4. Update Fargate stack:                             │
│       EnableApplicationSignals = true                 │
│       → SPRING_PROFILES_ACTIVE += aws-signals         │
│       → JAVA_TOOL_OPTIONS = -javaagent:adot.jar       │
│       → ADOT Collector sidecar added (Essential:false)│
│       → Rolling deploy of all 10 services             │
│  5. Verify: check Application Signals metrics         │
│                                                       │
│  Disable: re-run with action=disable                  │
│       → Reverses all above (sidecar removed via       │
│         AWS::NoValue, agent deactivated, SLOs deleted)│
└───────────────────────────────────────────────────────┘
         │
         │  On destroy:
         ▼
┌─ AWS_98_Destroy_All.yml ─────────────────────────────┐
│  FEATURE_TOGGLES array auto-detects:                  │
│    "observability:EnableApplicationSignals"            │
│    "fargate:EnableApplicationSignals"                  │
│  Disables all toggles → then deletes stacks           │
│  (Extensible: add future blog toggles to the array)   │
└───────────────────────────────────────────────────────┘
```

### Component Map: What Changes Per Module

| Layer | Component | Without Signals (default) | With Signals (AWS_100 enabled) |
|---|---|---|---|
| **Docker Image** | ADOT agent JAR | Present but inert (17MB) | Activated via `JAVA_TOOL_OPTIONS` |
| **Fargate Task** | Containers | 1 (app only) | 2 (app + ADOT Collector sidecar) |
| **Fargate Task** | Env vars | `SPRING_PROFILES_ACTIVE=dev,aws` | `+ aws-signals`, `JAVA_TOOL_OPTIONS`, `OTEL_RESOURCE_ATTRIBUTES` |
| **Spring Profile** | `application-aws-signals.yml` | Not activated | OTLP → localhost:4317 (sidecar), resource attributes |
| **Common-Utils** | `TenantContextSpanProcessor` | Loaded (adds `tenant.id=unknown`) | Adds real `tenant.id` from `X-Tenant-Id` header |
| **CloudFormation** | observability.yaml | Log groups, alarms, dashboard | + 8 SLO resources (conditioned) |
| **CloudFormation** | api-gateway-fargate.yaml | Single container tasks | + sidecar, env vars (conditioned) |
| **IAM** | ECS Task Role | X-Ray, CloudWatch | + `application-signals:*` |
| **IAM** | Deployer Policy | CloudWatch, logs | + `application-signals:*` |

---

## What We Have Today (Local Observability)

The platform already has a full **local** SRE stack (Prometheus + Grafana + Alertmanager) with:
- 5 Grafana dashboards (including `slo-overview.json` with availability %, P99/P95 latency, error budget burn rate)
- 28 Prometheus alert rules (SLO breaches, error budgets, application health, claims processing, infrastructure)
- Recording rules for SLIs (request rate, error rate, availability, latency percentiles, 30-day error budget)
- `SloComplianceService` in Portal-SRE tracking per-tenant compliance
- OpenTelemetry SDK configured (Micrometer bridge, OTLP exporter to Jaeger/Zipkin)

**What's missing:** The bridge from local observability to **CloudWatch Application Signals** — ADOT agent, X-Ray integration, Application Signals discovery, and native CloudWatch SLO resources.

---

## Phase 0: Prerequisites (Enable Application Signals)

All Phase 0 tasks are implemented as **feature toggles** — nothing is hardcoded. Enable via `AWS_100_Blog_SLO_20260313.yml` workflow (human-in-the-loop approval). Disable at any time by re-running with `action=disable`. Dockerfiles are fully reusable without Application Signals (ADOT agent is inert unless `JAVA_TOOL_OPTIONS` env var is set).

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 0.1 | Add ADOT Java auto-instrumentation agent to Dockerfiles | All 10 Dockerfiles | COMPLETED | Agent downloaded at build time, activated only via `JAVA_TOOL_OPTIONS` env var in Fargate |
| 0.2 | Add ADOT Collector as Fargate sidecar container | api-gateway-fargate.yaml | COMPLETED | Feature-toggled via `EnableApplicationSignals` param; `!If`+`AWS::NoValue` pattern — sidecar removed when disabled |
| 0.3 | Enable Application Signals SLO resources in CloudFormation | observability.yaml | COMPLETED | 8 `AWS::CloudWatch::ServiceLevelObjective` resources, conditioned on `IsAppSignalsEnabled` |
| 0.4 | Add IAM permissions for Application Signals | CloudFormation + IAM policies | COMPLETED | `application-signals:*` in ECS Task Role + deployer IAM policy (X-Ray already existed) |
| 0.5 | Create new `application-aws-signals.yml` Spring profile | All 10 modules | COMPLETED | Activated as `dev,aws,aws-signals`; OTLP → `localhost:4317` (ADOT sidecar); env-specific sampling via `TRACING_SAMPLING_PROBABILITY` |
| 0.6 | Add `tenantId` as OTel resource attribute for per-tenant filtering | Common-Utils | COMPLETED | `TenantContextSpanProcessor` reads `X-Tenant-Id` header → low-cardinality `tenant.id` span attribute |
| 0.7 | Create `AWS_100_Blog_SLO_20260313.yml` GitHub Actions workflow | .github/workflows | COMPLETED | Human-in-the-loop approval, enable/disable toggle, updates both observability + Fargate stacks, verifies metrics |
| 0.8 | Update AWS_98 to detect and clean up blog feature toggles | .github/workflows | COMPLETED | Dynamic `FEATURE_TOGGLES` array — disables all blog toggles before stack deletion; extensible for future blogs |

---

## Phase 1: Smoke Tests (Day 0 — Post-Deployment)

Assumes GitHub Actions has deployed the Fargate services via CloudFormation (`AWS_99_Orchestrator_Full_Deploy.yml`).

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 1.1 | Verify all 10 services discovered in Application Signals console | AWS Console | NOT STARTED | Application Signals → Services should list all 10 |
| 1.2 | Verify service map renders Portal → API → DB call paths | AWS Console | NOT STARTED | Feign client dependencies visible in service map |
| 1.3 | Verify Application Signals metrics flowing | AWS CLI / Console | NOT STARTED | `aws cloudwatch list-metrics --namespace ApplicationSignals` — Latency, Fault, Error per service |
| 1.4 | Verify X-Ray trace correlation (Portal → Feign → API → DB) | X-Ray Console | NOT STARTED | Full distributed trace visible end-to-end |
| 1.5 | Verify per-operation metrics (GET/POST/PUT endpoints) | Application Signals → Service → Operations | NOT STARTED | Each REST endpoint listed with latency + error rate |

---

## Phase 2: SLO Definition & Validation (Day 0–1)

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 2.1 | Create Availability SLO (99.9%) for API-Claims via CloudFormation | observability.yaml | NOT STARTED | Error rate < 0.1% over rolling 30-day window |
| 2.2 | Create Latency P99 SLO (<500ms) for API-Claims via CloudFormation | observability.yaml | NOT STARTED | P99 response time target |
| 2.3 | Create **Service-Level SLO** across all API-Claims operations | AWS Console / CloudFormation | NOT STARTED | **Blog feature #2** — single holistic SLO across all endpoints |
| 2.4 | Verify SLO status shows "Healthy" under normal load | Application Signals → SLOs | NOT STARTED | Green status, error budget ~100% |
| 2.5 | Create SLOs for all 4 API services (8 SLOs total) | observability.yaml | NOT STARTED | Availability + Latency x 4 services (Claims, Members, Tenants, Entitlements) |

---

## Phase 3: SLO Breach & Error Budget Testing (Day 1–2)

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 3.1 | Inject 5xx errors → trigger Availability SLO breach | API-Claims | NOT STARTED | Kill downstream or use fault injection endpoint; SLO → "Breached" |
| 3.2 | Inject latency → trigger P99 Latency SLO breach | API-Claims | NOT STARTED | Artificial delay or concurrent overload; P99 SLO → "At Risk" |
| 3.3 | Verify CloudWatch Alarm fires on SLO breach | SNS Topic | NOT STARTED | Alert email/webhook received via SNS |
| 3.4 | Verify error budget burn rate calculation | Application Signals → SLO detail | NOT STARTED | Fast burn (14.4x/1h) and slow burn (6x/6h) thresholds |
| 3.5 | Verify Service-Level SLO degrades when single operation degrades | Application Signals → SLOs | NOT STARTED | **Blog feature #2** — slow POST /claims affects service-level SLO even if GETs healthy |

---

## Phase 4: SLO Performance Report (Week 1+)

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 4.1 | Verify daily SLO Performance Report generated | Application Signals → Performance Report | NOT STARTED | **Blog feature #3** — daily reliability trend chart |
| 4.2 | Verify breach periods align with fault injection windows | Performance Report | NOT STARTED | Cross-reference with Phase 3 test timestamps |
| 4.3 | Verify weekly rollup reflects aggregate compliance | Performance Report | NOT STARTED | Weekly compliance % matches daily data |
| 4.4 | Export SLO Performance Report for stakeholder review | Performance Report | NOT STARTED | Calendar-aligned periods (not rolling window) |

---

## Phase 5: SLO Recommendations (Day 30+)

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 5.1 | Verify SLO Recommendations appear after 30 days of data | Application Signals → SLO Recommendations | NOT STARTED | **Blog feature #1** — suggested P99 latency + error rate targets per service |
| 5.2 | Validate recommendations are reasonable for workload | SLO Recommendations | NOT STARTED | Suggested targets within ~10% of actual P99 and error rates |
| 5.3 | Accept recommendation → verify SLO auto-created | SLO Recommendations → Create SLO | NOT STARTED | One-click SLO creation from recommendation |
| 5.4 | Verify recommendations adjust after workload pattern change | SLO Recommendations | NOT STARTED | Increase traffic for 1 week, recheck suggested targets |

---

## Phase 6: Multi-Tenant SLO Validation (Platform-Specific)

| # | Task | Module(s) | Status | Notes |
|---|---|---|---|---|
| 6.1 | Verify per-tenant metrics filterable in Application Signals | Application Signals → Metrics | NOT STARTED | `tenantId` dimension available for filtering (requires 0.6) |
| 6.2 | Create tenant-specific SLO (e.g., TENANT-001 availability) | CloudFormation / Console | NOT STARTED | SLO with dimension filter `tenantId=TENANT-001` |
| 6.3 | Verify tenant isolation — one tenant's breach doesn't mask another | Application Signals → SLOs | NOT STARTED | TENANT-001 breached, TENANT-002 healthy simultaneously |
| 6.4 | Wire Portal-SRE dashboard to CloudWatch Application Signals API | Portal-SRE | NOT STARTED | SloComplianceService reads real AWS SLO status via AWS SDK |

---

## Testing Timeline

| Timeframe | Phases | Blog Features Testable |
|---|---|---|
| **Day 0** | Phase 0 (prereqs) + Phase 1 (smoke) | Service discovery, metrics, traces |
| **Day 0–1** | Phase 2 (SLO definition) | **Service-Level SLOs** |
| **Day 1–2** | Phase 3 (breach testing) | Error budgets, **Service-Level SLO** degradation |
| **Week 1+** | Phase 4 (reports) | **SLO Performance Reports** (daily/weekly) |
| **Day 30+** | Phase 5 (recommendations) | **SLO Recommendations** (headline feature) |
| **Ongoing** | Phase 6 (multi-tenant) | Per-tenant SLO isolation |

---

## Summary

| Phase | Tasks | Completed | Remaining |
|---|---|---|---|
| Phase 0 — Prerequisites | 8 | **8** | 0 |
| Phase 1 — Smoke Tests | 5 | 0 | 5 |
| Phase 2 — SLO Definition | 5 | 0 | 5 |
| Phase 3 — Breach Testing | 5 | 0 | 5 |
| Phase 4 — Performance Reports | 4 | 0 | 4 |
| Phase 5 — SLO Recommendations | 4 | 0 | 4 |
| Phase 6 — Multi-Tenant SLOs | 4 | 0 | 4 |
| **TOTAL** | **35** | **8** | **27** |

---

## References

- [AWS Announcement](https://aws.amazon.com/about-aws/whats-new/2026/03/cloudwatch-application-signals-adds-slo-capabilities/)
- [SLO Recommendations Documentation](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/CloudWatch-ServiceLevelObjectives.html#CloudWatch-ServiceLevelObjectives-Recommendations)
- [CloudWatch Pricing](https://aws.amazon.com/cloudwatch/pricing/)
- [README_PENDING_TASKS.md](README_PENDING_TASKS.md) — Platform pending tasks (tracked separately)

## PlantUML Diagrams

Renderable architecture diagrams in [`Docs/Diagrams/`](../Diagrams/):

| Diagram | File | What It Shows |
|---|---|---|
| Enterprise BDAT | [`01_enterprise_bdat_architecture.puml`](../Diagrams/01_enterprise_bdat_architecture.puml) | Business, Data, Application, Technology layers |
| Fargate Baseline | [`02_aws_fargate_baseline.puml`](../Diagrams/02_aws_fargate_baseline.puml) | AWS_99 deployment — 10 Fargate services, ALB, RDS, no signals |
| Fargate + Signals | [`03_aws_fargate_with_signals.puml`](../Diagrams/03_aws_fargate_with_signals.puml) | AWS_100 enabled — ADOT sidecar, Application Signals, 8 SLOs |
| ECS Task Comparison | [`04_ecs_task_comparison.puml`](../Diagrams/04_ecs_task_comparison.puml) | Before vs after: env vars, sidecar, agent activation |
| SLO Data Flow | [`05_application_signals_slo_flow.puml`](../Diagrams/05_application_signals_slo_flow.puml) | App → ADOT → Signals → SLO engine → breach alerts |
| GitHub Actions | [`06_github_actions_workflow.puml`](../Diagrams/06_github_actions_workflow.puml) | AWS_99 → AWS_100 → AWS_98 workflow sequence |

Render with IntelliJ PlantUML plugin, VS Code extension, or [plantuml.com](https://www.plantuml.com/plantuml/uml).
