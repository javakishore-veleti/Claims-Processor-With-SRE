# First Time Developer Setup

This guide walks you through setting up the Claims-Processor-With-SRE project from scratch on your local machine.

---

## Prerequisites

Install the following before proceeding:

| Tool | Version | Notes |
|------|---------|-------|
| JDK | 17 (AdoptOpenJDK / Temurin) | Required for all Spring Boot services |
| Maven | 3.9+ | Multi-module build orchestration |
| Node.js | 20+ | npm scripts for service orchestration |
| npm | Bundled with Node.js | Task runner for dev workflows |
| Docker Desktop | Latest stable | Required only for full-stack mode |
| IntelliJ IDEA | 2023.3+ (recommended) | 54 pre-configured run configs included |
| VS Code | Latest | Alternative IDE with Java Extension Pack |
| Git | 2.40+ | Source control |

Verify your installations:

```bash
java -version        # Should show 17.x
mvn -version         # Should show 3.9.x+
node -version        # Should show v20.x+
npm -version         # Should show 10.x+
docker --version     # Should show 24.x+ (only needed for full-stack mode)
git --version        # Should show 2.40+
```

---

## Clone & Build

```bash
git clone https://github.com/javakishore-veleti/Claims-Processor-With-SRE.git
cd Claims-Processor-With-SRE
mvn clean install -DskipTests
```

The first build downloads all Maven dependencies and the frontend-maven-plugin automatically downloads the correct Node.js version for the Angular frontend build. This may take 5-10 minutes on a fresh setup.

---

## Quick Start (Zero Dependencies -- H2 Mode)

This is the fastest way to get running. No Docker, no PostgreSQL, no external services needed.

```bash
npm run services:start:all
```

This starts all 9 services using H2 in-memory databases. Each service runs independently with seed data auto-loaded on startup.

Open `index.html` in your browser to access the Developer Portal with links to all running services.

---

## Full Stack Start (Docker + PostgreSQL)

For a production-like local environment with PostgreSQL, Grafana, Prometheus, Kibana, and Jaeger:

```bash
# Step 1: Start infrastructure containers
npm run devops:start:all

# Step 2: Start services with PostgreSQL profiles
npm run services:start:all:postgres
```

This brings up the complete observability stack alongside all microservices backed by PostgreSQL.

---

## IntelliJ IDEA Setup

1. **Open the project**: File > Open > select the root `Claims-Processor-With-SRE` folder. IntelliJ will detect it as a Maven multi-module project.

2. **Wait for indexing**: IntelliJ will import all Maven modules and index the codebase. This takes 2-3 minutes on first open.

3. **Run configurations are pre-loaded**: IntelliJ automatically detects the `.run/` folder containing 54 pre-configured run configurations. These appear in the Run/Debug dropdown immediately.

4. **Choose a configuration**:
   - **`[H2]` configurations** -- Start a service with H2 in-memory database. Zero setup, instant startup. Best for quick testing.
   - **`[Local]` configurations** -- Start a service with H2 database and DEBUG-level logging. Best for day-to-day development and debugging.
   - **`[Postgres]` configurations** -- Start a service with local PostgreSQL. Requires Docker stack running.

5. **Run or Debug**: Select any configuration and press the Run or Debug button. No additional VM options or environment variables needed.

---

## Verify Everything Works

After starting services, verify the setup:

### Service Health

```bash
# Check all service statuses
npm run services:status:all

# Check Docker infrastructure status (if running full stack)
npm run devops:status:all
```

### Key URLs

| URL | Purpose |
|-----|---------|
| `index.html` (local file) | Developer Portal -- links to all services |
| http://localhost:8083/swagger-ui.html | Claims API -- Swagger UI |
| http://localhost:8083/actuator/health | Claims API -- Health check |
| http://localhost:8081/swagger-ui.html | Tenants API -- Swagger UI |
| http://localhost:3000 | Grafana dashboards (admin / claims_admin) |
| http://localhost:5601 | Kibana log explorer |
| http://localhost:9090 | Prometheus targets and queries |
| http://localhost:16686 | Jaeger distributed tracing |

### Quick Smoke Test

```bash
# Check Claims API health
curl http://localhost:8083/actuator/health

# List claims (should return seed data)
curl http://localhost:8083/api/v1/claims
```

---

## Project Structure Overview

```
Claims-Processor-With-SRE/
├── API-Claims/                  # Claims CRUD microservice (port 8083)
├── API-Tenants/                 # Multi-tenant management (port 8081)
├── API-Plans/                   # Insurance plans service (port 8082)
├── API-Members/                 # Member management (port 8084)
├── API-Providers/               # Provider network (port 8085)
├── API-Eligibility/             # Eligibility verification (port 8086)
├── API-Adjudication/            # Claims adjudication engine (port 8087)
├── API-Payments/                # Payment processing (port 8088)
├── API-Gateway/                 # API Gateway / BFF (port 8080)
├── Shared-DTO/                  # Shared data transfer objects
├── Shared-Utils/                # Common utilities and base classes
├── DevOps-Docker/               # Docker Compose files and configs
├── DevOps-Grafana/              # Grafana dashboards and provisioning
├── DevOps-Prometheus/           # Prometheus configs and alert rules
├── .run/                        # 54 IntelliJ run configurations
├── index.html                   # Developer Portal
├── package.json                 # npm task runner scripts
├── pom.xml                      # Root Maven POM
├── CLAUDE.md                    # Detailed architecture and diagrams
└── README.md                    # Project overview and architecture
```

For detailed architecture, see `README.md` in the project root.
For architecture diagrams and AI-assisted development context, see `CLAUDE.md`.

---

## Local Credentials

All credentials below are for local development only. Production credentials are managed via Kubernetes Secrets or cloud secret managers.

| Service | Username | Password | URL | Notes |
|---------|----------|----------|-----|-------|
| PostgreSQL | `claims_user` | `claims_pass` | `localhost:5432` | Database: `claimsdb` |
| H2 Console | `sa` | *(empty)* | http://localhost:8083/h2-console | JDBC URL: `jdbc:h2:mem:claimsdb` |
| Grafana | `admin` | `claims_admin` | http://localhost:3000 | 5 pre-built dashboards |
| Kibana | *(none)* | *(none)* | http://localhost:5601 | No auth in local mode |
| Prometheus | *(none)* | *(none)* | http://localhost:9090 | No auth in local mode |
| Jaeger | *(none)* | *(none)* | http://localhost:16686 | No auth in local mode |
| Elasticsearch | `elastic` | `changeme` | http://localhost:9200 | Used by Kibana |
| Redis | *(none)* | *(none)* | `localhost:6379` | No auth in local mode |
| Kafka | *(none)* | *(none)* | `localhost:9092` | No auth in local mode |

---

## Common First-Time Issues

### Maven dependency resolution failures

**Symptom**: `Could not resolve dependencies` or `Non-resolvable parent POM` errors when building a single module.

**Fix**: Always run the full build from the project root first:

```bash
mvn clean install -DskipTests
```

This installs `Shared-DTO` and `Shared-Utils` into your local Maven repository, which individual modules depend on.

### Port conflicts

**Symptom**: `Address already in use` or `Port 8081 is already in use`.

**Fix**: Check which process is using the port and stop it:

```bash
lsof -i:8081
# Kill the process if needed
kill -9 <PID>
```

Common port assignments: 8080 (Gateway), 8081 (Tenants), 8082 (Plans), 8083 (Claims), 8084-8088 (other services), 3000 (Grafana), 5432 (PostgreSQL), 5601 (Kibana), 9090 (Prometheus), 9092 (Kafka), 16686 (Jaeger).

### Docker network errors

**Symptom**: `Network claims-processor-network not found` or containers cannot communicate.

**Fix**: The Docker network is created automatically by the startup script. If it is missing, create it manually:

```bash
docker network create claims-processor-network
```

Or use the npm script which handles this automatically:

```bash
npm run devops:start:all
```

### Angular frontend build failures during Maven build

**Symptom**: Node.js or npm errors during `mvn clean install`.

**Fix**: The `frontend-maven-plugin` downloads its own Node.js and npm. If it fails, check your network connection and proxy settings. The plugin does not use your system-installed Node.js. If you are behind a corporate proxy, configure Maven proxy settings in `~/.m2/settings.xml`.

### H2 Console not accessible

**Symptom**: Cannot reach H2 Console at http://localhost:8083/h2-console.

**Fix**: H2 Console is only available when running with the `local` or default (H2) profile. Verify the service is running and use these connection settings:
- JDBC URL: `jdbc:h2:mem:claimsdb`
- Username: `sa`
- Password: *(leave empty)*

### IntelliJ run configurations not visible

**Symptom**: The `.run/` configurations do not appear in the Run dropdown.

**Fix**: Ensure you opened the project root folder (not a subfolder). Go to File > Open and select the `Claims-Processor-With-SRE` directory. IntelliJ auto-detects `.run/*.xml` files on project open.
