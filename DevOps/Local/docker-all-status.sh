#!/usr/bin/env bash
###############################################################################
# docker-all-status.sh
# Shows running / stopped status for the shared Docker network and every local
# Docker Compose service.
###############################################################################
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="claims-processor-devops"
NETWORK_NAME="claims-processor-network"

# ── Docker Network Status ───────────────────────────────────────────────────
echo "=========================================="
echo "  Docker Network Status"
echo "=========================================="
if docker network inspect "$NETWORK_NAME" >/dev/null 2>&1; then
    echo "  Network '$NETWORK_NAME': EXISTS"
    container_count=$(docker network inspect "$NETWORK_NAME" --format '{{len .Containers}}' 2>/dev/null || echo "0")
    echo "  Connected containers: ${container_count}"
else
    echo "  Network '$NETWORK_NAME': NOT FOUND"
fi

# ── All Claims-Processor- Containers ────────────────────────────────────────
echo ""
echo "=========================================="
echo "  All Claims-Processor- Containers"
echo "=========================================="
docker ps -a --filter "name=Claims-Processor-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# ── Per-Service Status (check ALL, report which are running) ────────────────
ALL_SERVICES=(
  "Postgres"
  "Redis"
  "Kafka"
  "Search/Elastic"
  "Observability/Kibana"
  "Search/Filebeat"
  "Observability/Prometheus"
  "Observability/Alertmanager"
  "Observability/Grafana"
  "Tracing/Jaeger"
  "Tracing/Zipkin"
  "Wiremock"
  "Ollama"
)

echo ""
echo "=========================================="
echo "  Per-Service Status (Project: $PROJECT_NAME)"
echo "=========================================="
printf "%-35s %s\n" "SERVICE" "STATUS"
echo "---------------------------------------------------"

for svc in "${ALL_SERVICES[@]}"; do
  compose_file="${SCRIPT_DIR}/${svc}/docker-compose.yaml"

  if [[ ! -f "${compose_file}" ]]; then
    printf "%-35s %s\n" "${svc}" "COMPOSE FILE MISSING"
    continue
  fi

  running=$(docker compose -p "$PROJECT_NAME" -f "${compose_file}" ps --status running -q 2>/dev/null | wc -l | tr -d ' ')

  if [[ "${running}" -gt 0 ]]; then
    printf "%-35s %s\n" "${svc}" "UP (${running} container(s))"
  else
    printf "%-35s %s\n" "${svc}" "DOWN"
  fi
done

echo "=========================================="
