import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reports.component.html'
})
export class ReportsComponent {
  stats = [
    { label: 'Total Claims', value: '1,247', trend: '+12% from last month' },
    { label: 'Approved', value: '78.4%', trend: '+2.1% from last month' },
    { label: 'Denied', value: '14.2%', trend: '-1.3% from last month' },
    { label: 'Avg Processing Time', value: '4.2 min', trend: '-0.8 min from last month' },
  ];

  topDenialReasons = [
    { reason: 'Pre-authorization not on file', count: 42, percentage: 23.7 },
    { reason: 'Service not covered under plan', count: 38, percentage: 21.5 },
    { reason: 'Duplicate claim submission', count: 29, percentage: 16.4 },
    { reason: 'Missing documentation', count: 25, percentage: 14.1 },
    { reason: 'Exceeded benefit maximum', count: 18, percentage: 10.2 },
  ];
}
