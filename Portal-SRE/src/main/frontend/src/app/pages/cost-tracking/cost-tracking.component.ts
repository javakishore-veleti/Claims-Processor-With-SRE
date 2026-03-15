import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cost-tracking',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cost-tracking.component.html'
})
export class CostTrackingComponent {
  costBreakdown = [
    { category: 'Compute (Fargate)', lastMonth: '$1,245.00', currentMonth: '$1,312.40', trend: '+5.4%', budget: '$1,500.00' },
    { category: 'Database (RDS)', lastMonth: '$342.00', currentMonth: '$342.00', trend: '0.0%', budget: '$400.00' },
    { category: 'Messaging (MSK)', lastMonth: '$456.00', currentMonth: '$456.00', trend: '0.0%', budget: '$500.00' },
    { category: 'Cache (ElastiCache)', lastMonth: '$124.80', currentMonth: '$124.80', trend: '0.0%', budget: '$150.00' },
    { category: 'Storage (S3)', lastMonth: '$22.10', currentMonth: '$28.40', trend: '+28.5%', budget: '$50.00' },
    { category: 'Serverless (Lambda)', lastMonth: '$14.20', currentMonth: '$18.90', trend: '+33.1%', budget: '$30.00' },
    { category: 'Monitoring (CloudWatch)', lastMonth: '$38.90', currentMonth: '$45.60', trend: '+17.2%', budget: '$60.00' },
    { category: 'Networking (VPC, ALB)', lastMonth: '$89.00', currentMonth: '$92.30', trend: '+3.7%', budget: '$120.00' }
  ];
}
