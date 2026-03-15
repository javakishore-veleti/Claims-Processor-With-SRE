import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-tenants',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './tenants.component.html'
})
export class TenantsComponent {
  tenants = [
    { tenantId: 'TNT-001', name: 'Acme Health Insurance', plan: 'Enterprise', members: 12450, status: 'Active', region: 'US-East', createdDate: '2024-06-15' },
    { tenantId: 'TNT-002', name: 'Blue Ridge Healthcare', plan: 'Professional', members: 5230, status: 'Active', region: 'US-Central', createdDate: '2024-08-22' },
    { tenantId: 'TNT-003', name: 'Pacific Wellness Group', plan: 'Enterprise', members: 8910, status: 'Active', region: 'US-West', createdDate: '2024-11-01' },
    { tenantId: 'TNT-004', name: 'Great Lakes Health Co-op', plan: 'Starter', members: 1120, status: 'Trial', region: 'US-Central', createdDate: '2026-01-10' },
    { tenantId: 'TNT-005', name: 'Southern Care Alliance', plan: 'Professional', members: 3780, status: 'Active', region: 'US-South', createdDate: '2025-03-18' },
    { tenantId: 'TNT-006', name: 'Nordic Health Partners', plan: 'Enterprise', members: 6420, status: 'Suspended', region: 'EU-North', createdDate: '2025-07-09' }
  ];

  getBadgeClass(status: string): string {
    switch (status) {
      case 'Active': return 'badge badge-success';
      case 'Trial': return 'badge badge-warning';
      case 'Suspended': return 'badge badge-danger';
      default: return 'badge badge-gray';
    }
  }
}
