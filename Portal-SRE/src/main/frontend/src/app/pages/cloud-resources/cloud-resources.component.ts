import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cloud-resources',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cloud-resources.component.html'
})
export class CloudResourcesComponent {
  resources = [
    { resourceId: 'rds-claims-prod', name: 'Claims Production DB', type: 'RDS PostgreSQL', provider: 'AWS', region: 'us-east-1', status: 'Running', monthCost: '$342.00' },
    { resourceId: 'ecs-api-claims', name: 'API-Claims Fargate', type: 'ECS Fargate', provider: 'AWS', region: 'us-east-1', status: 'Running', monthCost: '$189.50' },
    { resourceId: 'ecs-api-members', name: 'API-Members Fargate', type: 'ECS Fargate', provider: 'AWS', region: 'us-east-1', status: 'Running', monthCost: '$156.20' },
    { resourceId: 's3-claims-docs', name: 'Claims Document Store', type: 'S3 Bucket', provider: 'AWS', region: 'us-east-1', status: 'Active', monthCost: '$28.40' },
    { resourceId: 'elasticache-prod', name: 'Redis Cache Cluster', type: 'ElastiCache', provider: 'AWS', region: 'us-east-1', status: 'Running', monthCost: '$124.80' },
    { resourceId: 'msk-claims-prod', name: 'Claims Kafka Cluster', type: 'Amazon MSK', provider: 'AWS', region: 'us-east-1', status: 'Running', monthCost: '$456.00' },
    { resourceId: 'lambda-doc-extract', name: 'Document Extraction', type: 'Lambda', provider: 'AWS', region: 'us-east-1', status: 'Active', monthCost: '$18.90' },
    { resourceId: 'cw-claims-slo', name: 'Claims SLO Monitoring', type: 'CloudWatch', provider: 'AWS', region: 'us-east-1', status: 'Active', monthCost: '$45.60' }
  ];

  getBadgeClass(status: string): string {
    return status === 'Running' || status === 'Active' ? 'badge badge-success' : 'badge badge-danger';
  }
}
