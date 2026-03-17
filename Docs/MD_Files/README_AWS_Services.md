# AWS Services & CloudFormation Stacks

> Complete inventory of AWS services, CloudFormation stacks, GitHub Actions workflows, and observability setup for the Claims-Processor-With-SRE microservice platform.

---

## AWS Announcement — CloudWatch Application Signals SLO Capabilities

> **Posted:** March 13, 2026
> **Link:** https://aws.amazon.com/about-aws/whats-new/2026/03/cloudwatch-application-signals-adds-slo-capabilities/

Amazon CloudWatch Application Signals now offers three new console-based capabilities for Service Level Objectives (SLOs):

| Capability | What It Does |
|---|---|
| **SLO Recommendations** | Analyzes 30 days of service metrics (P99 latency, error rates) to suggest appropriate reliability targets |
| **Service-Level SLOs** | Holistic view of service reliability across all operations — bridges technical monitoring and business objectives |
| **SLO Performance Report** | Historical analysis aligned with calendar periods (daily, weekly, monthly) for trend identification |

**Pricing:** Based on inbound/outbound request count + SLO charges (2 Application Signals per SLI metric period per SLO).

This repository serves as a **reference implementation** demonstrating these capabilities with 10 microservices, per-tenant SLO tracking, and full OpenTelemetry instrumentation.

---

## Deployment Lifecycle

```
┌─────────────────────────────────────────────────────────┐
│                 AWS_99 (Full Deploy)                     │
│                                                         │
│  AWS_01 → AWS_02 → AWS_03 → AWS_06 → AWS_08 → AWS_09  │
│  (VPC)   (Core)   (Obs)    (Secrets) (ECR)    (Fargate)│
│                                        │                │
│                                   AWS_10 (Seed)         │
│                                                         │
│  Optional: AWS_05 (Messaging/Search), AWS_07 (EKS)     │
│  Disabled: AWS_04 (Cognito)                             │
└─────────────────────────────────────────────────────────┘

             ↕  Research Window (10-12 hours)  ↕

┌─────────────────────────────────────────────────────────┐
│                AWS_98 (Destroy All)                      │
│                                                         │
│  Reverse order: data-seed → fargate → eks → secrets →  │
│  messaging-search → observability → core-infra → vpc   │
│                                                         │
│  Result: $0.00 residual — zero resources remain         │
└─────────────────────────────────────────────────────────┘
```

---

## CloudFormation Stacks (9 Total)

### Stack 1: VPC & Security Groups (`vpc-sg`)

**Workflow:** `AWS_01_Create_VPC_SecurityGroups.yml`
**Template:** `DevOps/AWS/CloudFormation/vpc-security-groups.yaml`

| Resource | Type | Details |
|---|---|---|
| VPC | `AWS::EC2::VPC` | CIDR 10.0.0.0/16, DNS support enabled |
| Public Subnet 1 | `AWS::EC2::Subnet` | 10.0.1.0/24, AZ-0 |
| Public Subnet 2 | `AWS::EC2::Subnet` | 10.0.2.0/24, AZ-1 |
| Private Subnet 1 | `AWS::EC2::Subnet` | 10.0.10.0/24, AZ-0 |
| Private Subnet 2 | `AWS::EC2::Subnet` | 10.0.20.0/24, AZ-1 |
| Internet Gateway | `AWS::EC2::InternetGateway` | Public internet access |
| NAT Gateway | `AWS::EC2::NatGateway` | Private subnet outbound (optional) |
| Elastic IP | `AWS::EC2::EIP` | For NAT Gateway |
| Public Route Table | `AWS::EC2::RouteTable` | 0.0.0.0/0 → IGW |
| Private Route Table | `AWS::EC2::RouteTable` | 0.0.0.0/0 → NAT |
| ALB Security Group | `AWS::EC2::SecurityGroup` | Inbound: 80/443 from anywhere |
| App Security Group | `AWS::EC2::SecurityGroup` | Inbound: 8081-8090 from ALB |
| RDS Security Group | `AWS::EC2::SecurityGroup` | Inbound: 5432 from App |
| Redis Security Group | `AWS::EC2::SecurityGroup` | Inbound: 6379 from App |
| Kafka Security Group | `AWS::EC2::SecurityGroup` | Inbound: 9092 from App |
| OpenSearch Security Group | `AWS::EC2::SecurityGroup` | Inbound: 443 from App |

---

### Stack 2: Core Infrastructure (`core-infra`)

**Workflow:** `AWS_02_Create_Core_Infrastructure.yml`
**Template:** `DevOps/AWS/CloudFormation/core-infrastructure.yaml`

| Resource | Type | Details |
|---|---|---|
| RDS PostgreSQL 16 | `AWS::RDS::DBInstance` | db.t3.medium, GP3, AES256 encryption |
| DB Subnet Group | `AWS::RDS::DBSubnetGroup` | Multi-AZ (2 private subnets) |
| RDS Security Group | `AWS::EC2::SecurityGroup` | Inbound: 5432 from App |
| S3 Documents Bucket | `AWS::S3::Bucket` | Versioning, CORS, encryption, public access blocked |
| S3 Static Bucket | `AWS::S3::Bucket` | Versioning, encryption, public access blocked |
| ECR: api-claims | `AWS::ECR::Repository` | Image scanning on push, lifecycle: retain last 10 |
| ECR: api-members | `AWS::ECR::Repository` | " |
| ECR: api-tenants | `AWS::ECR::Repository` | " |
| ECR: api-entitlements | `AWS::ECR::Repository` | " |
| ECR: portal-claims-advisor | `AWS::ECR::Repository` | " |
| ECR: portal-claims-member | `AWS::ECR::Repository` | " |
| ECR: portal-batch-assignment | `AWS::ECR::Repository` | " |
| ECR: portal-tenants | `AWS::ECR::Repository` | " |
| ECR: portal-entitlements | `AWS::ECR::Repository` | " |
| ECR: portal-sre | `AWS::ECR::Repository` | " |
| ElastiCache Redis 7.1 | `AWS::ElastiCache::ReplicationGroup` | In-transit + at-rest encryption, auth token |
| Redis Subnet Group | `AWS::ElastiCache::SubnetGroup` | Multi-AZ (2 private subnets) |
| Redis Security Group | `AWS::EC2::SecurityGroup` | Inbound: 6379 from App |

---

### Stack 3: Observability (`observability`)

**Workflow:** `AWS_03_Create_Observability.yml`
**Template:** `DevOps/AWS/CloudFormation/observability.yaml`

| Resource | Type | Details |
|---|---|---|
| Log Group: api-claims | `AWS::Logs::LogGroup` | /claims-proc/{env}/api-claims, 30-day retention |
| Log Group: api-members | `AWS::Logs::LogGroup` | /claims-proc/{env}/api-members |
| Log Group: api-tenants | `AWS::Logs::LogGroup` | /claims-proc/{env}/api-tenants |
| Log Group: api-entitlements | `AWS::Logs::LogGroup` | /claims-proc/{env}/api-entitlements |
| Log Group: portal-claims-advisor | `AWS::Logs::LogGroup` | /claims-proc/{env}/portal-claims-advisor |
| Log Group: portal-claims-member | `AWS::Logs::LogGroup` | /claims-proc/{env}/portal-claims-member |
| Log Group: portal-batch-assignment | `AWS::Logs::LogGroup` | /claims-proc/{env}/portal-batch-assignment |
| Log Group: portal-tenants | `AWS::Logs::LogGroup` | /claims-proc/{env}/portal-tenants |
| Log Group: portal-entitlements | `AWS::Logs::LogGroup` | /claims-proc/{env}/portal-entitlements |
| Log Group: portal-sre | `AWS::Logs::LogGroup` | /claims-proc/{env}/portal-sre |
| SNS Alert Topic | `AWS::SNS::Topic` | Alarm notification delivery |
| CloudWatch Dashboard | `AWS::CloudWatch::Dashboard` | 5 widgets: service status, request rate, error rate, P99 latency, JVM heap |
| Alarm: High 5xx Rate | `AWS::CloudWatch::Alarm` | > 1% error rate over 5 min |
| Alarm: High P99 Latency | `AWS::CloudWatch::Alarm` | > 2000ms over 5 min |
| Alarm: Low Availability | `AWS::CloudWatch::Alarm` | < 99.5% over 5 min |
| Alarm: RDS CPU High | `AWS::CloudWatch::Alarm` | > 80% over 5 min |
| Alarm: RDS Free Storage Low | `AWS::CloudWatch::Alarm` | < 5GB over 5 min |
| CloudTrail | `AWS::CloudTrail::Trail` | Global events, log file validation |
| CloudTrail S3 Bucket | `AWS::S3::Bucket` | Glacier after 30d, expire 90d |
| CloudTrail Log Group | `AWS::Logs::LogGroup` | /claims-proc/{env}/cloudtrail |
| CloudTrail IAM Role | `AWS::IAM::Role` | CloudTrail → CloudWatch Logs |

---

### Stack 4: Auth — Cognito (`auth`) — DISABLED

**Workflow:** `AWS_04_Create_Auth_Cognito.yml` (commented out)
**Template:** `DevOps/AWS/CloudFormation/auth-cognito.yaml`

| Resource | Type | Status |
|---|---|---|
| Cognito User Pool | `AWS::Cognito::UserPool` | DISABLED |
| Cognito App Client | `AWS::Cognito::UserPoolClient` | DISABLED |
| Cognito Domain | `AWS::Cognito::UserPoolDomain` | DISABLED |
| User Pool Groups (5) | `AWS::Cognito::UserPoolGroup` | DISABLED (claims-admin, claims-member, sre-ops, tenant-admin, batch-operator) |

---

### Stack 5: Messaging & Search (`messaging-search`) — OPTIONAL

**Workflow:** `AWS_05_Create_Messaging_Search.yml`
**Template:** `DevOps/AWS/CloudFormation/messaging-search.yaml`

| Resource | Type | Status | Details |
|---|---|---|---|
| MSK Kafka Cluster | `AWS::MSK::Cluster` | **DISABLED** | Uses embedded Kafka instead |
| MSK Configuration | `AWS::MSK::Configuration` | **DISABLED** | " |
| Kinesis: Claims Topic | `AWS::Kinesis::Stream` | **DISABLED** | " |
| Kinesis: Documents Topic | `AWS::Kinesis::Stream` | **DISABLED** | " |
| Kinesis: Notifications Topic | `AWS::Kinesis::Stream` | **DISABLED** | " |
| Kinesis: Audit Topic | `AWS::Kinesis::Stream` | **DISABLED** | " |
| OpenSearch 2.11 | `AWS::OpenSearchService::Domain` | Optional | t3.small.search, 20GB GP3, encryption at rest |
| OpenSearch Security Group | `AWS::EC2::SecurityGroup` | Optional | Inbound: 443 from App |

---

### Stack 6: Secrets Manager (`secrets`)

**Workflow:** `AWS_06_Create_Secrets_Manager.yml`
**Template:** `DevOps/AWS/CloudFormation/secrets-manager.yaml`

| Resource | Type | Details |
|---|---|---|
| RDS Credentials | `AWS::SecretsManager::Secret` | username, host, port, dbname, auto-generated password |
| Redis Credentials | `AWS::SecretsManager::Secret` | host, port, auto-generated auth token |
| Messaging Credentials | `AWS::SecretsManager::Secret` | bootstrap servers, SASL password |
| OpenSearch Credentials | `AWS::SecretsManager::Secret` | endpoint, username, password |
| Encryption Keys | `AWS::SecretsManager::Secret` | AES-256-GCM key (44-char base64) |
| JWT Signing Key | `AWS::SecretsManager::Secret` | 64-char base64 signing key |
| Cognito Config | `AWS::SecretsManager::Secret` | DISABLED |

---

### Stack 7: EKS Cluster (`eks`) — DISABLED BY DEFAULT

**Workflow:** `AWS_07_Create_EKS_Cluster.yml`
**Template:** `DevOps/AWS/CloudFormation/eks-cluster.yaml`

| Resource | Type | Status | Details |
|---|---|---|---|
| EKS Cluster Role | `AWS::IAM::Role` | DISABLED | eks.amazonaws.com service principal |
| EKS Node Group Role | `AWS::IAM::Role` | DISABLED | ec2.amazonaws.com + CloudWatchAgentServerPolicy |
| EKS Cluster (v1.29) | `AWS::EKS::Cluster` | DISABLED | API, audit, authenticator, controller, scheduler logging |
| EKS Cluster Security Group | `AWS::EC2::SecurityGroup` | DISABLED | Inbound: 443 |
| Managed Node Group | `AWS::EKS::Nodegroup` | DISABLED | t3.medium, 2-40 nodes, 50GB disk |
| OIDC Provider | `AWS::IAM::OIDCProvider` | DISABLED | IRSA (IAM Roles for Service Accounts) |

---

### Stack 8: Docker Build & ECR Push (no CloudFormation stack)

**Workflow:** `AWS_08_Build_Push_ECR.yml`

Builds and pushes Docker images for all 10 modules to ECR:

| Module | ECR Repository | Port |
|---|---|---|
| API-Claims | `claims-proc/api-claims` | 8083 |
| API-Members | `claims-proc/api-members` | 8084 |
| API-Tenants | `claims-proc/api-tenants` | 8086 |
| API-Entitlements | `claims-proc/api-entitlements` | 8087 |
| Portal-Claims-Advisor | `claims-proc/portal-claims-advisor` | 8081 |
| Portal-Claims-Member | `claims-proc/portal-claims-member` | 8082 |
| Portal-Batch-Assignment | `claims-proc/portal-batch-assignment` | 8085 |
| Portal-Tenants | `claims-proc/portal-tenants` | 8088 |
| Portal-Entitlements | `claims-proc/portal-entitlements` | 8089 |
| Portal-SRE | `claims-proc/portal-sre` | 8090 |

Each image is tagged with: `latest`, `{env}-latest`, `{commit-sha}`.

---

### Stack 9: API Gateway & Fargate (`fargate`)

**Workflow:** `AWS_09_Create_API_Gateway_Fargate.yml`
**Template:** `DevOps/AWS/CloudFormation/api-gateway-fargate.yaml`

| Resource | Type | Details |
|---|---|---|
| ECS Cluster | `AWS::ECS::Cluster` | claims-proc-{env} |
| Application Load Balancer | `AWS::ElasticLoadBalancingV2::LoadBalancer` | Public-facing, 2 AZs |
| ALB Listener (HTTP) | `AWS::ElasticLoadBalancingV2::Listener` | Port 80 |
| 10 Target Groups | `AWS::ElasticLoadBalancingV2::TargetGroup` | One per microservice |
| 10 Listener Rules | `AWS::ElasticLoadBalancingV2::ListenerRule` | Path-based routing (see below) |
| 10 ECS Task Definitions | `AWS::ECS::TaskDefinition` | Fargate, configurable CPU/memory |
| 10 ECS Fargate Services | `AWS::ECS::Service` | One per microservice |
| ECS Task Execution Role | `AWS::IAM::Role` | ECR pull, CloudWatch Logs, Secrets Manager |

**ALB Path-Based Routing:**

| Path Pattern | Target Service | Port |
|---|---|---|
| `/advisor/*` | Portal-Claims-Advisor (default) | 8081 |
| `/member/*` | Portal-Claims-Member | 8082 |
| `/api/v1/claims/*` | API-Claims | 8083 |
| `/api/v1/members/*` | API-Members | 8084 |
| `/batch/*` | Portal-Batch-Assignment | 8085 |
| `/api/v1/tenants/*` | API-Tenants | 8086 |
| `/api/v1/entitlements/*` | API-Entitlements | 8087 |
| `/tenant-mgmt/*` | Portal-Tenants | 8088 |
| `/entitlement-mgmt/*` | Portal-Entitlements | 8089 |
| `/sre/*` | Portal-SRE | 8090 |

---

### Stack 10: Data Seed (`data-seed`)

**Workflow:** `AWS_10_Seed_Data.yml`
**Template:** `DevOps/AWS/CloudFormation/data-seed-lambda.yaml`

| Resource | Type | Details |
|---|---|---|
| Lambda Function | `AWS::Lambda::Function` | Python 3.12, 256MB, 300s timeout, VPC-connected |
| Lambda Security Group | `AWS::EC2::SecurityGroup` | Outbound: 5432 (RDS), 443 (CloudWatch/Secrets) |
| Lambda IAM Role | `AWS::IAM::Role` | VPC access, CloudWatch Logs, Secrets Manager read |
| Lambda Log Group | `AWS::Logs::LogGroup` | /aws/lambda/claims-proc-{env}-data-seed, 14-day retention |

**Seed Data:**

| Entity | Count | Details |
|---|---|---|
| Tenants | 100 | Industries, plans, status |
| Members | 1,000 | 10 per tenant |
| Claims | 10 | One per stage (INTAKE_RECEIVED → SETTLEMENT) |
| Privileges | 13 | Granular permissions |
| Roles | 9 | Role-privilege mappings |
| Users | 6 | User-role assignments |

---

## Operational Workflows (No Infrastructure)

| Workflow | Purpose |
|---|---|
| `AWS_00_Environment_Dashboard.yml` | Displays current status of all deployed stacks |
| `AWS_00_Cost_Report.yml` | Month-to-date cost breakdown by service + forecast |
| `AWS_98_Destroy_All.yml` | Destroys ALL stacks in reverse order with safety confirmation |
| `AWS_99_Orchestrator_Full_Deploy.yml` | One-click deploy of all stacks in dependency order |

---

## Observability & SRE Architecture

### Three Pillars of Observability

```
┌─────────────────────────────────────────────────────────────────┐
│                     Observability Stack                          │
├────────────────────┬────────────────────┬───────────────────────┤
│      Metrics       │      Traces        │        Logs           │
│                    │                    │                       │
│  Micrometer +      │  OpenTelemetry +   │  CloudWatch Logs +    │
│  Prometheus        │  W3C Trace Context │  CloudTrail           │
│  /actuator/        │  OTLP Exporter     │  10 log groups        │
│  prometheus        │  → Jaeger/Zipkin   │  → S3 archival        │
│                    │  → X-Ray           │  → Glacier (30d)      │
└────────────────────┴────────────────────┴───────────────────────┘
```

### SLOs Tracked Per Tenant

| SLO | Target | Metric | Window |
|---|---|---|---|
| **Availability** | 99.9% | `1 - (errors / total requests)` | 30 days |
| **Latency P95** | 200ms | `http_server_requests_seconds` P95 | 30 days |
| **Latency P99** | 500ms | `http_server_requests_seconds` P99 | 30 days |
| **Claims Throughput** | 100+/day | Claims processed per day | 1 day |

### Error Budget Burn Rate Alerts

| Alert | Burn Rate | Window | Severity |
|---|---|---|---|
| Fast Burn | 14.4x | 1 hour | CRITICAL |
| Slow Burn | 6x | 6 hours | WARNING |

### CloudWatch Alarms (5)

| Alarm | Threshold | Period |
|---|---|---|
| High 5xx Rate | > 1% | 5 min |
| High P99 Latency | > 2000ms | 5 min |
| Low Availability | < 99.5% | 5 min |
| RDS CPU High | > 80% | 5 min |
| RDS Free Storage Low | < 5GB | 5 min |

### Prometheus Alert Rules (50+)

| Category | Examples |
|---|---|
| **SLO Alerts** | AvailabilitySLOBreach, LatencyP99SLOBreach, ErrorBudgetFastBurn, ErrorBudgetSlowBurn |
| **Application Health** | ServiceDown, HighErrorRate, HighRequestLatency, JvmHeapHigh, ThreadPoolExhaustion |
| **Claims Processing** | ClaimProcessingBacklog, ClaimStageStuck, AiExtractionFailureRate, BatchImportFailure |
| **Infrastructure** | DatabaseConnectionFailure, RedisConnectionFailure, KafkaBrokerDown, DiskSpaceLow |
| **Auth & Resilience** | HighAuthenticationFailureRate, AuthenticationFailureSpike, CircuitBreakerOpen, BulkheadRejectionRate |

### Grafana Dashboards (5)

| Dashboard | File | Panels |
|---|---|---|
| SLO Overview | `slo-overview.json` | Availability %, P99 latency, SLO window selector (5m→30d) |
| Application Health | `application-health.json` | JVM metrics, HTTP request rates, thread/connection pools |
| Claims Processing | `claims-processing.json` | Claim throughput, processing time, AI extraction metrics |
| Infrastructure | `infrastructure.json` | PostgreSQL, Redis, Kafka, disk space |
| Auth & Resilience | `auth-resilience.json` | Authentication failures, circuit breaker states |

---

## Portal-SRE Dashboard (Port 8090)

The SRE dashboard is a full-stack Spring Boot + Angular 18 application with 8 pages:

| Page | What It Shows |
|---|---|
| **Dashboard** | Central hub — services UP, active incidents, recent deployments, error budget |
| **Service Health** | Real-time polling of all 9 microservices `/actuator/health`, dependency map |
| **SLO Compliance** | Per-tenant SLO status (Met/At Risk/Breached), error budget tracking |
| **Incidents** | P1-P4 incidents, status tracking (OPEN→RESOLVED), MTTR calculation |
| **Deployments** | Deployment history with version, environment, commit hash, rollback tracking |
| **Cloud Resources** | Multi-cloud resource inventory (AWS/Azure/GCP), health status per provider |
| **Tenant Analytics** | Top tenants by usage, composite health scores (0-100), cost MTD |
| **Cost Tracking** | Cost breakdown by category (compute, database, messaging, cache, storage) |

---

## Application Instrumentation

### OpenTelemetry + Micrometer (All 10 Services)

```yaml
# Every service exposes:
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus
  tracing:
    sampling:
      probability: 1.0          # 100% trace sampling
    propagation:
      type: w3c                 # W3C Trace Context propagation

# OTLP export for distributed tracing:
otel:
  exporter:
    otlp:
      endpoint: http://localhost:4318/v1/traces
```

### Dependencies (per service)

| Dependency | Purpose |
|---|---|
| `micrometer-registry-prometheus` | Expose metrics at `/actuator/prometheus` |
| `micrometer-tracing-bridge-otel` | Bridge Micrometer → OpenTelemetry |
| `opentelemetry-exporter-otlp` | Export traces via OTLP protocol |

### Prometheus Scrape Targets

All 10 application services + 3 infrastructure exporters:

| Target | Port | Endpoint |
|---|---|---|
| Portal-Claims-Advisor | 8081 | `/actuator/prometheus` |
| Portal-Claims-Member | 8082 | `/actuator/prometheus` |
| API-Claims | 8083 | `/actuator/prometheus` |
| API-Members | 8084 | `/actuator/prometheus` |
| Portal-Batch-Assignment | 8085 | `/actuator/prometheus` |
| API-Tenants | 8086 | `/actuator/prometheus` |
| API-Entitlements | 8087 | `/actuator/prometheus` |
| Portal-Tenants | 8088 | `/actuator/prometheus` |
| Portal-Entitlements | 8089 | `/actuator/prometheus` |
| Portal-SRE | 8090 | `/actuator/prometheus` |
| Kafka Exporter | 9308 | `/metrics` |
| Redis Exporter | 9121 | `/metrics` |
| PostgreSQL Exporter | 9187 | `/metrics` |

---

## Cost Estimate (12-Hour Research Window)

### Default Configuration (MSK/EKS/Cognito disabled)

| Service | Est. Cost (12 hrs) | Notes |
|---|---|---|
| ECS Fargate (10 tasks) | ~$1.00 | 0.25 vCPU / 0.5 GB per task, per-second billing |
| ALB | ~$0.30 | Hourly billing |
| RDS PostgreSQL (db.t3.micro) | ~$0.25 | |
| ElastiCache Redis | ~$0.25 | cache.t3.micro |
| NAT Gateway | ~$0.50 | Hourly + data transfer |
| ECR | ~$0.10 | Storage only |
| Secrets Manager (7 secrets) | ~$0.01 | |
| CloudWatch (logs, alarms, dashboard) | ~$0.10 | |
| CloudTrail | ~$0.05 | |
| Lambda (data seed) | ~$0.01 | Runs once |
| S3 (2 buckets) | ~$0.01 | Minimal storage |
| **Total (default)** | **~$2.50 - $4.00** | |

### If Optional Services Enabled

| Service | Additional Cost (12 hrs) |
|---|---|
| MSK Kafka (2 brokers, kafka.t3.small) | +$10-15 |
| OpenSearch (t3.small.search) | +$3-5 |
| EKS Cluster + Nodes | +$5-10 |
| Cognito | +$0 (free tier) |

### After AWS_98 Destroy All

| Remaining Resources | Cost |
|---|---|
| Everything | **$0.00** |

All CloudFormation stacks, ECR images, secrets, log groups, and S3 data are destroyed. Zero residual charges.

---

## GitHub Actions Workflow Reference

| Workflow | File | Purpose |
|---|---|---|
| **AWS_00** | `AWS_00_Environment_Dashboard.yml` | Display status of all deployed stacks |
| **AWS_00** | `AWS_00_Cost_Report.yml` | Month-to-date cost report + forecast |
| **AWS_01** | `AWS_01_Create_VPC_SecurityGroups.yml` | Create networking foundation |
| **AWS_01** | `AWS_01_Destroy_VPC_SecurityGroups.yml` | Destroy VPC stack |
| **AWS_02** | `AWS_02_Create_Core_Infrastructure.yml` | Create RDS, S3, ECR, Redis |
| **AWS_02** | `AWS_02_Destroy_Core_Infrastructure.yml` | Destroy core-infra stack |
| **AWS_03** | `AWS_03_Create_Observability.yml` | Create CloudWatch, CloudTrail, alarms |
| **AWS_03** | `AWS_03_Destroy_Observability.yml` | Destroy observability stack |
| **AWS_04** | `AWS_04_Create_Auth_Cognito.yml` | Create Cognito (disabled) |
| **AWS_04** | `AWS_04_Destroy_Auth_Cognito.yml` | Destroy Cognito (disabled) |
| **AWS_05** | `AWS_05_Create_Messaging_Search.yml` | Create MSK/Kinesis/OpenSearch (optional) |
| **AWS_05** | `AWS_05_Destroy_Messaging_Search.yml` | Destroy messaging-search stack |
| **AWS_06** | `AWS_06_Create_Secrets_Manager.yml` | Create Secrets Manager entries |
| **AWS_06** | `AWS_06_Destroy_Secrets_Manager.yml` | Destroy secrets stack |
| **AWS_07** | `AWS_07_Create_EKS_Cluster.yml` | Create EKS cluster (disabled by default) |
| **AWS_07** | `AWS_07_Destroy_EKS_Cluster.yml` | Destroy EKS stack |
| **AWS_08** | `AWS_08_Build_Push_ECR.yml` | Build Docker images → push to ECR |
| **AWS_09** | `AWS_09_Create_API_Gateway_Fargate.yml` | Deploy ALB + Fargate services |
| **AWS_09** | `AWS_09_Destroy_API_Gateway_Fargate.yml` | Destroy Fargate stack |
| **AWS_10** | `AWS_10_Seed_Data.yml` | Seed database via Lambda |
| **AWS_10** | `AWS_10_Destroy_Data_Seed.yml` | Destroy data-seed Lambda |
| **AWS_98** | `AWS_98_Destroy_All.yml` | Destroy ALL stacks (reverse order, safety confirmation) |
| **AWS_99** | `AWS_99_Orchestrator_Full_Deploy.yml` | One-click full environment deploy |

---

## AWS Services Summary

Total unique AWS services used across all stacks:

| # | AWS Service | Purpose in This Project |
|---|---|---|
| 1 | **Amazon VPC** | Network isolation, public/private subnets |
| 2 | **Amazon EC2** (Security Groups, EIPs) | Network security, NAT Gateway |
| 3 | **Amazon RDS** (PostgreSQL 16) | Primary database for all microservices |
| 4 | **Amazon S3** | Document storage, static assets, CloudTrail logs |
| 5 | **Amazon ECR** | Docker image registry (10 repositories) |
| 6 | **Amazon ElastiCache** (Redis 7.1) | Caching layer (when `cache.enabled: true`) |
| 7 | **Amazon ECS** (Fargate) | Serverless container orchestration |
| 8 | **Elastic Load Balancing** (ALB) | Path-based routing to 10 microservices |
| 9 | **AWS Secrets Manager** | Credentials and encryption keys |
| 10 | **Amazon CloudWatch** (Logs, Alarms, Dashboards) | Centralized logging and monitoring |
| 11 | **Amazon CloudWatch Application Signals** | SLO tracking, service metrics, reliability targets |
| 12 | **AWS CloudTrail** | API audit logging with S3 archival |
| 13 | **Amazon SNS** | Alert notifications |
| 14 | **AWS Lambda** | Database seeding (Python 3.12) |
| 15 | **AWS IAM** | Roles and policies for ECS, Lambda, CloudTrail |
| 16 | **Amazon CloudFormation** | Infrastructure as Code (9 stacks) |
| 17 | **Amazon EKS** | Kubernetes orchestration (optional, disabled) |
| 18 | **Amazon MSK** | Managed Kafka (optional, disabled — uses embedded) |
| 19 | **Amazon Kinesis** | Event streaming (optional, disabled — uses embedded) |
| 20 | **Amazon OpenSearch** | Full-text search and log analysis (optional) |
| 21 | **Amazon Cognito** | User authentication (optional, disabled) |

---

## Key File Locations

| Category | Path |
|---|---|
| CloudFormation Templates | `DevOps/AWS/CloudFormation/*.yaml` |
| GitHub Workflows | `.github/workflows/AWS_*.yml` |
| Prometheus Config | `DevOps/Local/Observability/Prometheus/prometheus.yml` |
| Prometheus Alert Rules | `DevOps/Local/Observability/Prometheus/alert-rules.yml` |
| Prometheus Recording Rules | `DevOps/Local/Observability/Prometheus/recording-rules.yml` |
| Grafana Dashboards | `DevOps/Local/Observability/Grafana/dashboards/*.json` |
| Portal-SRE Backend | `Portal-SRE/src/main/java/com/healthcare/claims/portal/sre/` |
| Portal-SRE Frontend | `Portal-SRE/src/main/frontend/src/app/pages/` |
| OpenTelemetry Config | `*/src/main/resources/application.yml` (each service) |
