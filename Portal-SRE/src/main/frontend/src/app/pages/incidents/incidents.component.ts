import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-incidents',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './incidents.component.html'
})
export class IncidentsComponent {
  incidents = [
    { incidentId: 'INC-2026-042', title: 'API-Entitlements high latency', severity: 'P2', status: 'Investigating', service: 'API-Entitlements', startedAt: '2026-03-14 07:22 AM', duration: '1h 38m', assignee: 'Bob Martinez' },
    { incidentId: 'INC-2026-041', title: 'Kafka consumer lag spike', severity: 'P3', status: 'Resolved', service: 'Kafka Broker', startedAt: '2026-03-13 02:15 PM', duration: '45m', assignee: 'Bob Martinez' },
    { incidentId: 'INC-2026-038', title: 'Claims API 5xx errors during deployment', severity: 'P1', status: 'Resolved', service: 'API-Claims', startedAt: '2026-03-10 11:30 AM', duration: '22m', assignee: 'Alice Johnson' },
    { incidentId: 'INC-2026-035', title: 'Redis connection pool exhaustion', severity: 'P2', status: 'Resolved', service: 'Redis Cache', startedAt: '2026-03-07 04:45 PM', duration: '1h 12m', assignee: 'Bob Martinez' },
    { incidentId: 'INC-2026-031', title: 'Database connection timeout under load', severity: 'P1', status: 'Resolved', service: 'PostgreSQL', startedAt: '2026-03-03 09:00 AM', duration: '38m', assignee: 'Alice Johnson' }
  ];

  getBadgeClass(field: string): string {
    switch (field) {
      case 'P1': return 'badge badge-danger';
      case 'P2': return 'badge badge-warning';
      case 'P3': return 'badge badge-gray';
      case 'Investigating': return 'badge badge-warning';
      case 'Resolved': return 'badge badge-success';
      default: return 'badge badge-gray';
    }
  }
}
