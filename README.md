# Claims Processor With SRE

**HealthCare Plans AI Platform** — A multi-tenant healthcare claims processing platform with AI-powered automation and Site Reliability Engineering practices.

---

## Vision

Transform healthcare claims processing from a manual, error-prone workflow into an intelligent, automated pipeline that leverages AI for document understanding, pattern detection, and adjudication recommendations — while maintaining enterprise-grade reliability through SRE practices.

## Who Is This For?

- **Healthcare Payers** processing member claims
- **Third-Party Administrators (TPAs)** managing claims on behalf of payers
- **Claims Processing Organizations** seeking to modernize with AI and SRE
- **Platform Engineers** building multi-tenant healthcare SaaS

---

## Table of Contents

- [Vision](#vision)
- [Who Is This For?](#who-is-this-for)
- [1. Business Architecture](#1-business-architecture)
  - [Business Context](#business-context)
  - [Business Capabilities](#business-capabilities)
  - [Claim Processing Lifecycle](#claim-processing-lifecycle)
  - [Claim Processing Stages](#claim-processing-stages)
- [2. Data Architecture](#2-data-architecture)
  - [Data Domains](#data-domains)
  - [Data Storage Strategy](#data-storage-strategy)
- [3. Application Architecture](#3-application-architecture)
  - [Service Landscape](#service-landscape)
  - [Inter-Service Communication](#inter-service-communication)
  - [Event-Driven Architecture](#event-driven-architecture)
- [4. Technology Architecture](#4-technology-architecture)
  - [Multi-Cloud Strategy](#multi-cloud-strategy)
  - [Observability Architecture](#observability-architecture)
  - [Reliability and Resilience](#reliability-and-resilience)
- [5. GitHub Actions Deployment](#5-github-actions-deployment)
  - [Environment Setup](#environment-setup)
  - [Required Secrets](#required-secrets)
  - [Optional Variables](#optional-variables)
  - [IAM Permissions](#iam-permissions)
- [Getting Started](#getting-started)
- [License](#license)

---

## 1. Business Architecture

### Business Context

Healthcare claims processing involves multiple stakeholders with distinct needs. Members and patients submit claims and track their status. Healthcare providers submit claims on behalf of patients. Claims support staff handle the day-to-day review, validation, and management of claims as they move through the processing pipeline. Claims administrators configure business rules, manage workflows, and oversee the adjudication process. Batch operations teams handle high-volume member imports and bulk processing tasks. Site Reliability Engineers monitor system health, track Service Level Objectives, and respond to incidents to keep the platform running reliably.

```mermaid
flowchart LR
    subgraph Stakeholders
        M["Members / Patients"]
        P["Healthcare Providers"]
        CS["Claims Support Staff"]
        CA["Claims Administrators"]
        BO["Batch Operations Team"]
        SRE["SRE / Operations"]
    end

    subgraph Platform["Claims Processor Platform"]
        direction TB
        Intake["Claim Intake"]
        Process["AI Processing"]
        Adjudicate["Adjudication"]
        Settle["Settlement"]
    end

    M -->|"Submit claims, view status"| Platform
    P -->|"Submit claims on behalf of patients"| Platform
    CS -->|"Review, validate, manage claims"| Platform
    CA -->|"Configure rules, manage workflows"| Platform
    BO -->|"Bulk member import, batch operations"| Platform
    SRE -->|"Monitor SLOs, respond to incidents"| Platform
```

### Business Capabilities

**1. Multi-Tenant Claims Intake**

Organizations (tenants) onboard to the platform and configure their own claims workflows, business rules, and processing preferences. Each tenant operates in complete isolation from other tenants, ensuring data privacy and regulatory compliance. Claims support staff search for existing members within their tenant, upload claim artifacts in a wide range of formats, and select the appropriate processing stage for each claim. The platform accepts PNG, JPG, TIFF, PDF, DOCX, XLSX, CSV, and EDI X12 837 files, covering everything from scanned paper claims to electronic data interchange submissions.

**2. AI-Powered Document Understanding**

When a claim document is uploaded, the platform routes it through AI models for automatic data extraction. The system first classifies the document type — distinguishing between claim forms, Explanations of Benefits (EOBs), itemized bills, lab reports, prescriptions, and referrals. Once classified, the AI extracts structured data including patient demographics, provider details, diagnosis codes (ICD-10), procedure codes (CPT/HCPCS), service dates, and billed amounts. Every extracted field is accompanied by a confidence score, allowing staff to focus their review effort on low-confidence extractions rather than re-keying every field manually. This dramatically reduces manual data entry while maintaining accuracy through human oversight of uncertain extractions.

**3. Intelligent Adjudication**

AI models match each claim against the member's policy coverage rules, checking eligibility, benefit limits, pre-authorization requirements, and exclusions. The system also queries historical claim patterns to inform adjudication recommendations — identifying similar past claims and their outcomes. Claims that meet a configurable confidence threshold can auto-advance through adjudication, while low-confidence claims are queued for human review. When a claim requires human attention, the AI provides its reasoning and supporting evidence so that adjudicators can make informed decisions quickly rather than starting from scratch.

**4. Member and Entitlement Management**

The platform provides a complete multi-tenant identity and access management system. Users are organized into Groups, Groups are assigned Roles, and Roles contain fine-grained Privileges. Users can also receive individual Privileges beyond their Group assignments for exceptional cases. The system supports integration with external identity providers including Active Directory, Okta, and Auth0, allowing organizations to use their existing identity infrastructure. Tenant isolation is enforced at every layer, ensuring that users in one organization can never access data belonging to another.

**5. Operational Excellence (SRE)**

Reliability is not an afterthought — it is a first-class concern built into the platform from day one. Service Level Objectives define explicit reliability targets (such as 99.9% availability and p99 latency under 500 milliseconds). Error budgets track the margin between current reliability and the SLO target, informing decisions about whether to prioritize feature velocity or stability investments. The platform includes automated alerting on SLO breaches, security anomalies, infrastructure degradation, and application health issues. Observability is comprehensive: structured logs, distributed traces, and time-series metrics provide full visibility into system behavior.

### Claim Processing Lifecycle

The lifecycle of a claim follows a structured pipeline from initial intake through final settlement. Each stage is tracked, auditable, and visible to both staff and members.

```mermaid
flowchart TB
    subgraph Intake["1. Intake"]
        A1["Staff searches existing member"]
        A2["Selects claim stage from dropdown"]
        A3["Uploads claim artifacts"]
        A4["System validates & stores in document store"]
    end

    subgraph Extraction["2. AI Extraction"]
        B1["Document classification"]
        B2["OCR & text extraction"]
        B3["Entity extraction: codes, amounts, dates"]
        B4["Confidence scoring & visual grounding"]
    end

    subgraph Review["3. Review & Enrichment"]
        C1["Staff reviews extracted data side-by-side"]
        C2["Medical code validation (ICD-10, CPT, NPI)"]
        C3["Eligibility & coverage check"]
        C4["Duplicate & fraud detection"]
    end

    subgraph Adjudication["4. Adjudication"]
        D1["AI matches claim to policy rules"]
        D2["Historical precedent lookup"]
        D3["Benefit calculation"]
        D4["Recommendation: approve / deny / partial"]
    end

    subgraph Settlement["5. Settlement & Close"]
        E1["Payment processing"]
        E2["EOB generation"]
        E3["Member notification"]
        E4["Claim archived to knowledge base"]
    end

    Intake --> Extraction --> Review --> Adjudication --> Settlement
```

During **Intake**, claims support staff locate the member in the system, select the appropriate processing stage, and upload one or more claim artifacts. The system validates file formats, scans for corruption, and stores the original documents in the document store with full versioning.

During **AI Extraction**, the platform classifies each document, performs OCR on image-based documents, and extracts structured entities such as diagnosis codes, procedure codes, provider identifiers, service dates, and billed amounts. Each extraction includes a confidence score and visual grounding (highlighting where on the document the data was found).

During **Review and Enrichment**, staff review the AI-extracted data in a side-by-side view with the original document. The system validates medical codes against standard code sets, checks member eligibility and coverage, and runs duplicate and fraud detection algorithms.

During **Adjudication**, the AI matches the enriched claim data against the member's policy rules and historical precedents. It calculates the benefit amount and provides a recommendation — approve, deny, or partial approval — along with its reasoning.

During **Settlement and Close**, approved claims proceed to payment processing. The system generates an Explanation of Benefits for the member, sends notifications, and archives the completed claim to the knowledge base for future reference and analytics.

### Claim Processing Stages

| Stage | Description |
|---|---|
| INTAKE_RECEIVED | Claim artifact uploaded, pending initial review |
| DOCUMENT_VERIFICATION | Verifying document legibility and completeness |
| DATA_EXTRACTION | AI extracting structured data from the artifact |
| EXTRACTION_REVIEW | Staff reviewing and correcting AI-extracted data |
| ELIGIBILITY_CHECK | Verifying member eligibility and policy coverage |
| ADJUDICATION | AI-assisted decision on claim |
| ADJUDICATION_REVIEW | Staff reviewing AI recommendation |
| APPROVED / DENIED / PARTIAL_APPROVED | Adjudication outcome |
| SETTLEMENT | Payment processing initiated |
| CLOSED | Claim fully settled and archived |
| APPEAL | Member disputed denial, claim re-opened |

---

## 2. Data Architecture

### Data Domains

The platform organizes its data into five distinct domains, each with clear ownership and boundaries. The Tenant Domain is the root of the data hierarchy — every other entity is scoped by tenant, ensuring complete multi-tenant isolation.

```mermaid
flowchart TB
    subgraph TenantDomain["Tenant Domain"]
        Tenant["Tenant"]
        TenantConfig["Tenant Configuration"]
    end

    subgraph IdentityDomain["Identity & Access Domain"]
        User["User"]
        Group["Group"]
        Role["Role"]
        Privilege["Privilege"]
    end

    subgraph MemberDomain["Member Domain"]
        Member["Member / Patient"]
        Policy["Policy"]
    end

    subgraph ClaimsDomain["Claims Domain"]
        Claim["Claim"]
        ClaimDoc["Claim Document"]
        ExtractedData["Extracted Data"]
        AdjResult["Adjudication Result"]
    end

    subgraph EventDomain["Event Domain"]
        ClaimEvents["Claim Events"]
        MemberEvents["Member Events"]
        AuditTrail["Audit Trail"]
    end

    Tenant --> User & Member & Claim
    User --> Group --> Role --> Privilege
    Member --> Claim --> ClaimDoc --> ExtractedData
    Claim --> AdjResult
    Claim --> ClaimEvents
    Member --> MemberEvents
```

**Multi-Tenant Isolation** — Every data entity is scoped by a tenantId. The Tenant is the root entity from which all other data relationships descend. Database queries, API responses, and event streams are all filtered by tenant context, which is established at the authentication layer and propagated through every service call. This ensures that a user in one tenant can never accidentally or intentionally access another tenant's data.

**Identity Model** — Users belong to Groups, Groups are assigned Roles, and Roles contain Privileges. This hierarchical model supports both broad organizational policies (via Groups and Roles) and fine-grained exceptions (via individual Privilege assignments). The identity model integrates with external providers, allowing organizations to federate authentication while the platform manages authorization.

**Claims Data Flow** — A Claim references a Member and contains one or more Claim Documents (the uploaded artifacts). Each Document produces Extracted Data (the AI-parsed structured information) and ultimately an Adjudication Result. This chain of data relationships preserves full traceability from the original document through to the final decision.

**Event Sourcing** — All state transitions across the platform emit events to the event bus. These events drive asynchronous processing (such as search index updates and notifications), create immutable audit trails for compliance, and feed analytics pipelines for operational intelligence. The event-driven approach decouples producers from consumers, allowing new downstream capabilities to be added without modifying existing services.

**Data Security** — All external-facing identifiers (tenant IDs, member IDs, claim IDs, user IDs) are encrypted using AES-256-GCM before leaving the system boundary. This means that IDs visible in API responses, URLs, and logs are encrypted tokens rather than raw database identifiers, preventing enumeration attacks and reducing the impact of data exposure.

### Data Storage Strategy

The platform uses purpose-built storage for each data category, selecting the right tool for each job rather than forcing all data into a single store.

| Data Type | Storage | Purpose |
|---|---|---|
| Transactional Data | PostgreSQL (RDS / Cloud SQL / Azure SQL) | Claims, members, tenants, entitlements — the system of record |
| Document Storage | S3 / Azure Blob / GCS | Original claim artifacts (PDFs, images, EDI files) with versioning |
| Cache | Redis / ElastiCache / Azure Redis | Session data, frequently accessed lookups, rate limiting counters |
| Search Index | Elasticsearch / OpenSearch / Azure Cognitive Search | Full-text search across claims and member records |
| Event Stream | Kafka / Kinesis / Event Hub / Pub/Sub | Real-time event processing, async workflows, audit trails |
| Application Logs | Elasticsearch via Filebeat | Structured JSON log aggregation and analysis |
| Metrics | Prometheus / CloudWatch / Azure Monitor | Time-series performance and reliability data |
| Traces | Jaeger / Zipkin via OpenTelemetry | Distributed request tracing across service boundaries |

---

## 3. Application Architecture

### Service Landscape

The platform is organized into user-facing portals, backend API services, and shared libraries. Each portal and API service is independently deployable, allowing teams to develop, test, and release services on their own schedules.

```mermaid
flowchart TB
    subgraph Portals["User-Facing Portals"]
        PA["Claims Advisor Portal<br/>(Staff)"]
        PM["Claims Member Portal<br/>(Customers)"]
        PT["Tenant Management Portal"]
        PE["Entitlements Portal"]
        PB["Batch Assignment Portal"]
    end

    subgraph APIs["Backend API Services"]
        AC["Claims API"]
        AM["Members API"]
        AT["Tenants API"]
        AE["Entitlements API"]
    end

    subgraph Shared["Shared Libraries"]
        CU["Common Utils<br/>(Encryption, DTOs, Service Client)"]
    end

    PA & PM & PT & PE & PB --> AC & AM & AT & AE
    AC & AM & AT & AE --> CU
```

**Portals** deliver rich user interfaces for different stakeholder personas. The Claims Advisor Portal is designed for claims support staff, providing document upload, side-by-side review, and claim management workflows. The Claims Member Portal gives members and patients visibility into their claim status and history. The Tenant Management Portal allows administrators to onboard and configure tenants. The Entitlements Portal manages users, groups, roles, and privileges. The Batch Assignment Portal supports high-volume operations such as bulk member imports from spreadsheets.

**API Services** are pure backend services exposing REST APIs. They implement CQRS (Command/Query Responsibility Segregation), maintaining separate controllers, service layers, and in some cases repository layers for read operations versus write operations. This separation allows read and write workloads to be optimized and scaled independently.

**Common Utils** is a shared library consumed by all services. It provides standardized ID encryption (ensuring all external-facing IDs are encrypted), common data transfer objects (such as ApiResponse and PagedResponse for consistent API responses), and a resilient inter-service HTTP client with built-in circuit breaker, bulkhead, and retry capabilities.

### Inter-Service Communication

All service-to-service communication flows through a resilient client that protects the platform from cascade failures and transient errors.

```mermaid
flowchart LR
    subgraph Caller["Calling Service"]
        RC["ResilientServiceClient"]
        CB["Circuit Breaker"]
        BH["Bulkhead"]
        RT["Retry"]
        Auth["Auth Interceptor"]
    end

    subgraph Target["Target Service"]
        API["REST API"]
        Security["Security Filter"]
    end

    RC --> CB --> BH --> RT --> Auth --> API
    API --> Security
```

Every inter-service call goes through the ResilientServiceClient, which layers multiple resilience patterns. The **Circuit Breaker** monitors the failure rate of calls to each target service and opens the circuit (fast-failing all requests) when the failure rate exceeds 50%, preventing a struggling downstream service from dragging down its callers. The **Bulkhead** limits the number of concurrent calls to each target service (25 maximum), preventing any single downstream dependency from exhausting the caller's thread pool. The **Retry** mechanism handles transient failures by automatically retrying failed requests up to 3 times with exponential backoff. The **Auth Interceptor** attaches authentication credentials to outbound requests. Authentication mode is feature-toggled and supports multiple providers: no authentication (for local development), basic authentication, JWT tokens, AWS Cognito, Azure Active Directory, and GCP IAM.

### Event-Driven Architecture

The platform uses an event-driven architecture to decouple services and enable asynchronous processing. The event bus implementation is feature-toggled, allowing the platform to run with different event streaming technologies depending on the deployment environment.

```mermaid
flowchart LR
    subgraph Producers
        ClaimService["Claims Service"]
        MemberService["Members Service"]
    end

    subgraph EventBus["Event Bus (Feature-Toggled)"]
        direction TB
        K["Kafka"]
        KN["AWS Kinesis"]
        EH["Azure Event Hub"]
        PS["GCP Pub/Sub"]
    end

    subgraph Consumers
        Search["Search Indexer"]
        Notify["Notification Service"]
        Audit["Audit Logger"]
        Analytics["Analytics Pipeline"]
    end

    ClaimService --> EventBus
    MemberService --> EventBus
    EventBus --> Search & Notify & Audit & Analytics
```

When a claim changes state or a member record is updated, the producing service emits an event to the event bus. Downstream consumers process these events independently: the Search Indexer updates the full-text search index, the Notification Service sends alerts to members and staff, the Audit Logger creates immutable audit records for compliance, and the Analytics Pipeline feeds operational dashboards and business intelligence. Because consumers are decoupled from producers, new consumers can be added without modifying existing services.

---

## 4. Technology Architecture

### Multi-Cloud Strategy

The platform is designed to run on any major cloud provider or locally on a developer workstation. Cloud-specific integrations (databases, object storage, event streaming, AI services, secrets management, and observability) are abstracted behind feature toggles and Spring profiles, allowing the same application code to deploy across environments without modification.

```mermaid
flowchart TB
    subgraph Local["Local Development"]
        H2["H2 In-Memory DB"]
        DockerStack["Docker: PostgreSQL, Redis, Kafka,<br/>Elasticsearch, Kibana, Prometheus,<br/>Grafana, Jaeger, Ollama"]
    end

    subgraph AWS["Amazon Web Services"]
        RDS["RDS PostgreSQL"]
        EC["ElastiCache Redis"]
        MSK["Amazon MSK"]
        OS["OpenSearch"]
        S3["S3"]
        Bedrock["Bedrock Data Automation"]
        CW["CloudWatch + App Signals"]
    end

    subgraph Azure["Microsoft Azure"]
        ASQL["Azure SQL"]
        ARedis["Azure Cache for Redis"]
        AEH["Event Hub"]
        ACS["Cognitive Search"]
        ABlob["Blob Storage"]
        AAI["Azure AI"]
        AKV["Key Vault"]
    end

    subgraph GCP["Google Cloud Platform"]
        CSQL["Cloud SQL"]
        MS["Memorystore"]
        PSub["Pub/Sub"]
        GSearch["Enterprise Search"]
        GCS["Cloud Storage"]
        VAI["Vertex AI"]
        GSM["Secret Manager"]
    end
```

For **local development**, services start with an embedded H2 in-memory database by default, requiring zero infrastructure setup. When a more production-like environment is needed, a Docker Compose stack provides PostgreSQL, Redis, Kafka, Elasticsearch, Kibana, Prometheus, Grafana, Jaeger, and Ollama (for local AI inference using Mistral 7B).

On **AWS**, the platform uses RDS PostgreSQL for transactional data, ElastiCache for caching, Amazon MSK for event streaming, OpenSearch for full-text search, S3 for document storage, Bedrock Data Automation for AI-powered document processing, and CloudWatch with Application Signals for observability.

On **Azure**, the equivalent services are Azure SQL, Azure Cache for Redis, Event Hub, Cognitive Search, Blob Storage, Azure AI, and Key Vault for secrets management.

On **GCP**, the platform uses Cloud SQL, Memorystore, Pub/Sub, Enterprise Search, Cloud Storage, Vertex AI, and Secret Manager.

### Observability Architecture

The SRE observability stack provides three pillars of visibility into the platform's behavior: metrics, logs, and traces.

**Metrics** flow from application instrumentation (via Micrometer) to Prometheus for collection and storage, and then to Grafana for visualization. The platform ships with five pre-built Grafana dashboards: an SLO Overview dashboard tracking availability and latency objectives with error budget burn rates; an Application Health dashboard showing JVM metrics, thread pools, garbage collection, and uptime; a Claims Processing dashboard monitoring claim throughput, stage durations, and AI extraction latency; an Infrastructure dashboard tracking PostgreSQL, Redis, and Kafka health; and an Auth and Resilience dashboard monitoring authentication success rates and circuit breaker states.

**Logging** uses structured JSON output (via Logback) collected by Filebeat and shipped to Elasticsearch. Kibana provides three pre-configured data views: Application Logs for operational troubleshooting, Claims Data for claims-specific analysis, and Members Data for member-related investigations. Structured logging ensures that every log entry includes correlation IDs, tenant context, and request metadata for precise filtering and analysis.

**Tracing** uses the OpenTelemetry standard, with trace data flowing to both Jaeger and Zipkin. Distributed traces follow requests as they cross service boundaries, making it straightforward to identify latency bottlenecks, understand call chains, and diagnose failures in the distributed system. All nine services participate in trace propagation.

**Alerting** is comprehensive, with 28 Prometheus alert rules and 14 Grafana alert rules covering service availability, error rates, SLO breaches, error budget exhaustion, high latency (p99), Kafka consumer lag, Redis connection failures, JVM memory pressure, and disk usage. Alerts route through Alertmanager to PagerDuty, Slack, and email channels based on severity.

### Reliability and Resilience

The platform implements multiple resilience patterns to ensure reliable operation under adverse conditions.

| Pattern | Implementation | Purpose |
|---|---|---|
| Circuit Breaker | Resilience4j | Prevent cascade failures when downstream services degrade |
| Bulkhead | Resilience4j | Isolate concurrent call limits per dependency to prevent resource exhaustion |
| Rate Limiter | Resilience4j | Protect services against unexpected traffic spikes |
| Retry | Resilience4j | Automatically recover from transient failures with exponential backoff |
| Connection Pool | HikariCP | Efficient, bounded database connection management |
| SLOs / SLIs | CloudWatch App Signals + Prometheus | Define and continuously track reliability targets |
| Error Budgets | Prometheus recording rules | Balance reliability investment against feature development velocity |
| Feature Toggles | Spring ConfigurationProperties | Safe, incremental rollout of new integrations and capabilities |

---

## 5. GitHub Actions Deployment

The GitHub Actions workflows (`AWS_01` through `AWS_11`) deploy all infrastructure and services to AWS via CloudFormation. Before running any workflow you must configure GitHub Environments and their secrets.

### Environment Setup

Go to your repository → **Settings → Environments** and create three environments:

| Environment | Purpose |
|---|---|
| `dev` | Development — shared team environment |
| `staging` | Pre-production validation |
| `prod` | Live production workloads |

Each environment scopes its own secrets, so you can use different AWS accounts or credentials per tier.

### Required Secrets

Add the following secrets to **each** environment (`dev`, `staging`, `prod`):

| Secret Name | Description | Example Value (dev) |
|---|---|---|
| `CLAIMS_PROC_AWS_ACCESS_KEY_ID` | AWS IAM Access Key ID | `AKIAIOSFODNN7EXAMPLE` |
| `CLAIMS_PROC_AWS_SECRET_ACCESS_KEY` | AWS IAM Secret Access Key | *(from AWS IAM console)* |
| `CLAIMS_PROC_RDS_MASTER_PASSWORD` | PostgreSQL master password | `BH6KuVN27f2aMVQWG7XC` — [how to generate](#claims_proc_rds_master_password) |
| `CLAIMS_PROC_REDIS_AUTH_TOKEN` | ElastiCache Redis auth token | `4knQn7k0PNbTCx1V9qc15r6EfFh7sDgS` — [how to generate](#claims_proc_redis_auth_token) |
| `CLAIMS_PROC_ENCRYPTION_KEY` | AES-256 app encryption key | `bWbimbvUejwdlV0TQRFt0Qz9yKpsnLYmhQ4sot090u0=` — [how to generate](#claims_proc_encryption_key) |

> You may use the same IAM credentials across all environments or separate AWS accounts per environment for stricter isolation.

#### `CLAIMS_PROC_RDS_MASTER_PASSWORD`

AWS RDS requires: **minimum 8 characters**, must not contain `/`, `@`, `"`, or spaces.

**Recommended value for `dev` (copy-paste ready):**
```
BH6KuVN27f2aMVQWG7XC
```

To generate your own — with `openssl`:
```bash
openssl rand -base64 24 | tr -d '/+= ' | cut -c1-20
```
Or with Python 3 (available on all laptops without extra tools):
```bash
python3 -c "import secrets, string; c=string.ascii_letters+string.digits; print(''.join(secrets.choice(c) for _ in range(20)))"
```

Use different passwords per environment. Store the value in a password manager as well as the GitHub secret.

> ⚠️ Use a **different, private password** for `staging` and `prod`. Store it in a secrets vault.

#### `CLAIMS_PROC_REDIS_AUTH_TOKEN`

AWS ElastiCache requires: **minimum 16 characters**, maximum 128, printable ASCII only (no spaces).

**Recommended value for `dev` (copy-paste ready):**
```
4knQn7k0PNbTCx1V9qc15r6EfFh7sDgS
```

To generate your own — with `openssl`:
```bash
openssl rand -base64 32 | tr -d '/+= ' | cut -c1-32
```
Or with Python 3:
```bash
python3 -c "import secrets, string; c=string.ascii_letters+string.digits; print(''.join(secrets.choice(c) for _ in range(32)))"
```

> ⚠️ Use a **different, private token** for `staging` and `prod`. Store it in a secrets vault.

#### `CLAIMS_PROC_ENCRYPTION_KEY`

Used for AES-256-GCM encryption of sensitive IDs in the application (via `Common-Utils`). Must be a **base64-encoded 32-byte (256-bit) key**.

**Recommended value for `dev` (copy-paste ready):**
```
bWbimbvUejwdlV0TQRFt0Qz9yKpsnLYmhQ4sot090u0=
```

To generate your own — with `openssl`:
```bash
openssl rand -base64 32
```
Or with Python 3:
```bash
python3 -c "import base64, os; print(base64.b64encode(os.urandom(32)).decode())"
```

> ⚠️ Use a **unique, privately generated key** for `staging` and `prod`. If you rotate the key after data has been written, existing encrypted values will become unreadable. Store it in a secrets vault (e.g. AWS Secrets Manager, 1Password, HashiCorp Vault).

### Optional Variables

These **Variables** (not secrets) can be set per environment to override defaults:

| Variable Name | Default | Description |
|---|---|---|
| `CLAIMS_PROC_AWS_REGION` | `us-east-1` | AWS region for all resources |
| `CLAIMS_PROC_PROJECT_NAME` | `claims-proc` | Project name prefix used in stack names and tags |
| `CLAIMS_PROC_RDS_INSTANCE_CLASS` | `db.t3.medium` | RDS instance class override |

### IAM Permissions

The IAM user behind the access keys must have permissions for the services deployed by each workflow:

| Workflow | AWS Services Required |
|---|---|
| AWS_01 — VPC & Security Groups | `EC2:*` |
| AWS_02 — Core Infrastructure | `RDS:*`, `ElastiCache:*`, `EC2:*` |
| AWS_03 — Observability | `CloudWatch:*`, `SNS:*` |
| AWS_04 — Auth (Cognito) | `Cognito-IDP:*`, `Cognito-Identity:*` |
| AWS_05 — Messaging & Search | `Kinesis:*`, `MSK:*`, `ES:*` (OpenSearch) |
| AWS_06 — Secrets Manager | `SecretsManager:*` |
| AWS_07 — EKS Cluster | `EKS:*`, `EC2:*`, `IAM:*` |
| AWS_08 — Build & Push ECR | `ECR:*` |
| AWS_09 — API Gateway & Fargate | `ECS:*`, `ElasticLoadBalancing:*`, `EC2:*` |
| AWS_10 — Data Seed | `Lambda:*`, `RDS:*` |

All workflows also require `CloudFormation:*` and `IAM:*` (for `CAPABILITY_NAMED_IAM` stacks).

#### How to Set Up the IAM User and Policies

> **Note:** IAM policies cannot be set up inside GitHub Actions workflows themselves — you need AWS credentials to create IAM policies, creating a chicken-and-egg problem. The one-time setup below must be done manually in the AWS Console or CLI before any workflow can run.

**Step 1 — Create an IAM User**

1. Go to **AWS Console → IAM → Users → Create user**
2. Name it `claims-proc-github-actions`
3. Select **"Attach policies directly"** (not via group for now)
4. Skip adding policies — you'll attach them next
5. After creation, go to the user → **Security credentials → Create access key**
6. Choose **"Other"** as the use case → copy the Access Key ID and Secret — these go into GitHub Secrets

**Step 2 — Create and Attach Policies**

Policy JSON files are provided in [`DevOps/AWS/IAM/`](DevOps/AWS/IAM/).

For each policy: **AWS Console → IAM → Policies → Create policy → JSON tab** → paste the JSON → name it → Create.
Then attach to the `claims-proc-github-actions` user.

---

##### Option A — Dev / Quick Start (1 policy, all permissions)

Use **`claims-proc-policy-dev-all-in-one.json`** — one policy covering everything. Fastest to set up for `dev`.

Policy name to use: `claims-proc-dev-all-in-one`

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "CloudFormationAndIAM",
      "Effect": "Allow",
      "Action": [ "cloudformation:*", "iam:*" ],
      "Resource": "*"
    },
    {
      "Sid": "NetworkingAndContainers",
      "Effect": "Allow",
      "Action": [ "ec2:*", "eks:*", "ecr:*", "ecs:*", "elasticloadbalancing:*" ],
      "Resource": "*"
    },
    {
      "Sid": "DataAndSecrets",
      "Effect": "Allow",
      "Action": [ "rds:*", "elasticache:*", "secretsmanager:*", "s3:*" ],
      "Resource": "*"
    },
    {
      "Sid": "AuthAndObservability",
      "Effect": "Allow",
      "Action": [ "cognito-idp:*", "cognito-identity:*", "cloudwatch:*", "logs:*", "sns:*", "cloudtrail:*" ],
      "Resource": "*"
    },
    {
      "Sid": "MessagingAndLambda",
      "Effect": "Allow",
      "Action": [ "kinesis:*", "kafka:*", "es:*", "lambda:*" ],
      "Resource": "*"
    },
    {
      "Sid": "CostExplorer",
      "Effect": "Allow",
      "Action": [ "ce:GetCostAndUsage", "ce:GetCostForecast", "ce:GetDimensionValues", "ce:GetTags", "budgets:DescribeBudgets", "budgets:ViewBudget" ],
      "Resource": "*"
    }
  ]
}
```

---

##### Option B — Staging / Prod (5 least-privilege policies)

Split across 5 policies for tighter control. AWS allows up to 10 managed policies per user/role.

**Policy 1 — `claims-proc-policy-1-cfn-iam`** (required by all workflows)

File: [`DevOps/AWS/IAM/claims-proc-policy-1-cfn-iam.json`](DevOps/AWS/IAM/claims-proc-policy-1-cfn-iam.json)

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "CloudFormationFull",
      "Effect": "Allow",
      "Action": [
        "cloudformation:CreateStack", "cloudformation:UpdateStack",
        "cloudformation:DeleteStack", "cloudformation:DescribeStacks",
        "cloudformation:DescribeStackEvents", "cloudformation:DescribeStackResources",
        "cloudformation:GetTemplate", "cloudformation:ListStacks",
        "cloudformation:ValidateTemplate", "cloudformation:CreateChangeSet",
        "cloudformation:ExecuteChangeSet", "cloudformation:DescribeChangeSet"
      ],
      "Resource": "*"
    },
    {
      "Sid": "IAMFull",
      "Effect": "Allow",
      "Action": [
        "iam:CreateRole", "iam:DeleteRole", "iam:GetRole", "iam:PassRole",
        "iam:AttachRolePolicy", "iam:DetachRolePolicy",
        "iam:PutRolePolicy", "iam:DeleteRolePolicy", "iam:GetRolePolicy",
        "iam:CreateInstanceProfile", "iam:DeleteInstanceProfile",
        "iam:AddRoleToInstanceProfile", "iam:RemoveRoleFromInstanceProfile",
        "iam:TagRole", "iam:UpdateAssumeRolePolicy"
      ],
      "Resource": "*"
    }
  ]
}
```

**Policy 2 — `claims-proc-policy-2-network-containers`** (AWS_01, AWS_07, AWS_08, AWS_09)

File: [`DevOps/AWS/IAM/claims-proc-policy-2-network-containers.json`](DevOps/AWS/IAM/claims-proc-policy-2-network-containers.json)

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "EC2Networking",
      "Effect": "Allow",
      "Action": [
        "ec2:CreateVpc", "ec2:DeleteVpc", "ec2:DescribeVpcs",
        "ec2:CreateSubnet", "ec2:DeleteSubnet", "ec2:DescribeSubnets",
        "ec2:CreateSecurityGroup", "ec2:DeleteSecurityGroup", "ec2:DescribeSecurityGroups",
        "ec2:AuthorizeSecurityGroupIngress", "ec2:AuthorizeSecurityGroupEgress",
        "ec2:CreateInternetGateway", "ec2:AttachInternetGateway",
        "ec2:CreateRouteTable", "ec2:CreateRoute", "ec2:AssociateRouteTable",
        "ec2:AllocateAddress", "ec2:CreateNatGateway", "ec2:DescribeNatGateways",
        "ec2:DescribeAvailabilityZones", "ec2:CreateTags", "ec2:DescribeTags"
      ],
      "Resource": "*"
    },
    {
      "Sid": "EKS",
      "Effect": "Allow",
      "Action": [
        "eks:CreateCluster", "eks:DeleteCluster", "eks:DescribeCluster",
        "eks:ListClusters", "eks:UpdateClusterConfig",
        "eks:CreateNodegroup", "eks:DeleteNodegroup", "eks:DescribeNodegroup",
        "eks:TagResource"
      ],
      "Resource": "*"
    },
    {
      "Sid": "ECR",
      "Effect": "Allow",
      "Action": [
        "ecr:CreateRepository", "ecr:DeleteRepository", "ecr:DescribeRepositories",
        "ecr:GetAuthorizationToken", "ecr:BatchCheckLayerAvailability",
        "ecr:PutImage", "ecr:InitiateLayerUpload", "ecr:UploadLayerPart",
        "ecr:CompleteLayerUpload", "ecr:BatchGetImage", "ecr:TagResource"
      ],
      "Resource": "*"
    },
    {
      "Sid": "ECSAndALB",
      "Effect": "Allow",
      "Action": [
        "ecs:CreateCluster", "ecs:DeleteCluster", "ecs:DescribeClusters",
        "ecs:CreateService", "ecs:UpdateService", "ecs:DeleteService",
        "ecs:RegisterTaskDefinition", "ecs:DescribeTaskDefinition",
        "elasticloadbalancing:CreateLoadBalancer", "elasticloadbalancing:DeleteLoadBalancer",
        "elasticloadbalancing:DescribeLoadBalancers",
        "elasticloadbalancing:CreateTargetGroup", "elasticloadbalancing:DeleteTargetGroup",
        "elasticloadbalancing:CreateListener", "elasticloadbalancing:CreateRule",
        "elasticloadbalancing:RegisterTargets", "elasticloadbalancing:AddTags"
      ],
      "Resource": "*"
    }
  ]
}
```

**Policy 3 — `claims-proc-policy-3-data-secrets`** (AWS_02, AWS_06, AWS_10)

File: [`DevOps/AWS/IAM/claims-proc-policy-3-data-secrets.json`](DevOps/AWS/IAM/claims-proc-policy-3-data-secrets.json)

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "RDS",
      "Effect": "Allow",
      "Action": [
        "rds:CreateDBInstance", "rds:DeleteDBInstance", "rds:DescribeDBInstances",
        "rds:ModifyDBInstance", "rds:CreateDBSubnetGroup", "rds:DeleteDBSubnetGroup",
        "rds:DescribeDBSubnetGroups", "rds:CreateDBSnapshot",
        "rds:RestoreDBInstanceFromDBSnapshot", "rds:AddTagsToResource"
      ],
      "Resource": "*"
    },
    {
      "Sid": "ElastiCache",
      "Effect": "Allow",
      "Action": [
        "elasticache:CreateReplicationGroup", "elasticache:DeleteReplicationGroup",
        "elasticache:DescribeReplicationGroups", "elasticache:ModifyReplicationGroup",
        "elasticache:CreateCacheSubnetGroup", "elasticache:DeleteCacheSubnetGroup",
        "elasticache:DescribeCacheSubnetGroups", "elasticache:AddTagsToResource"
      ],
      "Resource": "*"
    },
    {
      "Sid": "SecretsManager",
      "Effect": "Allow",
      "Action": [
        "secretsmanager:CreateSecret", "secretsmanager:DeleteSecret",
        "secretsmanager:DescribeSecret", "secretsmanager:GetSecretValue",
        "secretsmanager:PutSecretValue", "secretsmanager:UpdateSecret",
        "secretsmanager:ListSecrets", "secretsmanager:TagResource"
      ],
      "Resource": "*"
    },
    {
      "Sid": "S3ForLambdaPackages",
      "Effect": "Allow",
      "Action": [
        "s3:CreateBucket", "s3:DeleteBucket", "s3:PutObject",
        "s3:GetObject", "s3:DeleteObject", "s3:ListBucket"
      ],
      "Resource": "*"
    }
  ]
}
```

**Policy 4 — `claims-proc-policy-4-auth-observability`** (AWS_03, AWS_04)

File: [`DevOps/AWS/IAM/claims-proc-policy-4-auth-observability.json`](DevOps/AWS/IAM/claims-proc-policy-4-auth-observability.json)

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "Cognito",
      "Effect": "Allow",
      "Action": [
        "cognito-idp:CreateUserPool", "cognito-idp:DeleteUserPool",
        "cognito-idp:DescribeUserPool", "cognito-idp:UpdateUserPool",
        "cognito-idp:CreateUserPoolClient", "cognito-idp:DeleteUserPoolClient",
        "cognito-idp:CreateUserPoolDomain", "cognito-idp:DeleteUserPoolDomain",
        "cognito-idp:TagResource",
        "cognito-identity:CreateIdentityPool", "cognito-identity:DeleteIdentityPool",
        "cognito-identity:UpdateIdentityPool", "cognito-identity:SetIdentityPoolRoles"
      ],
      "Resource": "*"
    },
    {
      "Sid": "CloudWatchAndLogs",
      "Effect": "Allow",
      "Action": [
        "cloudwatch:PutMetricAlarm", "cloudwatch:DeleteAlarms",
        "cloudwatch:DescribeAlarms", "cloudwatch:PutDashboard",
        "cloudwatch:GetDashboard", "cloudwatch:ListDashboards",
        "cloudwatch:PutMetricData", "cloudwatch:TagResource",
        "logs:CreateLogGroup", "logs:DeleteLogGroup", "logs:DescribeLogGroups",
        "logs:CreateLogStream", "logs:PutLogEvents", "logs:PutRetentionPolicy",
        "logs:TagLogGroup"
      ],
      "Resource": "*"
    },
    {
      "Sid": "SNS",
      "Effect": "Allow",
      "Action": [
        "sns:CreateTopic", "sns:DeleteTopic", "sns:GetTopicAttributes",
        "sns:SetTopicAttributes", "sns:ListTopics",
        "sns:Subscribe", "sns:Unsubscribe", "sns:Publish", "sns:TagResource"
      ],
      "Resource": "*"
    },
    {
      "Sid": "CloudTrail",
      "Effect": "Allow",
      "Action": [
        "cloudtrail:CreateTrail", "cloudtrail:UpdateTrail", "cloudtrail:DeleteTrail",
        "cloudtrail:GetTrail", "cloudtrail:GetTrailStatus", "cloudtrail:StartLogging",
        "cloudtrail:StopLogging", "cloudtrail:DescribeTrails", "cloudtrail:ListTrails",
        "cloudtrail:PutEventSelectors", "cloudtrail:GetEventSelectors",
        "cloudtrail:AddTags", "cloudtrail:RemoveTags", "cloudtrail:ListTags"
      ],
      "Resource": "*"
    },
    {
      "Sid": "CostExplorer",
      "Effect": "Allow",
      "Action": [
        "ce:GetCostAndUsage", "ce:GetCostForecast",
        "ce:GetDimensionValues", "ce:GetTags",
        "budgets:DescribeBudgets", "budgets:ViewBudget"
      ],
      "Resource": "*"
    }
  ]
}
```

**Policy 5 — `claims-proc-policy-5-messaging-lambda`** (AWS_05, AWS_10)

File: [`DevOps/AWS/IAM/claims-proc-policy-5-messaging-lambda.json`](DevOps/AWS/IAM/claims-proc-policy-5-messaging-lambda.json)

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "Kinesis",
      "Effect": "Allow",
      "Action": [
        "kinesis:CreateStream", "kinesis:DeleteStream", "kinesis:DescribeStream",
        "kinesis:ListStreams", "kinesis:PutRecord", "kinesis:PutRecords",
        "kinesis:GetRecords", "kinesis:GetShardIterator", "kinesis:AddTagsToStream"
      ],
      "Resource": "*"
    },
    {
      "Sid": "MSKManagedKafka",
      "Effect": "Allow",
      "Action": [
        "kafka:CreateCluster", "kafka:DeleteCluster", "kafka:DescribeCluster",
        "kafka:ListClusters", "kafka:UpdateBrokerCount",
        "kafka:CreateConfiguration", "kafka:DescribeConfiguration",
        "kafka:TagResource", "kafka:ListTagsForResource"
      ],
      "Resource": "*"
    },
    {
      "Sid": "OpenSearch",
      "Effect": "Allow",
      "Action": [
        "es:CreateDomain", "es:DeleteDomain", "es:DescribeDomain",
        "es:DescribeDomains", "es:ListDomainNames",
        "es:UpdateDomainConfig", "es:AddTags"
      ],
      "Resource": "*"
    },
    {
      "Sid": "Lambda",
      "Effect": "Allow",
      "Action": [
        "lambda:CreateFunction", "lambda:DeleteFunction", "lambda:GetFunction",
        "lambda:UpdateFunctionCode", "lambda:UpdateFunctionConfiguration",
        "lambda:InvokeFunction", "lambda:ListFunctions",
        "lambda:CreateEventSourceMapping", "lambda:TagResource", "lambda:AddPermission"
      ],
      "Resource": "*"
    }
  ]
}
```

> **Tip:** For initial setup, attaching `AdministratorAccess` to the IAM user is the fastest way to get started. Tighten to the least-privilege policies above before promoting to `prod`.

---

## Getting Started

For setup instructions, see:
- **[First Time Setup](Docs/MD_Files/README_Developer_First_Time.md)** — prerequisites, build, and initial run
- **[Daily Development](Docs/MD_Files/README_Developer_Daily_Activities.md)** — common commands, debugging, testing
- **[Deployment Guide](Docs/MD_Files/README_Developer_Deployment_Technical.md)** — profiles, Docker, cloud deployment

For architecture details with detailed Mermaid diagrams, see **[CLAUDE.md](CLAUDE.md)**.

For all service links, Swagger UIs, dashboards, and credentials, open **[index.html](index.html)** in your browser.

---

## License

This project is licensed under the [Apache License 2.0](LICENSE).
