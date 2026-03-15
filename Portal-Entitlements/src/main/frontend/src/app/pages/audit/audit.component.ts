import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-audit',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './audit.component.html'
})
export class AuditComponent {
  auditLogs = [
    { timestamp: '2026-03-14 08:14:22', user: 'alice.johnson', action: 'LOGIN', resource: 'Session', detail: 'Successful login from 10.0.1.42', severity: 'Info' },
    { timestamp: '2026-03-14 08:16:05', user: 'alice.johnson', action: 'CLAIM_UPDATE', resource: 'CLM-2026-00147', detail: 'Stage changed: EXTRACTION_REVIEW -> ELIGIBILITY_CHECK', severity: 'Info' },
    { timestamp: '2026-03-14 07:45:30', user: 'bob.martinez', action: 'LOGIN', resource: 'Session', detail: 'Successful login from 10.0.2.18', severity: 'Info' },
    { timestamp: '2026-03-14 07:52:11', user: 'bob.martinez', action: 'DEPLOYMENT', resource: 'API-Claims', detail: 'Deployed v3.2.1 to staging environment', severity: 'Warning' },
    { timestamp: '2026-03-13 16:30:44', user: 'emily.davis', action: 'LOGIN_FAILED', resource: 'Session', detail: 'Failed login attempt (account locked after 5 failures)', severity: 'Critical' },
    { timestamp: '2026-03-13 15:22:18', user: 'david.park', action: 'TENANT_UPDATE', resource: 'TNT-003', detail: 'Updated plan from Professional to Enterprise', severity: 'Info' },
    { timestamp: '2026-03-13 14:08:55', user: 'carol.wei', action: 'CLAIM_UPDATE', resource: 'CLM-2026-00139', detail: 'Stage changed: ADJUDICATION_REVIEW -> APPROVED', severity: 'Info' },
    { timestamp: '2026-03-13 09:00:00', user: 'system', action: 'BATCH_COMPLETE', resource: 'JOB-2026-0086', detail: 'Batch job completed: 2098/2104 records succeeded', severity: 'Info' }
  ];

  getBadgeClass(severity: string): string {
    switch (severity) {
      case 'Info': return 'badge badge-gray';
      case 'Warning': return 'badge badge-warning';
      case 'Critical': return 'badge badge-danger';
      default: return 'badge badge-gray';
    }
  }
}
