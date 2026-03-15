import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tenant-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tenant-analytics.component.html'
})
export class TenantAnalyticsComponent {
  tenants = [
    { tenantId: 'TNT-001', name: 'Acme Health Insurance', claimsProcessed: 4821, avgLatency: '128ms', errorRate: '0.03%', apiCalls: '142K', storageUsed: '24.5 GB', costMTD: '$892.40' },
    { tenantId: 'TNT-002', name: 'Blue Ridge Healthcare', claimsProcessed: 2134, avgLatency: '115ms', errorRate: '0.01%', apiCalls: '68K', storageUsed: '12.1 GB', costMTD: '$412.80' },
    { tenantId: 'TNT-003', name: 'Pacific Wellness Group', claimsProcessed: 3567, avgLatency: '142ms', errorRate: '0.05%', apiCalls: '98K', storageUsed: '18.7 GB', costMTD: '$678.20' },
    { tenantId: 'TNT-004', name: 'Great Lakes Health Co-op', claimsProcessed: 412, avgLatency: '98ms', errorRate: '0.00%', apiCalls: '12K', storageUsed: '2.3 GB', costMTD: '$89.50' },
    { tenantId: 'TNT-005', name: 'Southern Care Alliance', claimsProcessed: 1890, avgLatency: '132ms', errorRate: '0.02%', apiCalls: '54K', storageUsed: '9.8 GB', costMTD: '$345.60' }
  ];
}
