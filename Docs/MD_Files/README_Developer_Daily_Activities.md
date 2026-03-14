# Daily Development Guide

Practical workflows for day-to-day development on Claims-Processor-With-SRE. Assumes you have completed the first-time setup.

---

## Starting Your Day

Check what is already running before starting anything:

```bash
# Check microservice status
npm run services:status:all

# Check Docker infrastructure status
npm run devops:status:all
```

Start only what you need. There is no requirement to run all 9 services at once -- most development tasks only need 1-2 services.

---

## Common Workflows

### Working on a Single Service

Start just the service you are developing:

```bash
# Via npm (starts in background)
npm run services:start:api-claims

# Via Maven (starts in foreground with console output)
mvn spring-boot:run -pl API-Claims

# Via IntelliJ
# Run > Select "API-Claims [H2]" > Click Run or Debug
```

If your service depends on another (e.g., Claims depends on Tenants for tenant validation), start the dependency first:

```bash
npm run services:start:api-tenants
npm run services:start:api-claims
```

### Stopping Services

```bash
# Stop a single service
npm run services:stop:api-claims

# Stop all services
npm run services:stop:all
```

---

## Running Tests

### All Tests

```bash
# Run all tests across all modules
npm run test

# Or directly with Maven
mvn test
```

### Single Module

```bash
mvn -pl API-Claims test
```

### Single Test Class

```bash
mvn -pl API-Claims -Dtest=ApiClaimsApplicationTests test
```

### Single Test Method

```bash
mvn -pl API-Claims -Dtest=ApiClaimsApplicationTests#contextLoads test
```

### Skip Tests During Build

```bash
mvn clean install -DskipTests          # Skip test execution
mvn clean install -Dmaven.test.skip    # Skip test compilation and execution
```

---

## Testing APIs

### Swagger UI

Every service exposes Swagger UI for interactive API testing:

| Service | Swagger URL |
|---------|-------------|
| API-Gateway | http://localhost:8080/swagger-ui.html |
| API-Tenants | http://localhost:8081/swagger-ui.html |
| API-Plans | http://localhost:8082/swagger-ui.html |
| API-Claims | http://localhost:8083/swagger-ui.html |
| API-Members | http://localhost:8084/swagger-ui.html |
| API-Providers | http://localhost:8085/swagger-ui.html |
| API-Eligibility | http://localhost:8086/swagger-ui.html |
| API-Adjudication | http://localhost:8087/swagger-ui.html |
| API-Payments | http://localhost:8088/swagger-ui.html |

### OpenAPI Spec

Raw OpenAPI JSON for code generation or Postman import:

```
http://localhost:8083/v3/api-docs
```

### H2 Database Console

Available when running with H2 (default/local profile):

- URL: http://localhost:8083/h2-console
- JDBC URL: `jdbc:h2:mem:claimsdb`
- Username: `sa`
- Password: *(leave empty)*

Each service has its own H2 console on its respective port with its own database name.

### curl Examples

```bash
# Health check
curl -s http://localhost:8083/actuator/health | jq .

# List all claims
curl -s http://localhost:8083/api/v1/claims | jq .

# Create a claim
curl -X POST http://localhost:8083/api/v1/claims \
  -H "Content-Type: application/json" \
  -d '{"tenantId": "T001", "memberId": "M001", "amount": 500.00}' | jq .

# Get a specific claim (IDs are encrypted in responses)
curl -s http://localhost:8083/api/v1/claims/{encrypted-id} | jq .
```

---

## Checking Logs

### Console Output

- **Terminal**: Logs stream directly to stdout when running via `mvn spring-boot:run`
- **IntelliJ**: Logs appear in the Run tool window for the active run configuration
- **Background services**: Started via `npm run services:start:*` log to the console that started them

### Log Files

When running with the `dev` or `local` profile, structured JSON logs are written to:

```
./logs/<service-name>.json
```

### Kibana (Requires Docker Stack)

For centralized log exploration across all services:

1. Start the Docker stack: `npm run devops:start:all`
2. Open Kibana: http://localhost:5601/app/discover
3. Create an index pattern matching `claims-*` on first visit
4. Use KQL queries to filter:
   - `service.name: "api-claims"` -- logs from Claims service
   - `level: "ERROR"` -- all errors across services
   - `trace.id: "abc123"` -- follow a request across services

### Adjusting Log Levels at Runtime

Spring Boot Actuator allows changing log levels without restarting:

```bash
# Check current level
curl -s http://localhost:8083/actuator/loggers/com.claims | jq .

# Set to DEBUG
curl -X POST http://localhost:8083/actuator/loggers/com.claims \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'

# Reset to default
curl -X POST http://localhost:8083/actuator/loggers/com.claims \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": null}'
```

---

## Monitoring & Debugging

### Health Checks

```bash
# Basic health
curl -s http://localhost:8083/actuator/health | jq .

# Detailed health (shows db, disk, ping components)
curl -s http://localhost:8083/actuator/health | jq '.components'
```

### Metrics

```bash
# Prometheus format (for scraping)
curl -s http://localhost:8083/actuator/prometheus

# Spring Boot metrics list
curl -s http://localhost:8083/actuator/metrics | jq '.names[]'

# Specific metric
curl -s http://localhost:8083/actuator/metrics/jvm.memory.used | jq .
```

### Grafana Dashboards

URL: http://localhost:3000 (admin / claims_admin)

5 pre-provisioned dashboards are available:
- JVM metrics (heap, GC, threads)
- HTTP request rates and latencies
- Database connection pool stats
- Service-level SLI/SLO tracking
- Infrastructure overview

### Jaeger Distributed Tracing

URL: http://localhost:16686

Use Jaeger to trace requests across microservices:
1. Select a service from the dropdown
2. Click "Find Traces"
3. Click a trace to see the full call chain with timing

### Prometheus

URL: http://localhost:9090

- **Targets**: http://localhost:9090/targets -- verify all services are being scraped
- **Query**: Use PromQL to explore metrics, e.g., `rate(http_server_requests_seconds_count[5m])`

---

## Working with Profiles

### Available Profile Combinations

```bash
# H2 in-memory (default, no Docker needed)
mvn spring-boot:run -pl API-Claims

# Local with explicit H2
mvn spring-boot:run -pl API-Claims -Dspring-boot.run.profiles=local

# Local with PostgreSQL (requires Docker PostgreSQL)
mvn spring-boot:run -pl API-Claims -Dspring-boot.run.profiles=local,local-postgres

# Dev with AWS service stubs
mvn spring-boot:run -pl API-Claims -Dspring-boot.run.profiles=dev,aws

# Dev with Azure service stubs
mvn spring-boot:run -pl API-Claims -Dspring-boot.run.profiles=dev,azure

# Dev with GCP service stubs
mvn spring-boot:run -pl API-Claims -Dspring-boot.run.profiles=dev,gcp
```

### Profile Behavior Summary

| Profile | Database | Log Level | Feature Toggles | Use Case |
|---------|----------|-----------|-----------------|----------|
| *(default)* | H2 | INFO | Disabled | Quick start, demos |
| `local` | H2 | DEBUG | Disabled | Daily development |
| `local,local-postgres` | PostgreSQL | DEBUG | Disabled | Testing with real DB |
| `dev,aws` | PostgreSQL | DEBUG | Enabled | AWS integration dev |
| `dev,azure` | PostgreSQL | DEBUG | Enabled | Azure integration dev |
| `dev,gcp` | PostgreSQL | DEBUG | Enabled | GCP integration dev |

---

## Docker Operations

### Full Stack

```bash
npm run devops:start:all      # Start all infrastructure containers
npm run devops:stop:all       # Stop all infrastructure containers
npm run devops:status:all     # Check status of all containers
```

### Individual Components

```bash
npm run devops:start:postgres    # Just PostgreSQL
npm run devops:stop:postgres     # Stop PostgreSQL

npm run devops:start:grafana     # Grafana + Prometheus
npm run devops:stop:grafana      # Stop Grafana + Prometheus

npm run devops:start:elk         # Elasticsearch + Logstash + Kibana
npm run devops:stop:elk          # Stop ELK stack

npm run devops:start:kafka       # Kafka + Zookeeper
npm run devops:stop:kafka        # Stop Kafka

npm run devops:start:jaeger      # Jaeger tracing
npm run devops:stop:jaeger       # Stop Jaeger
```

### Rebuilding Docker Images

```bash
# Rebuild a single service image
docker build -t claims-processor/api-claims:latest -f API-Claims/Dockerfile .

# Rebuild all images
npm run docker:build:all
```

---

## Building

```bash
npm run build         # Build all modules, skip tests
npm run build:tests   # Build all modules with tests
npm run clean         # Clean all target directories
```

For a single module:

```bash
mvn clean package -pl API-Claims -DskipTests
```

---

## End of Day

```bash
# Stop all running services
npm run services:stop:all

# Optionally stop Docker infrastructure to free resources
npm run devops:stop:all
```

---

## Tips and Tricks

- **Fastest startup**: Use `[H2]` IntelliJ run configurations. H2 mode has zero external dependencies and starts in under 5 seconds.

- **Feature toggles are disabled by default**: In `local` and default profiles, all feature toggles (search, event streaming, caching, log shipping) are disabled. This means services run without needing Kafka, Redis, Elasticsearch, or any cloud services. Safe to run anytime.

- **Seed data**: The API-Tenants service auto-creates 100 sample tenants on startup. Other services create their own seed data as well. No manual data setup needed.

- **Encrypted IDs**: All entity IDs in API responses are encrypted. In `local` and `dev` profiles, NoOp encryption is used (IDs pass through unchanged). In upper environments, AES-256 encryption is applied.

- **Hot reload**: Spring Boot DevTools is included. When running from IntelliJ, code changes trigger automatic restart. For Maven, add `-Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"`.

- **Parallel builds**: Speed up Maven builds with parallel execution:
  ```bash
  mvn clean install -DskipTests -T 1C    # 1 thread per CPU core
  ```

- **Offline builds**: After the first successful build, work offline to avoid dependency checks:
  ```bash
  mvn clean install -DskipTests -o
  ```
