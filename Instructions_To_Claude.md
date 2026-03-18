# Instructions To Claude

This file contains only the instructions given by the user during the initial project setup conversations.

---

## Instruction 1: Initialize CLAUDE.md
Analyze this codebase and create a CLAUDE.md file, which will be given to future instances of Claude Code to operate in this repository.

## Instruction 2: Project Overview (User-Provided Content)
Claims-Processor-With-SRE is a microservice within the HealthCare-Plans-AI-Platform.

On March 13th 2026 in AWS News website, AWS mentioned "Amazon CloudWatch Application Signals adds new SLO capabilities"
https://aws.amazon.com/about-aws/whats-new/2026/03/cloudwatch-application-signals-adds-slo-capabilities/

This project is designed to showcase the above new capabilities of AWS CloudWatch Application Signals for monitoring and managing Service Level Objectives (SLOs) in a healthcare claims processing microservice. The project will demonstrate how to define and monitor SLOs using AWS CloudWatch, and how to use these insights to improve the reliability and performance of the claims processing service.

- This project is designed to intake healthcare claims data, process it using AI models to identify patterns and anomalies, and provide insights for improving claim processing efficiency and accuracy.
- The service will also incorporate Site Reliability Engineering (SRE) principles to ensure high availability and performance.
- This project is designed to be deployed in
  - AWS
  - Azure
  - GCP
  - On-premises environments (Local with Docker Desktop, Kubernetes)
- Claims Support intake a claim screenshot or PDF, uploads it to the system, and the system processes the claim using AI models to extract relevant information,
- Extract information is useful to identify patterns, and provide insights for improving claim processing efficiency and accuracy.
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

## Instruction 3: Architecture Diagram
Can you draw architecture diagram neat one and update in the Architecture Diagram section.

## Instruction 4: Commit and Push
Commit and push the code.

## Instruction 5: Architecture Diagram Redesign
Technical / AWS Architecture View too many components and hard to view or study. Normalize based on functional, core architectural components, Event Driven Architecture, Serverless Architecture Usage, Deployment Architecture, AI integration architecture, Cloud Deployment Architecture with native services, SRE focused Architecture, DevOps focused Architecture (CI/CD practices and tools), Performance and Scalability Architecture, Spring Boot and Spring framework modules usage stack and more.

## Instruction 6: Commit and Push
Commit and push.

## Instruction 7: Claim Intake Processing Workflow
Soon after "Business / Functional View" can you add how a claim when uploaded what all happen from the point the claim image is uploaded or claim pdf or claim document or claim file is uploaded. Maybe we should mention in a typical claims processing business what stages in a dropdown a claims support staff who are intaking the claims artifact (multi-file format support) select the stage in the dropdown and then proceeds in the backend and shows up in the UI the extract information for the customer. Claims are always uploaded for an existing customer only.

## Instruction 8: AWS Bedrock Data Automation & Detailed Architecture
We are going to leverage AWS Bedrock Amazon Bedrock Data Automation — Automate the generation of useful insights from unstructured multimodal content such as documents, images, audio, and video for your AI-powered applications. The word document in this folder have an understanding. For copyright issues perspective don't copy the architecture as it is. Now create another section with Detailed Architecture heading and have new thinking how we should implement this Claims Intake Processing Start to Finish based on whatever we discussed until now.

## Instruction 9: Maven Multi-Module Project Structure
We will have parent pom.xml at root of the repo and Portal-Claims-Advisor a spring boot app with angular code and spring delivers the UI also along with backend logic and Portal-Claims-Member (member=customer here) another spring boot with angular code and spring boot delivers the UI also along with backend logic. API-Claims for claims CRUD and search. API-Members for customer/member crud and search and lookup. Portal-Batch-Assignment with spring boot delivering the UI for batch process assignments includes creating the customer in a batch in the UI or through an excel import. Can you create another file Instructions_To_Claude.md and mention all instructions I gave to you until now don't add anything else this file should have only my instructions. Now can you create above Maven projects and Angular projects using Spring Boot 3.5.6 and JDK 17. We will also have another folder DevOps/Local/docker-all-up.sh, docker-all-down.sh, docker-all-status.sh with folders as Postgres/docker-compose.yaml, Redis/docker-compose.yaml, Kafka/docker-compose.yaml, Observability/Prometheus/docker-compose.yaml, Observability/Grafana/docker-compose.yaml, Search/Elastic/docker-compose.yaml, like that Jaeger, Wiremock/docker-compose.yaml etc.

## Instruction 10: H2 Database, IntelliJ Run Configs, Root package.json
Use h2 database too just in case we don't start docker as default database. Have a profile local-postgres and create IntelliJ run configurations file in .run folder multiple ones one with h2 and the other with postgres for each API's services and Portal applications. Also create a package.json file at the root of the project and this is for developer operations only not for typical typescript projects. In this package.json have commands to start/stop/status each service independently and also all together on different port numbers. Also have commands to start stop status of docker-all-up.sh down.sh status.sh.

## Instruction 11: Fix Maven Parent POM Resolution Error
Getting error "Could not find artifact org.springframework.boot:spring-boot-starter-validation:jar:0.1.0-SNAPSHOT". The parent pom.xml had Spring Boot starters with `${project.parent.version}` which resolved to `0.1.0-SNAPSHOT` instead of the Spring Boot version. Spring Boot starter versions should be inherited from `spring-boot-starter-parent`, not declared explicitly in dependencyManagement.

## Instruction 12: .gitignore and SRE Observability Stack
Did we add node_modules to the gitignore and also I don't see any default dashboards or visualizations or alerts upfront created when I start the docker containers locally have you done it? I need them for each aspect of this whole codebase as per SRE industry practices includes SLI SLO SLA Alerts Dashboards Metrics.

## Instruction 13: Rename npm Scripts with Prefixes
npm run start:all confusing we need to differentiate between starting DevOps folder contents and services. I feel npm run devops:start:all that devops prefix will help and same with services:start:all prefixes and related prefixes fix or improve them.

## Instruction 14: Developer Portal index.html
Also create index.html page at the root of this repo that will have links to everything that can be accessible for this application like UIs, API swaggers, DevOps tools accessible via http etc.

## Instruction 15: Multi-Cloud AI — Not Pure AWS Bedrock
Don't make this codebase pure AWS Bedrock based. I see that you added it in the main line of index.html. We might use GCP Vertex or Azure AI too.

## Instruction 16: OpenTelemetry, Micrometer, Actuator, Circuit Breaker, Bulkhead
Did we add OpenTelemetry maven dependencies and also for Angular whatever library is used in the industry and also Micrometer, not sure if Micrometer is there good enough and no need to add OpenTelemetry, and also Actuator maven dependency, circuit breaker, Bulkhead library for Spring Boot or Java.

## Instruction 17: Professional index.html Redesign
I don't like black color layout. Please give professional touch with appropriate fav icons or images or what is the best professionally.

## Instruction 18: Keep Instructions_To_Claude.md Updated
Are you sure you are updating Instructions_To_Claude.md file from the last you updated? We discussed so many things since then.

## Instruction 19: Docker Container Naming and Network
Can you ensure all docker containers prefixed with Claims-Processor- and also use name: in docker-compose.yaml. Have a dedicated network claims-processor-network with bridge driver. Check for network existence while starting docker-all-up.sh.

## Instruction 20: Zookeeper Health Check Fix
Zookeeper health check failing — nc command not available in Confluent image. Use cub zk-ready instead.

## Instruction 21: Docker Project Name Lowercase
Docker project name must be lowercase — Claims-Processor-DevOps changed to claims-processor-devops.

## Instruction 22: Maven Version Resolution Fix
micrometer-tracing-bridge-otel and opentelemetry-exporter-otlp missing versions. Added explicit versions in parent pom dependencyManagement.

## Instruction 23: Grafana Credentials and Auto-Login
Set passwords automatically for all docker containers to reduce effort on purge and restart. Grafana password set to claims_admin with anonymous viewer access. Redis password set to claims_redis. Update index.html with all credentials.

## Instruction 24: Prometheus Alert Rules Not Mounted
Alert rules and recording rules YAML files were not mounted as volumes in the Prometheus docker-compose. Fixed by adding volume mounts and using absolute paths in prometheus.yml.

## Instruction 25: Grafana Alert Rules
Need Grafana-native alert rules provisioned on startup. Created 14 unified alerting rules across SLO, Application Health, Claims Processing, and Infrastructure groups with contact points and notification policies.

## Instruction 26: Kibana + Feature Toggles for Search, Events, and Multi-Cloud
Add Kibana for logs and application search indexes. Support feature toggles for: search indexing (Elasticsearch, AWS OpenSearch, Azure Cognitive Search, GCP Search), event streaming (Kafka, Azure EventHub, AWS Kinesis, GCP Pub/Sub), log shipping, Redis caching, and metrics export. All integrations should be feature-toggled.

## Instruction 27: Multi-Environment and Multi-Cloud Profiles
Have -aws, -azure, -gcp, -local profiles. Need local, dev, test, staging, pre-prod, prod environments. Non-local environments derive values from Kubernetes ConfigMaps, Docker env vars, or key vault/vault for secrets (AWS Secrets Manager, Azure Key Vault, GCP Secret Manager, HashiCorp Vault).

## Instruction 28: Elasticsearch + Kibana Integration
Integrate Kibana with actual data — log shipping via Filebeat, Elasticsearch index templates for claims/members/app-logs, Kibana data views auto-configured, logback-spring.xml with JSON structured logging.

## Instruction 29: Keep Everything Updated
Ensure feature toggles for indexing/searching, Kafka publishing, Redis, log shipping to Prometheus/Grafana/Kibana. Keep index.html updated with all tech stack. Keep architecture sections in .md files updated.

## Instruction 30: HikariCP, Bulkhead, Circuit Breaker, Inter-Service Auth, Auth Error Metrics
Are we using HikariCP connection pool and also Bulkhead design pattern and also Circuit Breaker for inter-service communication? Based on feature toggles (default false) are we using JWT token or basic authentication or AWS Cognito — all configured through feature toggles for inter-service communication authentication. Also are we logging in dashboards or alerts metrics if certain % of authentication errors received by the target API in observability tools?

## Instruction 31: Common-Libraries with Common-Utils (Encryption, DTOs)
Create Common-Libraries parent module with Common-Utils child. Utility class for encrypt/decrypt tenant id, customer id, claim ids using respective encryption keys. Ensure Common-Utils is used by every other Maven module. Enhance APIs to accept encrypted IDs and use DTO classes (BaseReqDTO, domain-specific ReqDTO, RespDTO with ApiResponse wrapper) consistently from controller through service layer.

## Instruction 32: README.md and CLAUDE.md Architecture Description
In README.md or CLAUDE.md while describing the architecture, describe in sequence of Business Architecture, Data Architecture, Application Architecture, Technology Architecture (BDAT). Don't write about folder structure or Java code — make it descriptive with diagrams. Create separate files: README_Developer_First_Time.md, README_Developer_Daily_Activities.md, README_Developer_Deployment_Technical.md in Docs/MD_Files/.

## Instruction 33: Portal Modules Are Orchestrators
All API- modules are core business services that do CRUD and lookups. Portal- modules act as orchestrators across the core capabilities services in API- modules. Portal-Claims-Advisor orchestrates API-Claims, API-Members, API-Tenants, API-Entitlements.

## Instruction 34: Feign Client + Shared Common Domain Modules
Use FeignClient library for inter-microservice communication. Create Common-Claims, Common-Members, Common-Tenants, Common-Entitlements, Common-Apps as Maven modules under Common-Libraries. Define ReqDTO and RespDTO classes shared across the codebase. Don't share JPA Entity classes. Can have model classes as proxy to real JPA entities. Add these common-domain dependencies in Portal- and API- modules.

## Instruction 35: Swagger in All Services, /actuator/info Links, Portal-SRE
Swagger in all services. Add /actuator/info and /actuator/health links in index.html. Create Portal-SRE for SRE-related views — tenant analytics, cloud resource health, deployment tracking, incident management, per-tenant SLO compliance. Don't duplicate what Grafana/Prometheus/Jaeger/Kibana already do. Focus on business-level SRE: tenant usage, cost tracking, service health aggregation, MTTR.

## Instruction 36: CQRS for Spring Boot and Angular
Enabled CQRS for Spring Boot (separate command/query controllers in API-Tenants and API-Entitlements).

## Instruction 37: Multi-Tenant Entitlements Model
API-Entitlements for employees/contractors/partners. Integrate with Active Directory in future (feature toggled). Manages Roles, Privileges, Groups, GroupRoles, GroupRolePrivileges, User, UserGroups, UserRolePrivileges. All objects have tenant_id. API-Tenants for tenant CRUD. Portal-Tenants and Portal-Entitlements with Angular UI. Create 100 initial seed tenants with fictional names.

## Instruction 38: Commit and Push All Code
Commit and push all code to git.

## Instruction 39: P1-P11 Priority Task Execution
Worked through README_PENDING_TASKS.md priorities P1 (fix & stabilize) through P11 (documentation). 71 of 101 tasks completed. Remaining 30 tasks are deferred (Testcontainers, Angular E2E, performance testing, CI/CD, cloud deployments, ADRs, runbooks).

## Instruction 40: GitHub Actions for AWS Deployment
For now we will do only AWS setup later we will do Azure and GCP. Create GitHub Actions workflow for AWS deployment with stages: build, test, package, deploy to Elastic Beanstalk or Fargate. 
Use AWS CLI or GitHub Actions AWS toolkit for deployment steps. 
Ensure secrets are stored securely in GitHub Secrets. 
Add manual approval step before production deployment.

None of the Github workflow actions are automatic and all are manually triggered.

## Instruction 41: CloudWatch Application Signals — Phase 0 Implementation
Implement Phase 0 prerequisites for CloudWatch Application Signals SLO blog post (2026-03-13). All changes must be feature-toggled via `EnableApplicationSignals` CloudFormation parameter — not hardcoded. Dockerfiles must be reusable without signals (ADOT agent inert unless JAVA_TOOL_OPTIONS env var set). Create new `application-aws-signals.yml` Spring profile (not modify existing profiles) with granularity for dev through prod environments. Create standalone `AWS_100_Blog_SLO_20260313.yml` workflow with human-in-the-loop approval — NOT included in AWS_99. AWS_98 must detect and disable blog feature toggles before destroying stacks, using a dynamic FEATURE_TOGGLES array extensible for future blog implementations. Keep blog tasks in separate `README_AWS_BLOG_20260313_SLO.md` — do not mix with `README_PENDING_TASKS.md`.

AWS GitHub Actions Design:
1. We will have multiple workflows for different environments: dev, staging, prod. 
   1. Each workflow will have the same stages but different deployment targets and configurations.
   2. Show deployment environments in a dropdown of Github Actions UI for manual trigger.
2. Prefix all workflows with AWS_01 etc. for sequencing
3. We will have so man Github actions for AWS deployment.
   1. By default, we will use the existing VPC_ID and its subnets and security groups for deployment but we can have option to override them through workflow dispatch inputs.
   2. The existing VPC_ID info is stored in Github secrets only and not in index.html page.
   3. We will have different workflows based on tech stack
      1. We will have different cloud formation tempaltes
      2. VPC and Security Groups template if we are creating new. If we are using existing then we will skip this step in the workflow and use the existing VPC_ID and security groups.
      3. AWS CloudWatch, AWS Cloudtrail, AWS Cognito Pool, RDS Postgres DB, AWS S3 Bucket, AWS EKS, AWS ECR for Docker images, AWS OpenSearch, AWS Kinesis
      4. Infrastructure AWs API Gateway, AWS Fargate, AWS ECR
      5. AWS Secrets Manager for managing the secrets in non-local environments and also for inter-service communication authentication secrets management.
      6. A GitHub action to populate initial data seed in RDS Postgres DB after deployment using AWS Lambda or AWS SSM Run Command or AWS CloudFormation custom resource.
      7. 
      
   4. WE need to have another GitHub action that destorys eveything. 
   5. I am thinking we will have Cloudformation template to make the job easy both for creation of the above resources and destroying the above and also obtaining the status
   5. Not sure what I am missing here
            

