# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
Claims-Processor-With-SRE is a microservice within the HealthCare-Plans-AI-Platform.

On March 13th 2026 in AWS News website, AWS mentioned "Amazon CloudWatch Application Signals adds new SLO capabilities"
https://aws.amazon.com/about-aws/whats-new/2026/03/cloudwatch-application-signals-adds-slo-capabilities/

This project is designed to showcase the above new capabilities of AWS CloudWatch Application Signals for monitoring and managing Service Level Objectives (SLOs) in a healthcare claims processing microservice. 
The project will demonstrate how to define and monitor SLOs using AWS CloudWatch, and how to use these insights to improve the reliability and performance of the claims processing service.

- This project is designed to intake healthcare claims data, process it using AI models to identify patterns and anomalies, and provide insights for improving claim processing efficiency and accuracy. 
- The service will also incorporate Site Reliability Engineering (SRE) principles to ensure high availability and performance.
- This project is designed to be deployed in
  - AWS
  - Azure
  - GCP
  - On-premises environments (Local with Docker Desktop, Kubernetes)
- Claims Support intake a claim screenshot or PDF, uploads it to the system, and the system processes the claim using AI models to extract relevant information, 
- Extract information is usefu to identify patterns, and provide insights for improving claim processing efficiency and accuracy. 
- The system also incorporates SRE principles to ensure high availability and performance.

#### Tech stack:
    - Java / Spring Boot for backend development
    - AWS Cloud services for deployment and monitoring
    - AWS RDS Postgres DB for cloud; 
    - AWS Lambda for serverless processing; 
    - AWS S3 for storage; 
    - AWS CloudWatch for monitoring and logging
    - AWS API Gateway for API management
    - AWS Elastic Beanstalk or AWS Fargate for application deployment
    - For local development 
        - Docker based Postgres, Redis, Kafka, Prometheus, Grafana, Jaeger, and Zipkin containers for local development and testing
        - Ollama for local AI model inference with Mistral 7B Instruct model
        - LocalStack for simulating AWS services locally
    - Angular for Customer Portal frontend development and Admin Portal frontend development (two different Angular projects)

#### Architecture Diagram:

##### Business / Functional View
```mermaid
flowchart TB
    subgraph BusinessActors["Business Actors"]
        Member["Member / Patient"]
        Provider["Healthcare Provider"]
        ClaimsAdmin["Claims Administrator"]
        SREOps["SRE / Operations Team"]
    end

    subgraph BusinessFunctions["Business Functions"]
        direction TB
        subgraph ClaimLifecycle["Claim Lifecycle Management"]
            Submit["Submit Claim<br/>(Screenshot / PDF Upload)"]
            Validate["Validate &<br/>Extract Data"]
            Adjudicate["AI-Assisted<br/>Adjudication"]
            Settle["Settlement &<br/>Payment"]
        end
        subgraph Insights["Analytics & Insights"]
            Patterns["Pattern Detection<br/>& Anomaly Identification"]
            Efficiency["Processing Efficiency<br/>& Accuracy Metrics"]
            Reporting["Claims Reporting<br/>& Dashboards"]
        end
        subgraph Reliability["Reliability & Operations"]
            SLOs["SLO / SLI<br/>Monitoring"]
            Incidents["Incident Management<br/>& Post-Mortems"]
            Capacity["Capacity Planning<br/>& Auto-Scaling"]
        end
    end

    Member -->|"Submits claim"| Submit
    Provider -->|"Submits claim"| Submit
    Submit --> Validate --> Adjudicate --> Settle
    ClaimsAdmin -->|"Reviews & manages"| ClaimLifecycle
    Adjudicate --> Patterns
    Patterns --> Efficiency --> Reporting
    SREOps -->|"Monitors & responds"| Reliability
    ClaimLifecycle -.->|"Emits metrics"| SLOs
```

##### Claim Intake & Processing Workflow

> **Precondition:** Claims are always uploaded for an existing customer. The customer must already exist in the system before a claim can be submitted.

**Supported file formats:** PNG, JPG, TIFF (screenshots/images), PDF, DOCX, XLSX, CSV, and EDI (X12 837) claim files.

**Claim Processing Stages (Staff Dropdown):**

| Stage | Description |
|-------|-------------|
| `INTAKE_RECEIVED` | Claim artifact uploaded, pending initial review |
| `DOCUMENT_VERIFICATION` | Verifying document legibility, completeness, and format |
| `DATA_EXTRACTION` | AI extracts structured data from the uploaded artifact |
| `EXTRACTION_REVIEW` | Staff reviews and corrects AI-extracted data |
| `ELIGIBILITY_CHECK` | Verifying customer eligibility and policy coverage |
| `ADJUDICATION` | AI-assisted decision on claim approval, denial, or partial |
| `ADJUDICATION_REVIEW` | Staff reviews AI adjudication recommendation |
| `APPROVED` | Claim approved for payment |
| `DENIED` | Claim denied with reason codes |
| `PARTIAL_APPROVED` | Claim partially approved with adjustments |
| `SETTLEMENT` | Payment processing initiated |
| `CLOSED` | Claim fully settled and archived |
| `APPEAL` | Customer disputed denial, claim re-opened for review |

```mermaid
flowchart TB
    subgraph StaffUI["Claims Support Staff - Admin Portal (Angular)"]
        SearchCust["1. Search & Select<br/>Existing Customer"]
        SelectStage["2. Select Stage<br/>from Dropdown"]
        UploadFiles["3. Upload Claim Artifact(s)<br/>(Image / PDF / Doc / EDI)"]
        SubmitClaim["4. Submit Claim"]
    end

    subgraph Backend["Backend Processing (Spring Boot)"]
        direction TB
        Validate["Validate Customer Exists<br/>& File Format Check"]

        subgraph FileProcessing["File Processing"]
            Store["Store Original File<br/>(S3 / Document Store)"]
            Classify["Classify Document Type<br/>(Image vs PDF vs EDI)"]
        end

        subgraph AIExtraction["AI Data Extraction"]
            OCR["OCR & Text Extraction<br/>(Images / Scanned PDFs)"]
            Parse["Structured Data Parsing<br/>(Digital PDFs / EDI / CSV)"]
            NLP["NLP Entity Extraction<br/>(Provider, Diagnosis, CPT Codes,<br/>Dates, Amounts)"]
        end

        subgraph Enrichment["Data Enrichment & Validation"]
            CodeLookup["Medical Code Validation<br/>(ICD-10, CPT, NPI Lookup)"]
            PolicyCheck["Eligibility &<br/>Policy Coverage Check"]
            DupCheck["Duplicate &<br/>Fraud Detection"]
        end

        subgraph Adjudication["AI-Assisted Adjudication"]
            Rules["Business Rules<br/>Engine"]
            AIDecision["AI Recommendation<br/>(Approve / Deny / Partial)"]
            Confidence["Confidence Score<br/>& Reason Codes"]
        end

        SaveResult["Persist Claim Record<br/>(PostgreSQL)"]
    end

    subgraph Events["Event Notifications (Kafka)"]
        ClaimCreated["claim.created"]
        ExtractionComplete["claim.extraction.complete"]
        AdjudicationComplete["claim.adjudication.complete"]
        StageChanged["claim.stage.changed"]
    end

    subgraph CustomerUI["Customer Portal (Angular)"]
        ClaimStatus["View Claim Status<br/>& Current Stage"]
        ExtractedData["View Extracted<br/>Claim Details"]
        Timeline["Claim Processing<br/>Timeline"]
        Documents["View Uploaded<br/>Documents"]
    end

    SearchCust --> SelectStage --> UploadFiles --> SubmitClaim
    SubmitClaim --> Validate
    Validate --> Store --> Classify
    Classify -->|"Image / Scanned"| OCR
    Classify -->|"Digital PDF / EDI"| Parse
    OCR & Parse --> NLP
    NLP --> CodeLookup --> PolicyCheck --> DupCheck
    DupCheck --> Rules --> AIDecision --> Confidence
    Confidence --> SaveResult

    Validate --> ClaimCreated
    NLP --> ExtractionComplete
    Confidence --> AdjudicationComplete
    SelectStage --> StageChanged

    ClaimCreated & ExtractionComplete & AdjudicationComplete & StageChanged --> CustomerUI
    SaveResult --> ExtractedData
```

**Workflow summary:**
1. **Staff searches and selects an existing customer** in the Admin Portal
2. **Staff selects the claim stage** from the dropdown (typically starts at `INTAKE_RECEIVED`)
3. **Staff uploads one or more claim artifacts** — the system supports multiple files per claim (e.g., a scanned claim form image + an itemized bill PDF)
4. **Backend validates** customer existence and file formats, then stores originals in S3
5. **AI extracts structured data** — OCR for images/scanned PDFs, structured parsing for digital PDFs/EDI/CSV, then NLP extracts medical codes, provider info, diagnosis, dates, and amounts
6. **Data is enriched and validated** — medical code lookups (ICD-10, CPT, NPI), eligibility/policy checks, duplicate and fraud detection
7. **AI-assisted adjudication** — business rules + AI model produce a recommendation (approve/deny/partial) with confidence score and reason codes
8. **Staff reviews** extracted data and adjudication recommendation, corrects if needed, and advances the stage via the dropdown
9. **Customer sees results** in the Customer Portal — claim status, extracted details, processing timeline, and uploaded documents
10. **Kafka events** are emitted at each stage transition, enabling async downstream processing, audit trails, and real-time UI updates

##### 1. Core Application Architecture
```mermaid
flowchart LR
    subgraph Portals["User Portals"]
        CP["Customer Portal<br/>(Angular)"]
        AP["Admin Portal<br/>(Angular)"]
    end

    subgraph Core["Claims Processing Core"]
        API["Claims REST API"]
        Engine["Processing Engine"]
        DataAccess["Data Access Layer"]
    end

    subgraph Data["Data Stores"]
        DB["PostgreSQL<br/>(Claims Data)"]
        Cache["Redis<br/>(Cache)"]
        DocStore["Document Store<br/>(Claim PDFs / Images)"]
    end

    CP & AP --> API
    API --> Engine
    Engine --> DataAccess
    DataAccess --> DB & Cache & DocStore
```

##### 2. Event-Driven Architecture
```mermaid
flowchart LR
    subgraph Producers["Event Producers"]
        ClaimSubmit["Claim Submitted"]
        ClaimUpdate["Claim Updated"]
        DocUpload["Document Uploaded"]
    end

    subgraph EventBus["Apache Kafka (Event Bus)"]
        direction TB
        ClaimsTopic["claims-topic"]
        DocsTopic["documents-topic"]
        NotifTopic["notifications-topic"]
        AuditTopic["audit-topic"]
    end

    subgraph Consumers["Event Consumers"]
        Validator["Validation<br/>Consumer"]
        AIProcessor["AI Processing<br/>Consumer"]
        Notifier["Notification<br/>Consumer"]
        Auditor["Audit Trail<br/>Consumer"]
    end

    ClaimSubmit --> ClaimsTopic
    ClaimUpdate --> ClaimsTopic
    DocUpload --> DocsTopic
    ClaimsTopic --> Validator
    ClaimsTopic --> AIProcessor
    DocsTopic --> AIProcessor
    NotifTopic --> Notifier
    AuditTopic --> Auditor
    Validator --> NotifTopic
    AIProcessor --> NotifTopic & AuditTopic
```

##### 3. Serverless Architecture
```mermaid
flowchart TB
    subgraph Triggers["Triggers"]
        APIGW["AWS API Gateway"]
        S3Event["S3 Event<br/>(Document Upload)"]
        Schedule["CloudWatch<br/>Scheduled Rule"]
    end

    subgraph Lambdas["AWS Lambda Functions"]
        DocExtract["Document Extraction<br/>(OCR / Text Parse)"]
        AIInference["AI Inference<br/>(Pattern Detection)"]
        ClaimEnrich["Claim Enrichment<br/>(Data Augmentation)"]
        ReportGen["Report Generation<br/>(Scheduled)"]
    end

    subgraph Downstream["Downstream Services"]
        RDS["RDS PostgreSQL"]
        S3Out["S3 (Results)"]
        SNS["SNS Notifications"]
    end

    APIGW --> DocExtract & AIInference
    S3Event --> DocExtract
    Schedule --> ReportGen
    DocExtract --> ClaimEnrich
    ClaimEnrich --> AIInference
    AIInference --> RDS & S3Out
    ReportGen --> S3Out & SNS
```

##### 4. AI Integration Architecture
```mermaid
flowchart TB
    subgraph Input["Claim Input"]
        PDF["PDF / Screenshot"]
        Structured["Structured Data<br/>(Form Fields)"]
    end

    subgraph AIOrchestration["AI Orchestration Layer (Spring Boot)"]
        Router["AI Request Router"]
        PreProcess["Pre-Processing<br/>(Data Normalization)"]
        PostProcess["Post-Processing<br/>(Confidence Scoring)"]
    end

    subgraph CloudAI["Cloud AI (AWS)"]
        Lambda["AWS Lambda<br/>(Serverless Inference)"]
    end

    subgraph LocalAI["Local AI (Development)"]
        Ollama["Ollama<br/>(Mistral 7B Instruct)"]
    end

    subgraph AICapabilities["AI Capabilities"]
        OCR["Document OCR &<br/>Data Extraction"]
        Anomaly["Anomaly &<br/>Fraud Detection"]
        Pattern["Pattern Recognition<br/>& Classification"]
        Suggest["Adjudication<br/>Recommendations"]
    end

    PDF & Structured --> PreProcess
    PreProcess --> Router
    Router -->|"Production"| Lambda
    Router -.->|"Local Dev"| Ollama
    Lambda & Ollama --> PostProcess
    PostProcess --> OCR & Anomaly & Pattern & Suggest
```

##### 5. AWS Cloud Deployment Architecture
```mermaid
flowchart TB
    subgraph Internet["Internet"]
        Users["Users / Browsers"]
    end

    subgraph AWS["AWS Cloud"]
        subgraph Edge["Edge Layer"]
            APIGW["API Gateway"]
            IAM["IAM<br/>(Auth & Policies)"]
        end

        subgraph VPC["VPC (Private Network)"]
            subgraph Public["Public Subnet"]
                ALB["Application<br/>Load Balancer"]
            end

            subgraph Private["Private Subnet"]
                subgraph ComputeA["Compute Option A"]
                    EB["Elastic Beanstalk<br/>(Spring Boot)"]
                end
                subgraph ComputeB["Compute Option B"]
                    Fargate["Fargate<br/>(Containerized)"]
                end
            end

            subgraph DataSubnet["Data Subnet"]
                RDS["RDS PostgreSQL"]
                ElastiCache["ElastiCache<br/>(Redis)"]
            end
        end

        S3["S3<br/>(Documents & Static Assets)"]
        Lambda["Lambda<br/>(Serverless Functions)"]
        MSK["Amazon MSK<br/>(Managed Kafka)"]
    end

    Users --> APIGW
    APIGW --> IAM --> ALB
    ALB --> EB & Fargate
    EB & Fargate --> RDS & ElastiCache & S3 & Lambda & MSK
```

##### 6. SRE & Observability Architecture
```mermaid
flowchart TB
    subgraph AppLayer["Application Instrumentation"]
        Micrometer["Micrometer<br/>(Metrics SDK)"]
        SleuthZipkin["Spring Cloud Sleuth<br/>(Trace Propagation)"]
        LogFramework["SLF4J / Logback<br/>(Structured Logging)"]
    end

    subgraph AWSObserve["AWS Native Observability"]
        subgraph CloudWatch["AWS CloudWatch"]
            CWLogs["CloudWatch Logs"]
            CWMetrics["Metrics &<br/>Dashboards"]
            AppSignals["Application Signals<br/>& SLOs"]
            CWAlarms["CloudWatch<br/>Alarms"]
        end
        XRay["AWS X-Ray<br/>(Distributed Tracing)"]
    end

    subgraph OpenSource["Open Source Observability"]
        Prom["Prometheus<br/>(Metrics Collection)"]
        Grafana["Grafana<br/>(Visualization)"]
        Jaeger["Jaeger / Zipkin<br/>(Trace Analysis)"]
    end

    subgraph Respond["Incident Response"]
        PD["PagerDuty"]
        Runbooks["Runbooks &<br/>Auto-Remediation"]
        PostMortem["Post-Mortem<br/>Analysis"]
    end

    subgraph SLOs["SLO Management"]
        Availability["Availability SLO<br/>(99.9% uptime)"]
        Latency["Latency SLO<br/>(p99 < 500ms)"]
        ErrorBudget["Error Budget<br/>Tracking"]
    end

    Micrometer --> CWMetrics & Prom
    LogFramework --> CWLogs
    SleuthZipkin --> XRay & Jaeger
    Prom --> Grafana
    AppSignals --> SLOs
    CWAlarms --> PD
    PD --> Runbooks --> PostMortem
    SLOs --> ErrorBudget
```

##### 7. DevOps CI/CD Architecture
```mermaid
flowchart LR
    subgraph Source["Source Control"]
        GitHub["GitHub<br/>Repository"]
    end

    subgraph CI["Continuous Integration (GitHub Actions)"]
        Build["Build<br/>(Maven)"]
        UnitTest["Unit Tests<br/>(JUnit 5)"]
        IntegTest["Integration Tests<br/>(Testcontainers)"]
        CodeQuality["Code Quality<br/>(SonarQube / Checkstyle)"]
        SecurityScan["Security Scan<br/>(OWASP / Snyk)"]
        DockerBuild["Docker Image<br/>Build & Push"]
    end

    subgraph CD["Continuous Deployment"]
        subgraph Staging["Staging"]
            StageDeploy["Deploy to<br/>Staging"]
            SmokeTest["Smoke &<br/>E2E Tests"]
        end
        subgraph Prod["Production"]
            BlueGreen["Blue/Green<br/>Deployment"]
            Canary["Canary<br/>Release"]
        end
    end

    subgraph Infra["Infrastructure as Code"]
        CFN["CloudFormation /<br/>Terraform"]
        IACValidate["Validate &<br/>Plan"]
    end

    GitHub -->|"Push / PR"| Build
    Build --> UnitTest --> IntegTest --> CodeQuality --> SecurityScan --> DockerBuild
    DockerBuild --> StageDeploy --> SmokeTest
    SmokeTest -->|"Approved"| BlueGreen & Canary
    GitHub --> CFN --> IACValidate --> StageDeploy
```

##### 8. Performance & Scalability Architecture
```mermaid
flowchart TB
    subgraph LoadMgmt["Load Management"]
        ALB["Application<br/>Load Balancer"]
        RateLimit["API Gateway<br/>Rate Limiting"]
        CircuitBreaker["Circuit Breaker<br/>(Resilience4j)"]
    end

    subgraph Scaling["Auto-Scaling"]
        HPA["K8s HPA /<br/>Fargate Auto-Scale"]
        ASG["EC2 Auto Scaling<br/>Group"]
        KafkaPartitions["Kafka Partition<br/>Scaling"]
    end

    subgraph Caching["Caching Strategy"]
        Redis["Redis<br/>(Session & Data Cache)"]
        SpringCache["Spring Cache<br/>(Application Level)"]
    end

    subgraph Async["Async Processing"]
        Kafka["Kafka<br/>(Async Event Processing)"]
        Lambda["Lambda<br/>(Burst Processing)"]
        ThreadPool["Spring Async<br/>(Thread Pools)"]
    end

    subgraph Testing["Performance Testing"]
        LoadTest["Load Testing<br/>(k6 / Gatling)"]
        ChaosEng["Chaos Engineering<br/>(Gremlin)"]
        CapPlan["Capacity<br/>Planning"]
    end

    ALB --> HPA & ASG
    RateLimit --> ALB
    CircuitBreaker --> Redis & Kafka
    HPA -->|"Scale on CPU/Memory"| ASG
    KafkaPartitions -->|"Scale consumers"| Kafka
    LoadTest --> CapPlan
    ChaosEng --> CapPlan
```

##### 9. Spring Boot & Spring Framework Stack
```mermaid
flowchart TB
    subgraph SpringBoot["Spring Boot Application"]
        subgraph Web["Web Layer"]
            MVC["Spring MVC<br/>(REST Controllers)"]
            Validation["Spring Validation<br/>(Bean Validation)"]
            Security["Spring Security<br/>(JWT / OAuth2)"]
        end

        subgraph Service["Service Layer"]
            TX["Spring TX<br/>(Transaction Mgmt)"]
            Async["@Async<br/>(Async Processing)"]
            Retry["Spring Retry<br/>(Retry & Recovery)"]
            CacheAbs["@Cacheable<br/>(Cache Abstraction)"]
        end

        subgraph DataLayer["Data Layer"]
            JPA["Spring Data JPA<br/>(PostgreSQL)"]
            RedisData["Spring Data Redis<br/>(Cache Store)"]
        end

        subgraph Messaging["Messaging"]
            KafkaSpring["Spring Kafka<br/>(Producer / Consumer)"]
        end

        subgraph Observability["Observability"]
            Actuator["Spring Actuator<br/>(Health / Metrics)"]
            Micrometer["Micrometer<br/>(Metrics Export)"]
            Sleuth["Spring Cloud Sleuth<br/>(Distributed Tracing)"]
        end

        subgraph Config["Configuration"]
            Profiles["Spring Profiles<br/>(dev / staging / prod)"]
            CloudConfig["Spring Cloud Config<br/>(Externalized Config)"]
        end
    end

    MVC --> Security --> TX
    TX --> JPA & RedisData
    MVC --> Validation
    Service --> KafkaSpring
    Actuator --> Micrometer
    Profiles --> CloudConfig
```

##### 10. Local Development Architecture
```mermaid
flowchart TB
    subgraph IDE["Developer Workstation"]
        Code["Spring Boot App<br/>(IDE / Maven)"]
        DevProfile["spring.profiles.active=dev"]
    end

    subgraph DockerCompose["Docker Compose Stack"]
        Postgres["PostgreSQL<br/>:5432"]
        Redis["Redis<br/>:6379"]
        Kafka["Kafka + Zookeeper<br/>:9092"]
        Prometheus["Prometheus<br/>:9090"]
        Grafana["Grafana<br/>:3000"]
        Jaeger["Jaeger<br/>:16686"]
        Zipkin["Zipkin<br/>:9411"]
    end

    subgraph LocalAI["Local AI"]
        Ollama["Ollama<br/>(Mistral 7B Instruct)<br/>:11434"]
    end

    subgraph LocalAWS["AWS Simulation"]
        LocalStack["LocalStack<br/>(S3, Lambda, SQS, etc.)<br/>:4566"]
    end

    Code --> DevProfile
    Code --> Postgres & Redis & Kafka
    Code --> Ollama
    Code --> LocalStack
    Code --> Prometheus --> Grafana
    Code --> Jaeger & Zipkin
```

### Main objectives include:
#### Architecture and Design:
    - Showcase start to finish Architecture of Event Driven Architecture
#### AWS Cloud:
    - Leverage AWS Cloudwatch for monitoring and logging
    - Application Performance Monitoring (APM) with AWS X-Ray
    - Log aggregation and analysis with AWS CloudWatch Logs
    - Custom metrics and dashboards with AWS CloudWatch Metrics and Dashboards
    - SLOs and SLIs definition and monitoring with AWS CloudWatch SLOs
    - Implementing auto-scaling policies with AWS Auto Scaling
    - Disaster recovery planning and testing with AWS Backup and AWS Disaster Recovery
    - Deploy in AWS using AWS Elastic Beanstalk or AWS Fargate
    - Use GitHub Actions for CI/CD pipelines to automate testing and deployment
      - Create and Manage AWS resources using GitHub Actions
        - Use existing AWS VPC
        - Create AWS RDS instance for database needs
        - Create AWS S3 bucket for storage needs
        - Create AWS Lambda functions for serverless processing needs
        - Create AWS CloudWatch Alarms for monitoring needs
        - Create AWS IAM roles and policies for secure access management
        - Create AWS Elastic Beanstalk environment for application deployment
        - Create AWS Fargate tasks and services for containerized deployment
#### Azure Cloud:
     - Create and Manage Azure resources using GitHub Actions
    - Create and Manage GCP resources using GitHub Actions

### Claims Data integrates with AI models:
- Demonstrate integration of AI models for claims processing
- Implement SRE best practices for monitoring and reliability
  - Instrumentation with Prometheus and Grafana
  - Alerting with PagerDuty
  - Chaos engineering with Gremlin
  - Auto-scaling with Kubernetes Horizontal Pod Autoscaler (HPA)
  - Disaster recovery planning and testing
  - Incident management and post-mortem analysis
  - Service Level Objectives (SLOs) and Service Level Indicators (SLIs) definition and monitoring
  - Capacity planning and load testing
  - Blameless culture and continuous improvement practices
  - Documentation and knowledge sharing for SRE practices
  - Implementing a robust CI/CD pipeline for automated testing and deployment
  - Ensuring security best practices are followed in the development and deployment of the microservice, including vulnerability scanning and secure coding practices
  - 

## Repository Context

- **Parent project**: HealthCare-Plans-AI-Platform (multi-microservice architecture)
- **License**: Apache 2.0
- **Language expectation**: Java (based on .gitignore targeting .class, .jar, .war, .ear files)

## Build & Test Commands

No build system is configured yet. When one is added, update this section with:
- Build command (e.g., `mvn clean install` or `gradle build`)
- Run tests (e.g., `mvn test` or `gradle test`)
- Run single test (e.g., `mvn -pl module -Dtest=TestClass#method test`)
- Lint/format (e.g., `mvn checkstyle:check`)
- Run locally (e.g., `mvn spring-boot:run`)
