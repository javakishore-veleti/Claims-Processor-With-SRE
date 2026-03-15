import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-claim-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './claim-detail.component.html'
})
export class ClaimDetailComponent {
  claim = {
    claimNumber: 'CLM-2026-00147',
    dateSubmitted: '2026-03-10',
    provider: 'Metro General Hospital',
    type: 'Inpatient',
    billedAmount: '$12,450.00',
    allowedAmount: '$9,800.00',
    memberResponsibility: '$1,960.00',
    stage: 'In Review',
    patientName: 'John Doe',
    memberId: 'MEM-88421',
    diagnosisCodes: 'M54.5 - Low back pain',
    procedureCodes: '99213 - Office visit, 72148 - MRI lumbar spine',
    dateOfService: '2026-03-08',
    placeOfService: 'Inpatient Hospital'
  };

  timeline = [
    { date: '2026-03-10 09:14 AM', stage: 'INTAKE_RECEIVED', description: 'Claim submitted by provider' },
    { date: '2026-03-10 09:15 AM', stage: 'DOCUMENT_VERIFICATION', description: 'Documents verified for completeness' },
    { date: '2026-03-10 09:22 AM', stage: 'DATA_EXTRACTION', description: 'AI extracting structured data from claim form' },
    { date: '2026-03-10 10:05 AM', stage: 'EXTRACTION_REVIEW', description: 'Staff reviewing extracted data' }
  ];
}
