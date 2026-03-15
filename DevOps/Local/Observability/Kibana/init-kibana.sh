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
echo ""

# --- Saved Searches ---

# Create saved search: Claims by Stage
curl -sf -X POST "$KIBANA_HOST/api/saved_objects/search/claims-by-stage" \
  -H 'kbn-xsrf: true' \
  -H 'Content-Type: application/json' \
  -d '{
    "attributes": {
      "title": "Claims by Stage",
      "description": "All claims grouped by processing stage",
      "kibanaSavedObjectMeta": {
        "searchSourceJSON": "{\"index\":\"claims-*\",\"query\":{\"query\":\"\",\"language\":\"kuery\"},\"filter\":[]}"
      }
    }
  }' && echo "" && echo "Created saved search: Claims by Stage"

# Create saved search: Recent Application Errors
curl -sf -X POST "$KIBANA_HOST/api/saved_objects/search/app-errors" \
  -H 'kbn-xsrf: true' \
  -H 'Content-Type: application/json' \
  -d '{
    "attributes": {
      "title": "Application Errors (Last 24h)",
      "description": "All ERROR level log entries across services",
      "kibanaSavedObjectMeta": {
        "searchSourceJSON": "{\"index\":\"claims-app-logs-*\",\"query\":{\"query\":\"level: ERROR\",\"language\":\"kuery\"},\"filter\":[]}"
      }
    }
  }' && echo "" && echo "Created saved search: Application Errors"

# Create saved search: Slow Requests
curl -sf -X POST "$KIBANA_HOST/api/saved_objects/search/slow-requests" \
  -H 'kbn-xsrf: true' \
  -H 'Content-Type: application/json' \
  -d '{
    "attributes": {
      "title": "Slow Requests (>1s)",
      "description": "Requests taking more than 1 second",
      "kibanaSavedObjectMeta": {
        "searchSourceJSON": "{\"index\":\"claims-app-logs-*\",\"query\":{\"query\":\"elapsed_time > 1000\",\"language\":\"kuery\"},\"filter\":[]}"
      }
    }
  }' && echo "" && echo "Created saved search: Slow Requests"

echo ""
echo "All Kibana saved searches created successfully."
