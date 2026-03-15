#!/usr/bin/env bash
# Environment Status Dashboard — called by AWS_00_Environment_Dashboard.yml
# Required env vars: ENV, REGION, PROJECT_NAME, REPO, AWS credentials (already configured)
set -euo pipefail

REGION="${REGION}"
PROJECT="${PROJECT_NAME}"
NOW=$(date -u '+%Y-%m-%d %H:%M UTC')
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
CON="https://${REGION}.console.aws.amazon.com"
S="$GITHUB_STEP_SUMMARY"

# ── helpers ───────────────────────────────────────────────────────────
w()  { printf '%s\n' "$*" >> "$S"; }
wl() { printf '\n'       >> "$S"; }

stack_status() {
  aws cloudformation describe-stacks --stack-name "$1" \
    --query "Stacks[0].StackStatus" --output text 2>/dev/null || echo "NOT_DEPLOYED"
}
stack_outputs() {
  aws cloudformation describe-stacks --stack-name "$1" \
    --query "Stacks[0].Outputs" --output json 2>/dev/null || echo "[]"
}
get_out() { printf '%s' "$1" | jq -r ".[] | select(.OutputKey==\"$2\") | .OutputValue" 2>/dev/null; }
urlencode() { python3 -c "import urllib.parse,sys; print(urllib.parse.quote(sys.argv[1],safe=''))" "$1"; }
icon() {
  case "$1" in
    CREATE_COMPLETE|UPDATE_COMPLETE) echo "✅" ;;
    *IN_PROGRESS)                   echo "🔄" ;;
    *FAILED|*ROLLBACK*)             echo "❌" ;;
    NOT_DEPLOYED)                   echo "⚪" ;;
    *)                              echo "⚠️" ;;
  esac
}
cf_url() { echo "${CON}/cloudformation/home?region=${REGION}#/stacks/stackinfo?filteringText=$1"; }
sg_url() { echo "${CON}/ec2/home?region=${REGION}#SecurityGroups:groupId=$1"; }

# ── stack names ───────────────────────────────────────────────────────
N1="${PROJECT}-${ENV}-vpc-sg"
N2="${PROJECT}-${ENV}-core-infra"
N3="${PROJECT}-${ENV}-observability"
N4="${PROJECT}-${ENV}-auth"
N5="${PROJECT}-${ENV}-messaging-search"
N6="${PROJECT}-${ENV}-secrets"
N7="${PROJECT}-${ENV}-eks"
N9="${PROJECT}-${ENV}-fargate"
N10="${PROJECT}-${ENV}-data-seed"

echo "Fetching stack statuses..."
ST1=$(stack_status "$N1");  ST2=$(stack_status "$N2");  ST3=$(stack_status "$N3")
ST4=$(stack_status "$N4");  ST5=$(stack_status "$N5");  ST6=$(stack_status "$N6")
ST7=$(stack_status "$N7");  ST9=$(stack_status "$N9");  ST10=$(stack_status "$N10")

# ══════════════════════════════════════════════════════════════════════
# HEADER
# ══════════════════════════════════════════════════════════════════════
w "# 🏗️ Claims Processor · \`${ENV}\` Environment Dashboard"
wl
w "**Account:** \`${ACCOUNT_ID}\` · **Region:** \`${REGION}\` · **Generated:** ${NOW}"
wl
w "> Run this workflow anytime for a live snapshot — no AWS Console hunting needed."
wl
w "---"
wl

# ══════════════════════════════════════════════════════════════════════
# OVERVIEW TABLE
# ══════════════════════════════════════════════════════════════════════
w "## 📊 Stack Overview"
wl
w "| # | Stack | Workflow | Status | CloudFormation |"
w "|---|-------|----------|--------|----------------|"
w "| 1  | VPC & Security Groups     | AWS_01 | $(icon "$ST1") \`$ST1\`  | [🔗 Stack]($(cf_url "$N1")) |"
w "| 2  | Core Infrastructure       | AWS_02 | $(icon "$ST2") \`$ST2\`  | [🔗 Stack]($(cf_url "$N2")) |"
w "| 3  | Observability             | AWS_03 | $(icon "$ST3") \`$ST3\`  | [🔗 Stack]($(cf_url "$N3")) |"
w "| 4  | Auth (Cognito)            | AWS_04 | $(icon "$ST4") \`$ST4\`  | [🔗 Stack]($(cf_url "$N4")) |"
w "| 5  | Messaging & Search        | AWS_05 | $(icon "$ST5") \`$ST5\`  | [🔗 Stack]($(cf_url "$N5")) |"
w "| 6  | Secrets Manager           | AWS_06 | $(icon "$ST6") \`$ST6\`  | [🔗 Stack]($(cf_url "$N6")) |"
w "| 7  | EKS Cluster               | AWS_07 | $(icon "$ST7") \`$ST7\`  | [🔗 Stack]($(cf_url "$N7")) |"
w "| 8  | Build & Push ECR          | AWS_08 | _(image push — no stack)_ | — |"
w "| 9  | API Gateway + Fargate     | AWS_09 | $(icon "$ST9") \`$ST9\`  | [🔗 Stack]($(cf_url "$N9")) |"
w "| 10 | Data Seed (Lambda)        | AWS_10 | $(icon "$ST10") \`$ST10\` | [🔗 Stack]($(cf_url "$N10")) |"
wl
w "---"
wl

# ══════════════════════════════════════════════════════════════════════
# AWS_01 — VPC & Security Groups
# ══════════════════════════════════════════════════════════════════════
if [[ "$ST1" != "NOT_DEPLOYED" ]]; then
  echo "  → Fetching AWS_01 outputs..."
  O1=$(stack_outputs "$N1")
  VPC=$(get_out "$O1" "VpcId")
  PUB1=$(get_out "$O1" "PublicSubnet1Id");  PUB2=$(get_out "$O1" "PublicSubnet2Id")
  PRV1=$(get_out "$O1" "PrivateSubnet1Id"); PRV2=$(get_out "$O1" "PrivateSubnet2Id")
  ALB_SG=$(get_out "$O1" "ALBSecurityGroupId");  APP_SG=$(get_out "$O1" "AppSecurityGroupId")
  RDS_SG=$(get_out "$O1" "RDSSecurityGroupId");  RED_SG=$(get_out "$O1" "RedisSecurityGroupId")
  KFK_SG=$(get_out "$O1" "KafkaSecurityGroupId"); OS_SG=$(get_out "$O1" "OpenSearchSecurityGroupId")
  VU="${CON}/vpc/home?region=${REGION}"
  w "## 🌐 Networking — VPC & Security Groups (AWS_01)"
  wl
  w "| Resource | ID | Console |"
  w "|----------|----|---------|"
  w "| VPC | \`${VPC}\` | [🔗 VPC](${VU}#VpcDetails:VpcId=${VPC}) |"
  w "| Public Subnet 1 | \`${PUB1}\` | [🔗 Subnet](${VU}#SubnetDetails:subnetId=${PUB1}) |"
  w "| Public Subnet 2 | \`${PUB2}\` | [🔗 Subnet](${VU}#SubnetDetails:subnetId=${PUB2}) |"
  w "| Private Subnet 1 | \`${PRV1}\` | [🔗 Subnet](${VU}#SubnetDetails:subnetId=${PRV1}) |"
  w "| Private Subnet 2 | \`${PRV2}\` | [🔗 Subnet](${VU}#SubnetDetails:subnetId=${PRV2}) |"
  w "| ALB Security Group | \`${ALB_SG}\` | [🔗 SG]($(sg_url "$ALB_SG")) |"
  w "| App Security Group | \`${APP_SG}\` | [🔗 SG]($(sg_url "$APP_SG")) |"
  w "| RDS Security Group | \`${RDS_SG}\` | [🔗 SG]($(sg_url "$RDS_SG")) |"
  w "| Redis Security Group | \`${RED_SG}\` | [🔗 SG]($(sg_url "$RED_SG")) |"
  w "| Kafka Security Group | \`${KFK_SG}\` | [🔗 SG]($(sg_url "$KFK_SG")) |"
  w "| OpenSearch Security Group | \`${OS_SG}\` | [🔗 SG]($(sg_url "$OS_SG")) |"
  wl; w "---"; wl
fi

# ══════════════════════════════════════════════════════════════════════
# AWS_02 — Core Infrastructure
# ══════════════════════════════════════════════════════════════════════
if [[ "$ST2" != "NOT_DEPLOYED" ]]; then
  echo "  → Fetching AWS_02 outputs..."
  O2=$(stack_outputs "$N2")
  RDS_EP=$(get_out "$O2" "RDSEndpoint"); RDS_PORT=$(get_out "$O2" "RDSPort")
  REDIS_EP=$(get_out "$O2" "RedisEndpoint")
  S3_DOCS=$(get_out "$O2" "S3DocumentsBucket"); S3_STATIC=$(get_out "$O2" "S3StaticBucket")
  RDS_ID=$(echo "$RDS_EP" | cut -d'.' -f1)
  REDIS_ID=$(echo "$REDIS_EP" | sed 's/^master\.//' | cut -d'.' -f1)
  w "## 💾 Core Infrastructure — RDS · Redis · S3 · ECR (AWS_02)"
  wl
  w "### 🗄️ Database & Cache"
  w "| Resource | Endpoint | Console |"
  w "|----------|----------|---------|"
  w "| PostgreSQL RDS | \`${RDS_EP}:${RDS_PORT}\` | [🔗 RDS](${CON}/rds/home?region=${REGION}#database:id=${RDS_ID};is-cluster=false) |"
  w "| Redis | \`${REDIS_EP}:6379\` | [🔗 ElastiCache](${CON}/elasticache/home?region=${REGION}#/redis/${REDIS_ID}) |"
  wl
  w "### 🪣 S3 Buckets"
  w "| Bucket | Console |"
  w "|--------|---------|"
  w "| \`${S3_DOCS}\` — documents | [🔗 Open](https://s3.console.aws.amazon.com/s3/buckets/${S3_DOCS}) |"
  w "| \`${S3_STATIC}\` — static assets | [🔗 Open](https://s3.console.aws.amazon.com/s3/buckets/${S3_STATIC}) |"
  wl
  w "### 📦 ECR Repositories"
  w "| Repository | URI | Console |"
  w "|------------|-----|---------|"
  for key in ECRApiClaimsUri ECRApiEntitlementsUri ECRApiMembersUri ECRApiTenantsUri \
             ECRPortalClaimsAdvisorUri ECRPortalClaimsMemberUri ECRPortalBatchAssignmentUri \
             ECRPortalEntitlementsUri ECRPortalSREUri ECRPortalTenantsUri; do
    URI=$(get_out "$O2" "$key")
    REPO_PATH=$(echo "$URI" | cut -d'/' -f2-)
    LABEL=$(echo "$key" | sed 's/Uri$//' | sed 's/^ECR//' | \
            sed 's/\([A-Z]\)/-\1/g' | sed 's/^-//' | tr '[:upper:]' '[:lower:]')
    w "| \`${LABEL}\` | \`${URI}\` | [🔗 Repo](${CON}/ecr/repositories/private/${ACCOUNT_ID}/${REPO_PATH}?region=${REGION}) |"
  done
  wl; w "---"; wl
fi

# ══════════════════════════════════════════════════════════════════════
# AWS_03 — Observability
# ══════════════════════════════════════════════════════════════════════
if [[ "$ST3" != "NOT_DEPLOYED" ]]; then
  echo "  → Fetching AWS_03 outputs..."
  O3=$(stack_outputs "$N3")
  DASH=$(get_out "$O3" "DashboardName")
  TOPIC_ARN=$(get_out "$O3" "AlarmTopicArn")
  TRAIL_ARN=$(get_out "$O3" "TrailArn")
  TOPIC_NAME=$(echo "$TOPIC_ARN" | awk -F: '{print $NF}')
  TRAIL_NAME=$(echo "$TRAIL_ARN" | awk -F: '{print $NF}')
  w "## 📈 Observability — CloudWatch · CloudTrail · SNS (AWS_03)"
  wl
  w "| Resource | Value | Console |"
  w "|----------|-------|---------|"
  w "| CloudWatch Dashboard | \`${DASH}\` | [🔗 Dashboard](${CON}/cloudwatch/home?region=${REGION}#dashboards:name=${DASH}) |"
  w "| CloudTrail | \`${TRAIL_NAME}\` | [🔗 Trail](${CON}/cloudtrail/home?region=${REGION}#/trails/${TRAIL_ARN}) |"
  w "| SNS Alarm Topic | \`${TOPIC_NAME}\` | [🔗 Topic](${CON}/sns/v3/home?region=${REGION}#/topic/${TOPIC_ARN}) |"
  wl
  w "### 📋 CloudWatch Log Groups"
  w "| Service | Log Group | Console |"
  w "|---------|-----------|---------|"
  for key in LogGroupArnApiClaims LogGroupArnApiEntitlements LogGroupArnApiMembers \
             LogGroupArnApiTenants LogGroupArnPortalClaimsAdvisor LogGroupArnPortalClaimsMember \
             LogGroupArnPortalBatchAssignment LogGroupArnPortalEntitlements \
             LogGroupArnPortalSre LogGroupArnPortalTenants; do
    ARN=$(get_out "$O3" "$key")
    LG=$(echo "$ARN" | sed 's|arn:aws:logs:[^:]*:[^:]*:log-group:||' | sed 's|:\*$||')
    LG_ENC=$(urlencode "$LG")
    SVC=$(echo "$key" | sed 's/LogGroupArn//' | \
          sed 's/\([A-Z]\)/-\1/g' | sed 's/^-//' | tr '[:upper:]' '[:lower:]')
    w "| \`${SVC}\` | \`${LG}\` | [🔗 Logs](${CON}/cloudwatch/home?region=${REGION}#logsV2:log-groups/log-group/${LG_ENC}) |"
  done
  wl; w "---"; wl
fi

# ══════════════════════════════════════════════════════════════════════
# AWS_04 — Auth (Cognito)
# ══════════════════════════════════════════════════════════════════════
if [[ "$ST4" != "NOT_DEPLOYED" ]]; then
  echo "  → Fetching AWS_04 outputs..."
  O4=$(stack_outputs "$N4")
  UP_ID=$(get_out "$O4" "UserPoolId")
  UP_CLIENT=$(get_out "$O4" "UserPoolClientId")
  UP_DOMAIN=$(get_out "$O4" "UserPoolDomain")
  w "## 🔐 Auth — Cognito (AWS_04)"
  wl
  w "| Resource | Value | Console |"
  w "|----------|-------|---------|"
  w "| User Pool ID | \`${UP_ID}\` | [🔗 Users](${CON}/cognito/v2/idp/user-pools/${UP_ID}/users?region=${REGION}) |"
  w "| App Client ID | \`${UP_CLIENT}\` | [🔗 App Clients](${CON}/cognito/v2/idp/user-pools/${UP_ID}/app-integration?region=${REGION}) |"
  w "| Hosted UI Domain | \`${UP_DOMAIN}\` | [🔗 Launch Login Page](https://${UP_DOMAIN}/login) |"
  wl; w "---"; wl
fi

# ══════════════════════════════════════════════════════════════════════
# AWS_05 — Messaging & Search
# ══════════════════════════════════════════════════════════════════════
if [[ "$ST5" != "NOT_DEPLOYED" ]]; then
  echo "  → Fetching AWS_05 outputs..."
  O5=$(stack_outputs "$N5")
  MSK_PLAIN=$(get_out "$O5" "MSKBootstrapBrokers")
  MSK_TLS=$(get_out "$O5" "MSKBootstrapBrokersTls")
  OS_EP=$(get_out "$O5" "OpenSearchDomainEndpoint")
  w "## 📨 Messaging & Search — MSK Kafka · OpenSearch (AWS_05)"
  wl
  w "| Resource | Value | Console |"
  w "|----------|-------|---------|"
  w "| MSK Bootstrap (plaintext) | \`${MSK_PLAIN}\` | [🔗 MSK](${CON}/msk/home?region=${REGION}#/clusters) |"
  w "| MSK Bootstrap (TLS) | \`${MSK_TLS}\` | [🔗 MSK](${CON}/msk/home?region=${REGION}#/clusters) |"
  w "| OpenSearch Endpoint | \`https://${OS_EP}\` | [🔗 OpenSearch](${CON}/aos/home?region=${REGION}#/opensearch/domains) |"
  w "| OpenSearch Dashboard | \`https://${OS_EP}/_dashboards\` | [🔗 Open](https://${OS_EP}/_dashboards) |"
  wl; w "---"; wl
fi

# ══════════════════════════════════════════════════════════════════════
# AWS_06 — Secrets Manager
# ══════════════════════════════════════════════════════════════════════
if [[ "$ST6" != "NOT_DEPLOYED" ]]; then
  echo "  → AWS_06 deployed — listing secrets..."
  SM_BASE="${CON}/secretsmanager/listsecrets?region=${REGION}"
  w "## 🔑 Secrets Manager (AWS_06)"
  wl
  w "> Secrets store credentials for RDS, Redis, MSK, OpenSearch, and Cognito."
  wl
  w "| Secret | Console |"
  w "|--------|---------|"
  # List actual secrets filtered by environment prefix
  aws secretsmanager list-secrets --region "$REGION" \
    --query "SecretList[?contains(Name, \`${PROJECT}-${ENV}\`)].{Name:Name,ARN:ARN}" \
    --output json 2>/dev/null | \
    jq -r '.[] | "| `\(.Name)` | [🔗 Secret]('"${CON}"'/secretsmanager/secret?name=\(.Name)&region='"${REGION}"') |"' >> "$S" || \
    w "| _(run AWS_06 to populate)_ | [🔗 All Secrets](${SM_BASE}) |"
  wl; w "---"; wl
fi

# ══════════════════════════════════════════════════════════════════════
# AWS_07 — EKS Cluster
# ══════════════════════════════════════════════════════════════════════
if [[ "$ST7" != "NOT_DEPLOYED" ]]; then
  echo "  → Fetching AWS_07 outputs..."
  O7=$(stack_outputs "$N7")
  EKS_NAME=$(get_out "$O7" "ClusterName")
  EKS_EP=$(get_out "$O7" "ClusterEndpoint")
  NG_NAME=$(get_out "$O7" "NodeGroupName")
  OIDC_ARN=$(get_out "$O7" "OIDCProviderArn")
  w "## ☸️ EKS Cluster (AWS_07)"
  wl
  w "| Resource | Value | Console |"
  w "|----------|-------|---------|"
  w "| Cluster Name | \`${EKS_NAME}\` | [🔗 Cluster](${CON}/eks/home?region=${REGION}#/clusters/${EKS_NAME}) |"
  w "| API Endpoint | \`${EKS_EP}\` | [🔗 Detail](${CON}/eks/home?region=${REGION}#/clusters/${EKS_NAME}) |"
  w "| Node Group | \`${NG_NAME}\` | [🔗 Node Groups](${CON}/eks/home?region=${REGION}#/clusters/${EKS_NAME}/nodegroups) |"
  w "| OIDC Provider | \`${OIDC_ARN}\` | [🔗 IAM OIDC](https://console.aws.amazon.com/iam/home?#/providers) |"
  wl; w "---"; wl
fi

# ══════════════════════════════════════════════════════════════════════
# AWS_08 — ECR Image Build & Push (no CF stack)
# ══════════════════════════════════════════════════════════════════════
w "## 🐳 Container Images — ECR Build & Push (AWS_08)"
wl
w "> AWS_08 builds Docker images and pushes to ECR — no CloudFormation stack. Repositories are listed in the AWS_02 section above."
wl
w "| | Link |"
w "|-|------|"
w "| All ECR Repositories | [🔗 ECR Console](${CON}/ecr/repositories?region=${REGION}) |"
w "| AWS_08 Workflow History | [🔗 Run History](https://github.com/${REPO}/actions/workflows/AWS_08_Build_Push_ECR.yml) |"
wl; w "---"; wl

# ══════════════════════════════════════════════════════════════════════
# AWS_09 — API Gateway + Fargate (live application URLs)
# ══════════════════════════════════════════════════════════════════════
if [[ "$ST9" != "NOT_DEPLOYED" ]]; then
  echo "  → Fetching AWS_09 outputs..."
  O9=$(stack_outputs "$N9")
  ALB_DNS=$(get_out "$O9" "ALBDnsName")
  ECS_CLUSTER=$(get_out "$O9" "ECSClusterName")
  APP_BASE="http://${ALB_DNS}"
  w "## 🌍 Live Application URLs — Fargate (AWS_09)"
  wl
  w "### 🖥️ Portals"
  w "| Portal | URL | Open |"
  w "|--------|-----|------|"
  w "| Claims Advisor (Staff) | \`${APP_BASE}:8081\` | [🔗 Open](${APP_BASE}:8081) |"
  w "| Claims Member (Customer) | \`${APP_BASE}:8082\` | [🔗 Open](${APP_BASE}:8082) |"
  w "| Batch Assignment | \`${APP_BASE}:8085\` | [🔗 Open](${APP_BASE}:8085) |"
  w "| Tenant Management | \`${APP_BASE}:8088\` | [🔗 Open](${APP_BASE}:8088) |"
  w "| Entitlements Management | \`${APP_BASE}:8089\` | [🔗 Open](${APP_BASE}:8089) |"
  w "| SRE Dashboard | \`${APP_BASE}:8090\` | [🔗 Open](${APP_BASE}:8090) |"
  wl
  w "### 🔌 APIs (Swagger UI)"
  w "| API | Swagger URL | Open |"
  w "|-----|-------------|------|"
  w "| Claims API | \`${APP_BASE}:8083/swagger-ui.html\` | [🔗 Swagger](${APP_BASE}:8083/swagger-ui.html) |"
  w "| Members API | \`${APP_BASE}:8084/swagger-ui.html\` | [🔗 Swagger](${APP_BASE}:8084/swagger-ui.html) |"
  w "| Tenants API | \`${APP_BASE}:8086/swagger-ui.html\` | [🔗 Swagger](${APP_BASE}:8086/swagger-ui.html) |"
  w "| Entitlements API | \`${APP_BASE}:8087/swagger-ui.html\` | [🔗 Swagger](${APP_BASE}:8087/swagger-ui.html) |"
  wl
  w "### ⚙️ ECS & Load Balancer"
  w "| Resource | Value | Console |"
  w "|----------|-------|---------|"
  w "| ALB DNS | \`${ALB_DNS}\` | [🔗 Load Balancers](${CON}/ec2/home?region=${REGION}#LoadBalancers) |"
  w "| ECS Cluster | \`${ECS_CLUSTER}\` | [🔗 ECS Services](${CON}/ecs/home?region=${REGION}#/clusters/${ECS_CLUSTER}/services) |"
  wl; w "---"; wl
fi

# ══════════════════════════════════════════════════════════════════════
# AWS_10 — Data Seed Lambda
# ══════════════════════════════════════════════════════════════════════
if [[ "$ST10" != "NOT_DEPLOYED" ]]; then
  echo "  → Fetching AWS_10 outputs..."
  O10=$(stack_outputs "$N10")
  FN=$(get_out "$O10" "LambdaFunctionName")
  w "## 🌱 Data Seed — Lambda (AWS_10)"
  wl
  w "| Resource | Value | Console |"
  w "|----------|-------|---------|"
  w "| Lambda Function | \`${FN}\` | [🔗 Function](${CON}/lambda/home?region=${REGION}#/functions/${FN}) |"
  w "| Test / Invoke | _(run in console)_ | [🔗 Test Tab](${CON}/lambda/home?region=${REGION}#/functions/${FN}?tab=testing) |"
  w "| Logs | _(CloudWatch)_ | [🔗 Log Group](${CON}/cloudwatch/home?region=${REGION}#logsV2:log-groups/log-group/$(urlencode "/aws/lambda/${FN}")) |"
  wl; w "---"; wl
fi

# ══════════════════════════════════════════════════════════════════════
# FOOTER — Global Quick Links
# ══════════════════════════════════════════════════════════════════════
w "## 🔗 Global Quick Links"
wl
w "| Service | Link |"
w "|---------|------|"
w "| CloudFormation — all active stacks | [🔗 Stacks](${CON}/cloudformation/home?region=${REGION}#/stacks?filteringStatus=active) |"
w "| CloudWatch — Metrics | [🔗 Metrics](${CON}/cloudwatch/home?region=${REGION}#metricsV2) |"
w "| CloudWatch — Alarms | [🔗 Alarms](${CON}/cloudwatch/home?region=${REGION}#alarmsV2) |"
w "| CloudWatch — Log Insights | [🔗 Log Insights](${CON}/cloudwatch/home?region=${REGION}#logsV2:logs-insights) |"
w "| IAM User (CI/CD) | [🔗 claims-proc-github-actions](https://console.aws.amazon.com/iam/home?#/users/details/claims-proc-github-actions) |"
w "| Cost Explorer | [🔗 Cost Explorer](https://console.aws.amazon.com/cost-management/home#/cost-explorer) |"
w "| GitHub Actions — all runs | [🔗 Actions](https://github.com/${REPO}/actions) |"
wl
echo "✅ Dashboard written to Job Summary. Open the 'Summary' tab above to view."
