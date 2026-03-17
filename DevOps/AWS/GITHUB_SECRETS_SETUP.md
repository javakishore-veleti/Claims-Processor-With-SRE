# GitHub Secrets & Variables Setup for AWS Deployment

All secrets/variables are prefixed with `CLAIMS_PROC_` to avoid conflicts.

## GitHub Secrets (Settings > Secrets and variables > Actions > Secrets)

| Secret Name | Description | Example |
|---|---|---|
| `CLAIMS_PROC_AWS_ACCESS_KEY_ID` | IAM access key for GitHub Actions | `AKIA...` |
| `CLAIMS_PROC_AWS_SECRET_ACCESS_KEY` | IAM secret key | `wJalr...` |
| `CLAIMS_PROC_AWS_ACCOUNT_ID` | AWS account ID (for ECR URIs) | `123456789012` |
| `CLAIMS_PROC_VPC_ID` | Default existing VPC ID | `vpc-0abc123def456` |
| `CLAIMS_PROC_SUBNET_PRIVATE_1` | Private subnet 1 ID | `subnet-0abc...` |
| `CLAIMS_PROC_SUBNET_PRIVATE_2` | Private subnet 2 ID | `subnet-0def...` |
| `CLAIMS_PROC_SUBNET_PUBLIC_1` | Public subnet 1 ID | `subnet-0111...` |
| `CLAIMS_PROC_SUBNET_PUBLIC_2` | Public subnet 2 ID | `subnet-0222...` |
| `CLAIMS_PROC_RDS_MASTER_PASSWORD` | Master password for RDS PostgreSQL | (strong password) |
| `CLAIMS_PROC_REDIS_AUTH_TOKEN` | ElastiCache Redis auth token (16-128 chars, no `/` `"` `@` or spaces) | (strong token) |
| `CLAIMS_PROC_ENCRYPTION_KEY` | AES-256 key for EncryptionService | (32-byte base64 key) |

## GitHub Variables (Settings > Secrets and variables > Actions > Variables)

| Variable Name | Description | Default |
|---|---|---|
| `CLAIMS_PROC_AWS_REGION` | Target AWS region | `us-east-1` |
| `CLAIMS_PROC_PROJECT_NAME` | Resource prefix | `claims-proc` |

## Workflow Execution Order

Run workflows in this order for a fresh AWS deployment:

```
AWS_01 → AWS_02a/02b/02c/02d (parallel) → AWS_03 → AWS_04 → AWS_05 → AWS_06 → AWS_08 → AWS_09 → AWS_10
```

Or use **AWS_99 — Full Environment Orchestrator** to deploy everything in one click.

| Step | Workflow | What It Creates | VPC Required |
|---|---|---|---|
| 1 | AWS_01 - VPC & Security Groups | VPC (or use existing), security groups | — |
| 2a | AWS_02a - RDS PostgreSQL | RDS PostgreSQL instance | Yes |
| 2b | AWS_02b - S3 Buckets | S3 document & static asset buckets | No |
| 2c | AWS_02c - ECR Repositories | ECR repos for all 10 microservices | No |
| 2d | AWS_02d - ElastiCache Redis | Redis replication group with encryption | Yes |
| 3 | AWS_03 - Observability | CloudWatch log groups, dashboards, alarms, CloudTrail | No |
| 4 | AWS_04 - Auth (Cognito) | Cognito User Pool, groups, app client | No |
| 5 | AWS_05 - Messaging & Search | Kinesis streams (or MSK), OpenSearch domain | Yes |
| 6 | AWS_06 - Secrets Manager | Secret entries with connection details | No |
| 7 | AWS_07 - EKS Cluster | (Optional) EKS cluster + node groups | Yes |
| 8 | AWS_08 - Build & Push ECR | Docker images for all 10 modules → ECR | No |
| 9 | AWS_09 - API Gateway & Fargate | ECS/Fargate services, ALB, routing | Yes |
| 10 | AWS_10 - Data Seed | Seed tenants, members, claims, users in RDS | Yes |

> Steps 2b and 2c have no VPC dependency and can run in parallel with step 1.

**To destroy everything:** Run `AWS_98 - Destroy All` (requires typing environment name to confirm).

## IAM Policy for GitHub Actions User

The IAM user for GitHub Actions needs these permissions:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "cloudformation:*",
        "ec2:*",
        "ecs:*",
        "ecr:*",
        "eks:*",
        "elasticache:*",
        "es:*",
        "iam:*",
        "kinesis:*",
        "lambda:*",
        "logs:*",
        "rds:*",
        "s3:*",
        "secretsmanager:*",
        "sns:*",
        "cognito-idp:*",
        "cognito-identity:*",
        "cloudwatch:*",
        "cloudtrail:*",
        "elasticloadbalancing:*",
        "application-autoscaling:*",
        "ssm:*",
        "sts:GetCallerIdentity"
      ],
      "Resource": "*"
    }
  ]
}
```

> For production, scope these permissions down to specific resources using the `claims-proc-*` naming convention.
