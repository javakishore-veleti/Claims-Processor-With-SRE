import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-my-claims',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-claims.component.html'
})
export class MyClaimsComponent {
  searchTerm = '';

  claims = [
    { claimNumber: 'CLM-2026-00147', dateSubmitted: '2026-03-10', provider: 'Metro General Hospital', type: 'Inpatient', amount: '$12,450.00', stage: 'In Review' },
    { claimNumber: 'CLM-2026-00139', dateSubmitted: '2026-03-05', provider: 'Dr. Sarah Chen, MD', type: 'Office Visit', amount: '$285.00', stage: 'Approved' },
    { claimNumber: 'CLM-2026-00128', dateSubmitted: '2026-02-28', provider: 'Valley Imaging Center', type: 'Diagnostic', amount: '$1,800.00', stage: 'Pending Info' },
    { claimNumber: 'CLM-2026-00115', dateSubmitted: '2026-02-20', provider: 'Sunrise Pharmacy', type: 'Prescription', amount: '$142.50', stage: 'Approved' },
    { claimNumber: 'CLM-2026-00098', dateSubmitted: '2026-02-12', provider: 'Regional Orthopedics', type: 'Outpatient', amount: '$3,200.00', stage: 'Denied' },
    { claimNumber: 'CLM-2026-00084', dateSubmitted: '2026-02-01', provider: 'City Lab Services', type: 'Lab Work', amount: '$475.00', stage: 'Approved' }
  ];

  getBadgeClass(stage: string): string {
    switch (stage) {
      case 'In Review': return 'badge badge-primary';
      case 'Pending Info': return 'badge badge-warning';
      case 'Approved': return 'badge badge-success';
      case 'Denied': return 'badge badge-danger';
      default: return 'badge badge-gray';
    }
  }
}
