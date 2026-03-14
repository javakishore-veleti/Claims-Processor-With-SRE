#!/usr/bin/env bash
# Wait for Elasticsearch to be ready, then create index templates and indices
set -euo pipefail

ES_HOST="${ES_HOST:-http://localhost:9200}"

echo "Waiting for Elasticsearch..."
until curl -sf "$ES_HOST/_cluster/health" > /dev/null 2>&1; do
    sleep 2
done
echo "Elasticsearch is ready."

# Application Logs index template
curl -sf -X PUT "$ES_HOST/_index_template/claims-app-logs" -H 'Content-Type: application/json' -d '{
  "index_patterns": ["claims-app-logs-*"],
  "template": {
    "settings": {
      "number_of_shards": 1,
      "number_of_replicas": 0,
      "index.lifecycle.name": "claims-logs-policy"
    },
    "mappings": {
      "properties": {
        "@timestamp": { "type": "date" },
        "message": { "type": "text" },
        "level": { "type": "keyword" },
        "logger_name": { "type": "keyword" },
        "thread_name": { "type": "keyword" },
        "app": { "type": "keyword" },
        "port": { "type": "keyword" },
        "traceId": { "type": "keyword" },
        "spanId": { "type": "keyword" },
        "stack_trace": { "type": "text" }
      }
    }
  }
}'
echo ""
echo "Created index template: claims-app-logs"

# Claims search index template
curl -sf -X PUT "$ES_HOST/_index_template/claims-search" -H 'Content-Type: application/json' -d '{
  "index_patterns": ["claims-*"],
  "priority": 100,
  "template": {
    "settings": {
      "number_of_shards": 1,
      "number_of_replicas": 0,
      "analysis": {
        "analyzer": {
          "claims_analyzer": {
            "type": "custom",
            "tokenizer": "standard",
            "filter": ["lowercase", "asciifolding"]
          }
        }
      }
    },
    "mappings": {
      "properties": {
        "claimId": { "type": "keyword" },
        "claimNumber": { "type": "keyword" },
        "customerId": { "type": "keyword" },
        "memberName": { "type": "text", "analyzer": "claims_analyzer", "fields": { "keyword": { "type": "keyword" } } },
        "stage": { "type": "keyword" },
        "submittedDate": { "type": "date" },
        "providerName": { "type": "text", "analyzer": "claims_analyzer", "fields": { "keyword": { "type": "keyword" } } },
        "providerNpi": { "type": "keyword" },
        "diagnosisCodes": { "type": "keyword" },
        "procedureCodes": { "type": "keyword" },
        "billedAmount": { "type": "double" },
        "allowedAmount": { "type": "double" },
        "confidenceScore": { "type": "double" },
        "extractedData": { "type": "object", "enabled": false },
        "createdAt": { "type": "date" },
        "updatedAt": { "type": "date" }
      }
    }
  }
}'
echo ""
echo "Created index template: claims-search"

# Members search index template
curl -sf -X PUT "$ES_HOST/_index_template/members-search" -H 'Content-Type: application/json' -d '{
  "index_patterns": ["members-*"],
  "priority": 100,
  "template": {
    "settings": {
      "number_of_shards": 1,
      "number_of_replicas": 0,
      "analysis": {
        "analyzer": {
          "members_analyzer": {
            "type": "custom",
            "tokenizer": "standard",
            "filter": ["lowercase", "asciifolding"]
          }
        }
      }
    },
    "mappings": {
      "properties": {
        "memberId": { "type": "keyword" },
        "memberNumber": { "type": "keyword" },
        "firstName": { "type": "text", "analyzer": "members_analyzer", "fields": { "keyword": { "type": "keyword" } } },
        "lastName": { "type": "text", "analyzer": "members_analyzer", "fields": { "keyword": { "type": "keyword" } } },
        "fullName": { "type": "text", "analyzer": "members_analyzer" },
        "dateOfBirth": { "type": "date" },
        "email": { "type": "keyword" },
        "phone": { "type": "keyword" },
        "policyNumber": { "type": "keyword" },
        "policyStatus": { "type": "keyword" },
        "address": { "type": "text" },
        "createdAt": { "type": "date" },
        "updatedAt": { "type": "date" }
      }
    }
  }
}'
echo ""
echo "Created index template: members-search"

# Create initial empty indices
curl -sf -X PUT "$ES_HOST/claims-data" -H 'Content-Type: application/json' -d '{}' && echo "" && echo "Created index: claims-data"
curl -sf -X PUT "$ES_HOST/members-data" -H 'Content-Type: application/json' -d '{}' && echo "" && echo "Created index: members-data"
curl -sf -X PUT "$ES_HOST/claims-app-logs-000001" -H 'Content-Type: application/json' -d '{"aliases":{"claims-app-logs-write":{}}}' && echo "" && echo "Created index: claims-app-logs-000001 with write alias"

echo ""
echo "All index templates and indices created successfully."
