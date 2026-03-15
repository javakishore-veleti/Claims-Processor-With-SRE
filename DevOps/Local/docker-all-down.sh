#!/usr/bin/env bash
###############################################################################
# docker-all-down.sh
# Stops ALL local Docker Compose services in reverse dependency order, then
# removes the shared Docker network if it is no longer in use.
###############################################################################
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="claims-processor-devops"
NETWORK_NAME="claims-processor-network"

# Reverse order — tear down dependents before dependencies
SERVICES=(
  # "Ollama"         # Commented out to save laptop resources
  # "Wiremock"       # Commented out to save laptop resources
  "Tracing/Zipkin"
  "Tracing/Jaeger"
  "Observability/Grafana"
  "Observability/Alertmanager"
  "Observability/Prometheus"
  "Observability/Kibana"
  "Search/Filebeat"
  "Search/Elastic"
  "Kafka"
  "Redis"
  "Postgres"
)

echo "=========================================="
echo "  Stopping ALL local Docker Compose services"
echo "  Project: $PROJECT_NAME"
echo "=========================================="

for svc in "${SERVICES[@]}"; do
  compose_file="${SCRIPT_DIR}/${svc}/docker-compose.yaml"

  if [[ ! -f "${compose_file}" ]]; then
    echo "[WARN]  Compose file not found: ${compose_file} — skipping"
    continue
  fi

  echo ""
  echo "---------- Stopping: ${svc} ----------"
  docker compose -p "$PROJECT_NAME" -f "${compose_file}" down
  echo "[OK]    ${svc} stopped"
done

# ── Docker Network Cleanup ──────────────────────────────────────────────────
echo ""
if docker network inspect "$NETWORK_NAME" >/dev/null 2>&1; then
    echo "Removing Docker network: $NETWORK_NAME"
    docker network rm "$NETWORK_NAME" 2>/dev/null || echo "Network still in use, skipping removal"
else
    echo "Docker network '$NETWORK_NAME' does not exist, nothing to remove"
fi

echo ""
echo "=========================================="
echo "  All services have been stopped."
echo "=========================================="
echo ""
echo "--- Remaining Containers (if any) ---"
docker ps -a --filter "name=Claims-Processor-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo "=========================================="
