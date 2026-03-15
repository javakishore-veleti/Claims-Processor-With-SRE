import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users.component.html'
})
export class UsersComponent {
  users = [
    { userId: 'USR-001', name: 'Alice Johnson', email: 'alice.johnson@acmehealth.com', role: 'Claims Admin', group: 'Claims Team', status: 'Active', lastLogin: '2026-03-14 08:12 AM' },
    { userId: 'USR-002', name: 'Bob Martinez', email: 'bob.martinez@acmehealth.com', role: 'SRE Engineer', group: 'Platform Ops', status: 'Active', lastLogin: '2026-03-14 07:45 AM' },
    { userId: 'USR-003', name: 'Carol Wei', email: 'carol.wei@blueridge.org', role: 'Claims Reviewer', group: 'Claims Team', status: 'Active', lastLogin: '2026-03-13 04:30 PM' },
    { userId: 'USR-004', name: 'David Park', email: 'david.park@pacificwell.com', role: 'Tenant Admin', group: 'Administration', status: 'Active', lastLogin: '2026-03-12 11:20 AM' },
    { userId: 'USR-005', name: 'Emily Davis', email: 'emily.davis@acmehealth.com', role: 'Batch Operator', group: 'Data Operations', status: 'Locked', lastLogin: '2026-02-28 03:15 PM' },
    { userId: 'USR-006', name: 'Frank Okafor', email: 'frank.okafor@blueridge.org', role: 'Read-Only', group: 'Auditors', status: 'Active', lastLogin: '2026-03-11 09:00 AM' }
  ];

  getBadgeClass(status: string): string {
    return status === 'Active' ? 'badge badge-success' : 'badge badge-danger';
  }
}
