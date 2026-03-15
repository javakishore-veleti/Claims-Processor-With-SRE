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
| `CLAIMS_PROC_REDIS_AUTH_TOKEN` | ElastiCache Redis auth token | (strong token) |
| `CLAIMS_PROC_ENCRYPTION_KEY` | AES-256 key for EncryptionService | (32-byte base64 key) |

## GitHub Variables (Settings > Secrets and variables > Actions > Variables)

| Variable Name | Description | Default |
|---|---|---|
| `CLAIMS_PROC_AWS_REGION` | Target AWS region | `us-east-1` |
| `CLAIMS_PROC_PROJECT_NAME` | Resource prefix | `claims-proc` |

## Workflow Execution Order

Run workflows in this order for a fresh AWS deployment:

```
AWS_01 → AWS_02 → AWS_03 → AWS_04 → AWS_05 → AWS_06 → AWS_08 → AWS_09 → AWS_10
```

| Step | Workflow | What It Creates |
|---|---|---|
| 1 | AWS_01 - VPC & Security Groups | VPC (or use existing), security groups |
| 2 | AWS_02 - Core Infrastructure | RDS PostgreSQL, S3 buckets, ECR repos, ElastiCache Redis |
| 3 | AWS_03 - Observability | CloudWatch log groups, dashboards, alarms, CloudTrail |
| 4 | AWS_04 - Auth (Cognito) | Cognito User Pool, groups, app client |
| 5 | AWS_05 - Messaging & Search | Kinesis streams (or MSK), OpenSearch domain |
| 6 | AWS_06 - Secrets Manager | Secret entries with connection details |
| 7 | AWS_07 - EKS Cluster | (Optional) EKS cluster + node groups |
| 8 | AWS_08 - Build & Push ECR | Docker images for all 10 modules → ECR |
| 9 | AWS_09 - API Gateway & Fargate | ECS/Fargate services, ALB, routing |
| 10 | AWS_10 - Data Seed | Seed tenants, members, claims, users in RDS |

**To destroy everything:** Run `AWS_11 - Destroy All` (requires typing environment name to confirm).

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
