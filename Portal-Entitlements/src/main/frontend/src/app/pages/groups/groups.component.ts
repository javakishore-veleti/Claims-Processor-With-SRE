import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-groups',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './groups.component.html'
})
export class GroupsComponent {
  groups = [
    { groupId: 'GRP-001', name: 'Claims Team', description: 'Claims processing and review staff', members: 24, roles: 'Claims Admin, Claims Reviewer', createdDate: '2024-06-15' },
    { groupId: 'GRP-002', name: 'Platform Ops', description: 'SRE and DevOps engineers', members: 8, roles: 'SRE Engineer, Platform Admin', createdDate: '2024-06-15' },
    { groupId: 'GRP-003', name: 'Administration', description: 'Tenant and system administrators', members: 5, roles: 'Tenant Admin, Super Admin', createdDate: '2024-06-15' },
    { groupId: 'GRP-004', name: 'Data Operations', description: 'Batch processing and data management', members: 6, roles: 'Batch Operator, Data Analyst', createdDate: '2024-08-22' },
    { groupId: 'GRP-005', name: 'Auditors', description: 'Internal and external audit personnel', members: 3, roles: 'Read-Only', createdDate: '2025-01-10' }
  ];
}
