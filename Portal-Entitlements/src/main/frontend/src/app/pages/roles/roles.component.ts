import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-roles',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './roles.component.html'
})
export class RolesComponent {
  roles = [
    { roleId: 'ROLE-001', name: 'Super Admin', description: 'Full platform access', privileges: 42, assignedUsers: 2, scope: 'Global' },
    { roleId: 'ROLE-002', name: 'Tenant Admin', description: 'Manage tenant configuration and users', privileges: 28, assignedUsers: 5, scope: 'Tenant' },
    { roleId: 'ROLE-003', name: 'Claims Admin', description: 'Full claims management', privileges: 18, assignedUsers: 12, scope: 'Tenant' },
    { roleId: 'ROLE-004', name: 'Claims Reviewer', description: 'Review and advance claim stages', privileges: 12, assignedUsers: 14, scope: 'Tenant' },
    { roleId: 'ROLE-005', name: 'SRE Engineer', description: 'Platform monitoring and incident response', privileges: 22, assignedUsers: 8, scope: 'Global' },
    { roleId: 'ROLE-006', name: 'Batch Operator', description: 'Execute and monitor batch jobs', privileges: 8, assignedUsers: 6, scope: 'Tenant' },
    { roleId: 'ROLE-007', name: 'Read-Only', description: 'View-only access for auditing', privileges: 5, assignedUsers: 3, scope: 'Tenant' }
  ];
}
