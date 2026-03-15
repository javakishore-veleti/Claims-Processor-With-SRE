#!/usr/bin/env bash
###############################################################################
# docker-all-up.sh
# Creates the shared Docker network (if needed) and starts ALL local Docker
# Compose services in dependency order.
#
# Optional services controlled by environment variables (default: false):
#   CLAIMS_PROCESSOR_APP_LOCAL_REDIS=true
#   CLAIMS_PROCESSOR_APP_LOCAL_ELASTICSEARCH=true
#   CLAIMS_PROCESSOR_APP_LOCAL_KIBANA=true
#   CLAIMS_PROCESSOR_APP_LOCAL_FILEBEAT=true
#   CLAIMS_PROCESSOR_APP_LOCAL_WIREMOCK=true
#   CLAIMS_PROCESSOR_APP_LOCAL_OLLAMA=true
###############################################################################
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="claims-processor-devops"
NETWORK_NAME="claims-processor-network"

# ── Optional service flags (default: false unless set) ─────────────────────
ENABLE_REDIS="${CLAIMS_PROCESSOR_APP_LOCAL_REDIS:-false}"
ENABLE_ELASTICSEARCH="${CLAIMS_PROCESSOR_APP_LOCAL_ELASTICSEARCH:-false}"
ENABLE_KIBANA="${CLAIMS_PROCESSOR_APP_LOCAL_KIBANA:-false}"
ENABLE_FILEBEAT="${CLAIMS_PROCESSOR_APP_LOCAL_FILEBEAT:-false}"
ENABLE_WIREMOCK="${CLAIMS_PROCESSOR_APP_LOCAL_WIREMOCK:-false}"
ENABLE_OLLAMA="${CLAIMS_PROCESSOR_APP_LOCAL_OLLAMA:-false}"

# ── Docker Network ──────────────────────────────────────────────────────────
if ! docker network inspect "$NETWORK_NAME" >/dev/null 2>&1; then
    echo "Creating Docker network: $NETWORK_NAME"
    docker network create --driver bridge "$NETWORK_NAME"
else
    echo "Docker network '$NETWORK_NAME' already exists"
fi

# ── Build ordered service list based on env vars ────────────────────────────
SERVICES=("Postgres")

[[ "$ENABLE_REDIS" == "true" ]] && SERVICES+=("Redis")

SERVICES+=("Kafka")

if [[ "$ENABLE_ELASTICSEARCH" == "true" ]]; then
    SERVICES+=("Search/Elastic")
    [[ "$ENABLE_KIBANA" == "true" ]] && SERVICES+=("Observability/Kibana")
    [[ "$ENABLE_FILEBEAT" == "true" ]] && SERVICES+=("Search/Filebeat")
fi

SERVICES+=(
    "Observability/Prometheus"
    "Observability/Alertmanager"
    "Observability/Grafana"
    "Tracing/Jaeger"
    "Tracing/Zipkin"
)

[[ "$ENABLE_WIREMOCK" == "true" ]] && SERVICES+=("Wiremock")
[[ "$ENABLE_OLLAMA" == "true" ]] && SERVICES+=("Ollama")

echo ""
echo "=========================================="
echo "  Starting ALL local Docker Compose services"
echo "  Project: $PROJECT_NAME"
echo "  Network: $NETWORK_NAME"
echo "=========================================="
echo "  Optional: Redis=$ENABLE_REDIS, ES=$ENABLE_ELASTICSEARCH, Kibana=$ENABLE_KIBANA"
echo "            Filebeat=$ENABLE_FILEBEAT, WireMock=$ENABLE_WIREMOCK, Ollama=$ENABLE_OLLAMA"
echo "=========================================="

for svc in "${SERVICES[@]}"; do
  compose_file="${SCRIPT_DIR}/${svc}/docker-compose.yaml"

  if [[ ! -f "${compose_file}" ]]; then
    echo "[WARN]  Compose file not found: ${compose_file} — skipping"
    continue
  fi

  echo ""
  echo "---------- Starting: ${svc} ----------"
  docker compose -p "$PROJECT_NAME" -f "${compose_file}" up -d
  echo "[OK]    ${svc} is up"

  # After Elasticsearch starts: initialize index templates and indices
  if [[ "${svc}" == "Search/Elastic" ]]; then
    echo "[INIT]  Running Elasticsearch index template initialization..."
    bash "${SCRIPT_DIR}/Search/Elastic/init-indices.sh" || echo "[WARN]  ES init-indices.sh failed (ES may not be ready yet)"
  fi

  # After Kibana starts: initialize data views in the background
  if [[ "${svc}" == "Observability/Kibana" ]]; then
    echo "[INIT]  Running Kibana data view initialization in background..."
    bash "${SCRIPT_DIR}/Observability/Kibana/init-kibana.sh" &
  fi
done

echo ""
echo "=========================================="
echo "  All services have been started."
echo "=========================================="
echo ""
echo "--- Container Summary ---"
docker ps --filter "name=Claims-Processor-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""
echo "--- Network Info ---"
docker network inspect "$NETWORK_NAME" --format '{{.Name}}: {{len .Containers}} container(s) connected' 2>/dev/null || true
echo "=========================================="
