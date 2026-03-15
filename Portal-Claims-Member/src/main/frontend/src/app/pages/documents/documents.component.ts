import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './documents.component.html'
})
export class DocumentsComponent {
  documents = [
    { name: 'CMS-1500_Claim_Form.pdf', claimNumber: 'CLM-2026-00147', type: 'Claim Form', uploadDate: '2026-03-10', size: '245 KB', status: 'Processed' },
    { name: 'Lab_Results_Feb2026.pdf', claimNumber: 'CLM-2026-00128', type: 'Lab Report', uploadDate: '2026-02-28', size: '1.2 MB', status: 'Processed' },
    { name: 'Prescription_Receipt.jpg', claimNumber: 'CLM-2026-00115', type: 'Receipt', uploadDate: '2026-02-20', size: '890 KB', status: 'Processed' },
    { name: 'Itemized_Bill_Regional_Ortho.pdf', claimNumber: 'CLM-2026-00098', type: 'Itemized Bill', uploadDate: '2026-02-12', size: '312 KB', status: 'Processed' },
    { name: 'EOB_Statement_Jan2026.pdf', claimNumber: 'CLM-2026-00084', type: 'EOB', uploadDate: '2026-02-01', size: '156 KB', status: 'Processed' }
  ];

  getBadgeClass(status: string): string {
    return status === 'Processed' ? 'badge badge-success' : 'badge badge-warning';
  }
}
