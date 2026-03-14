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

##### Technical / AWS Architecture View
```mermaid
flowchart TB
    subgraph Presentation["Presentation Layer"]
        CP["Customer Portal<br/>(Angular)"]
        AP["Admin Portal<br/>(Angular)"]
    end

    subgraph AWSAPI["AWS API & Security Layer"]
        APIGW["AWS API Gateway"]
        IAM["AWS IAM<br/>(Roles & Policies)"]
    end

    subgraph Application["Application Layer"]
        subgraph SpringBoot["Spring Boot Microservice"]
            ClaimsAPI["Claims REST API"]
            ClaimsProcessor["Claims Processing<br/>Engine"]
            AIService["AI Integration<br/>Service"]
        end
        Kafka["Apache Kafka<br/>(Event Bus)"]
    end

    subgraph AILayer["AI / ML Layer"]
        Lambda["AWS Lambda<br/>(Serverless AI Processing)"]
        Ollama["Ollama - Mistral 7B<br/>(Local Dev Inference)"]
    end

    subgraph AWSData["AWS Data & Storage Layer"]
        S3["AWS S3<br/>(Claim Documents)"]
        RDS["AWS RDS PostgreSQL<br/>(Claims Data)"]
        Redis["Redis<br/>(Session & Cache)"]
    end

    subgraph AWSObservability["AWS Observability & SRE Layer"]
        subgraph CloudWatch["AWS CloudWatch"]
            CWLogs["CloudWatch Logs"]
            CWMetrics["CloudWatch Metrics<br/>& Dashboards"]
            CWSLOs["CloudWatch Application<br/>Signals & SLOs"]
        end
        XRay["AWS X-Ray<br/>(Distributed Tracing)"]
        subgraph OpenSource["Open Source Observability"]
            Prom["Prometheus"] --> Graf["Grafana"]
            Jaeger["Jaeger / Zipkin"]
        end
        PD["PagerDuty<br/>(Alerting & Incidents)"]
    end

    subgraph AWSDeployment["AWS Deployment & CI/CD"]
        VPC["AWS VPC"]
        subgraph Compute["Compute Options"]
            EB["AWS Elastic Beanstalk"]
            Fargate["AWS Fargate"]
        end
        GHA["GitHub Actions<br/>(CI/CD Pipeline)"]
        Backup["AWS Backup &<br/>Disaster Recovery"]
    end

    subgraph LocalDev["Local Development"]
        Docker["Docker Desktop"]
        LocalStack["LocalStack<br/>(AWS Simulation)"]
        K8s["Kubernetes<br/>(On-Prem)"]
    end

    CP & AP --> APIGW
    APIGW --> IAM --> ClaimsAPI
    ClaimsAPI --> ClaimsProcessor
    ClaimsProcessor --> AIService
    ClaimsProcessor <--> Kafka
    AIService --> Lambda
    AIService -.->|"Local only"| Ollama
    ClaimsProcessor --> S3
    ClaimsProcessor --> RDS
    ClaimsProcessor --> Redis
    SpringBoot --> CWLogs & CWMetrics & CWSLOs
    SpringBoot --> XRay
    SpringBoot --> Prom
    SpringBoot --> Jaeger
    CWSLOs --> PD
    GHA --> Compute
    VPC --> Compute
    GHA -.->|"Local"| Docker & LocalStack & K8s
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
