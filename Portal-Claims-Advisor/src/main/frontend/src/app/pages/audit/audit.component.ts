import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-audit',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './audit.component.html'
})
export class AuditComponent {
  auditEntries = [
    { timestamp: '2026-03-14 14:32:18', user: 'admin@claims.io', action: 'STAGE_CHANGE', entity: 'CLM-2026-0142', details: 'INTAKE_RECEIVED → EXTRACTION_REVIEW' },
    { timestamp: '2026-03-14 14:30:05', user: 'system', action: 'AI_EXTRACTION', entity: 'CLM-2026-0142', details: 'Bedrock DA extraction completed — 24 fields, 96.8% confidence' },
    { timestamp: '2026-03-14 14:28:42', user: 'jdoe@claims.io', action: 'DOCUMENT_UPLOAD', entity: 'CLM-2026-0142', details: 'Uploaded CMS-1500 form (PDF, 245KB)' },
    { timestamp: '2026-03-14 14:27:11', user: 'jdoe@claims.io', action: 'CLAIM_CREATED', entity: 'CLM-2026-0142', details: 'New claim for member MBR-100001 (John Smith)' },
    { timestamp: '2026-03-14 13:55:30', user: 'admin@claims.io', action: 'STAGE_CHANGE', entity: 'CLM-2026-0141', details: 'EXTRACTION_REVIEW → ADJUDICATION' },
    { timestamp: '2026-03-14 13:42:18', user: 'system', action: 'AI_ADJUDICATION', entity: 'CLM-2026-0140', details: 'Auto-approved — confidence 99.1%, amount $892.50' },
    { timestamp: '2026-03-14 12:15:03', user: 'admin@claims.io', action: 'MEMBER_LOOKUP', entity: 'MBR-100003', details: 'Searched member Robert Johnson' },
    { timestamp: '2026-03-14 11:48:55', user: 'system', action: 'SETTLEMENT', entity: 'CLM-2026-0138', details: 'Payment initiated — $567.25 to provider Pacific Wellness' },
  ];

  getActionClass(action: string): string {
    const map: Record<string, string> = {
      'CLAIM_CREATED': 'badge-info',
      'DOCUMENT_UPLOAD': 'badge-info',
      'AI_EXTRACTION': 'badge-warning',
      'AI_ADJUDICATION': 'badge-warning',
      'STAGE_CHANGE': 'badge-success',
      'SETTLEMENT': 'badge-success',
      'MEMBER_LOOKUP': 'badge-info',
    };
    return map[action] || 'badge-info';
  }
}
