#!/usr/bin/env bash
# Wait for Kibana to be ready, then create data views
set -euo pipefail

KIBANA_HOST="${KIBANA_HOST:-http://localhost:5601}"

echo "Waiting for Kibana..."
until curl -sf "$KIBANA_HOST/api/status" > /dev/null 2>&1; do
    sleep 3
done
echo "Kibana is ready."

# Create Application Logs data view
curl -sf -X POST "$KIBANA_HOST/api/data_views/data_view" \
  -H 'kbn-xsrf: true' \
  -H 'Content-Type: application/json' \
  -d '{
    "data_view": {
      "title": "claims-app-logs-*",
      "name": "Application Logs",
      "timeFieldName": "@timestamp"
    }
  }' && echo "" && echo "Created data view: Application Logs"

# Create Claims Search data view
curl -sf -X POST "$KIBANA_HOST/api/data_views/data_view" \
  -H 'kbn-xsrf: true' \
  -H 'Content-Type: application/json' \
  -d '{
    "data_view": {
      "title": "claims-*",
      "name": "Claims Data",
      "timeFieldName": "createdAt"
    }
  }' && echo "" && echo "Created data view: Claims Data"

# Create Members Search data view
curl -sf -X POST "$KIBANA_HOST/api/data_views/data_view" \
  -H 'kbn-xsrf: true' \
  -H 'Content-Type: application/json' \
  -d '{
    "data_view": {
      "title": "members-*",
      "name": "Members Data",
      "timeFieldName": "createdAt"
    }
  }' && echo "" && echo "Created data view: Members Data"

echo ""
echo "All Kibana data views created successfully."
