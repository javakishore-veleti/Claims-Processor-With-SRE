import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-privileges',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './privileges.component.html'
})
export class PrivilegesComponent {
  privileges = [
    { code: 'CLAIM_CREATE', name: 'Create Claims', module: 'Claims', description: 'Submit new claims into the system', roles: 3 },
    { code: 'CLAIM_READ', name: 'View Claims', module: 'Claims', description: 'View claim details and status', roles: 6 },
    { code: 'CLAIM_UPDATE', name: 'Update Claims', module: 'Claims', description: 'Modify claim data and advance stages', roles: 3 },
    { code: 'CLAIM_DELETE', name: 'Delete Claims', module: 'Claims', description: 'Remove claims from the system', roles: 1 },
    { code: 'MEMBER_CREATE', name: 'Create Members', module: 'Members', description: 'Add new member records', roles: 4 },
    { code: 'MEMBER_READ', name: 'View Members', module: 'Members', description: 'View member profiles and details', roles: 6 },
    { code: 'BATCH_EXECUTE', name: 'Execute Batch Jobs', module: 'Batch', description: 'Run batch import and processing jobs', roles: 2 },
    { code: 'TENANT_MANAGE', name: 'Manage Tenants', module: 'Tenants', description: 'Create and configure tenants', roles: 2 },
    { code: 'USER_MANAGE', name: 'Manage Users', module: 'Entitlements', description: 'Create, update, and deactivate users', roles: 2 },
    { code: 'AUDIT_READ', name: 'View Audit Logs', module: 'Audit', description: 'Access audit trail records', roles: 3 }
  ];
}
