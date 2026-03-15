import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './history.component.html'
})
export class HistoryComponent {
  history = [
    { jobId: 'JOB-2026-0086', name: 'Feb Enrollment Refresh', tenant: 'Acme Health Insurance', records: 2104, succeeded: 2098, failed: 6, status: 'Completed', completedAt: '2026-03-12 04:18 PM', duration: '12m 34s' },
    { jobId: 'JOB-2026-0081', name: 'New Group Onboarding', tenant: 'Pacific Wellness Group', records: 534, succeeded: 534, failed: 0, status: 'Completed', completedAt: '2026-03-10 11:02 AM', duration: '3m 12s' },
    { jobId: 'JOB-2026-0076', name: 'Q4 Reconciliation', tenant: 'Blue Ridge Healthcare', records: 3810, succeeded: 3724, failed: 86, status: 'Completed', completedAt: '2026-03-07 09:45 AM', duration: '28m 06s' },
    { jobId: 'JOB-2026-0071', name: 'Terminated Members', tenant: 'Acme Health Insurance', records: 189, succeeded: 0, failed: 189, status: 'Failed', completedAt: '2026-03-05 02:30 PM', duration: '0m 48s' },
    { jobId: 'JOB-2026-0065', name: 'Annual Renewal Import', tenant: 'Pacific Wellness Group', records: 4521, succeeded: 4521, failed: 0, status: 'Completed', completedAt: '2026-03-01 06:00 AM', duration: '35m 22s' }
  ];

  getBadgeClass(status: string): string {
    return status === 'Completed' ? 'badge badge-success' : 'badge badge-danger';
  }
}
