import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-slo-compliance',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './slo-compliance.component.html'
})
export class SloComplianceComponent {
  slos = [
    { sloId: 'SLO-001', name: 'Claims API Availability', target: '99.9%', current: '99.97%', errorBudget: '72%', status: 'Met', window: '30 days', service: 'API-Claims' },
    { sloId: 'SLO-002', name: 'Claims API Latency (p99)', target: '< 500ms', current: '142ms', errorBudget: '85%', status: 'Met', window: '30 days', service: 'API-Claims' },
    { sloId: 'SLO-003', name: 'Members API Availability', target: '99.9%', current: '99.99%', errorBudget: '90%', status: 'Met', window: '30 days', service: 'API-Members' },
    { sloId: 'SLO-004', name: 'Entitlements API Availability', target: '99.9%', current: '99.82%', errorBudget: '12%', status: 'At Risk', window: '30 days', service: 'API-Entitlements' },
    { sloId: 'SLO-005', name: 'Entitlements API Latency (p99)', target: '< 500ms', current: '485ms', errorBudget: '8%', status: 'At Risk', window: '30 days', service: 'API-Entitlements' },
    { sloId: 'SLO-006', name: 'Claim Processing Time', target: '< 2 min', current: '1m 24s', errorBudget: '65%', status: 'Met', window: '30 days', service: 'API-Claims' },
    { sloId: 'SLO-007', name: 'Document Extraction Success', target: '99.5%', current: '99.72%', errorBudget: '44%', status: 'Met', window: '30 days', service: 'Lambda' }
  ];

  getBadgeClass(status: string): string {
    switch (status) {
      case 'Met': return 'badge badge-success';
      case 'At Risk': return 'badge badge-warning';
      case 'Breached': return 'badge badge-danger';
      default: return 'badge badge-gray';
    }
  }
}
