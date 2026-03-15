import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-deployments',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './deployments.component.html'
})
export class DeploymentsComponent {
  deployments = [
    { deployId: 'DEP-0147', service: 'API-Claims', version: 'v3.2.1', environment: 'Staging', status: 'In Progress', deployer: 'GitHub Actions', startedAt: '2026-03-14 08:00 AM', strategy: 'Blue/Green' },
    { deployId: 'DEP-0146', service: 'API-Members', version: 'v2.8.4', environment: 'Production', status: 'Success', deployer: 'GitHub Actions', startedAt: '2026-03-13 06:00 PM', strategy: 'Canary' },
    { deployId: 'DEP-0145', service: 'Portal-Claims-Advisor', version: 'v4.1.0', environment: 'Production', status: 'Success', deployer: 'GitHub Actions', startedAt: '2026-03-13 05:30 PM', strategy: 'Rolling' },
    { deployId: 'DEP-0144', service: 'API-Entitlements', version: 'v2.5.2', environment: 'Staging', status: 'Rolled Back', deployer: 'bob.martinez', startedAt: '2026-03-12 03:15 PM', strategy: 'Blue/Green' },
    { deployId: 'DEP-0143', service: 'API-Tenants', version: 'v1.9.0', environment: 'Production', status: 'Success', deployer: 'GitHub Actions', startedAt: '2026-03-11 07:00 AM', strategy: 'Canary' }
  ];

  getBadgeClass(status: string): string {
    switch (status) {
      case 'Success': return 'badge badge-success';
      case 'In Progress': return 'badge badge-primary';
      case 'Rolled Back': return 'badge badge-danger';
      case 'Failed': return 'badge badge-danger';
      default: return 'badge badge-gray';
    }
  }
}
