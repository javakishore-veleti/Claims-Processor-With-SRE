import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-jobs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './jobs.component.html'
})
export class JobsComponent {
  jobs = [
    { jobId: 'JOB-2026-0089', name: 'Q1 Member Enrollment', tenant: 'Acme Health Insurance', format: 'Excel', records: 1247, status: 'Running', progress: '68%', startedAt: '2026-03-14 08:30 AM' },
    { jobId: 'JOB-2026-0088', name: 'Provider Update Batch', tenant: 'Blue Ridge Healthcare', format: 'CSV', records: 342, status: 'Queued', progress: '0%', startedAt: '—' },
    { jobId: 'JOB-2026-0087', name: 'March Enrollment File', tenant: 'Pacific Wellness Group', format: 'EDI X12 834', records: 856, status: 'Validating', progress: '15%', startedAt: '2026-03-14 07:45 AM' }
  ];

  getBadgeClass(status: string): string {
    switch (status) {
      case 'Running': return 'badge badge-primary';
      case 'Queued': return 'badge badge-gray';
      case 'Validating': return 'badge badge-warning';
      case 'Completed': return 'badge badge-success';
      case 'Failed': return 'badge badge-danger';
      default: return 'badge badge-gray';
    }
  }
}
