import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-eob',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './eob.component.html'
})
export class EobComponent {
  statements = [
    { eobId: 'EOB-2026-0312', claimNumber: 'CLM-2026-00139', provider: 'Dr. Sarah Chen, MD', serviceDate: '2026-03-02', billedAmount: '$285.00', planPaid: '$228.00', youOwe: '$57.00', issueDate: '2026-03-08' },
    { eobId: 'EOB-2026-0245', claimNumber: 'CLM-2026-00115', provider: 'Sunrise Pharmacy', serviceDate: '2026-02-18', billedAmount: '$142.50', planPaid: '$114.00', youOwe: '$28.50', issueDate: '2026-02-25' },
    { eobId: 'EOB-2026-0198', claimNumber: 'CLM-2026-00084', provider: 'City Lab Services', serviceDate: '2026-01-28', billedAmount: '$475.00', planPaid: '$380.00', youOwe: '$95.00', issueDate: '2026-02-05' },
    { eobId: 'EOB-2026-0142', claimNumber: 'CLM-2025-00971', provider: 'Family Medical Group', serviceDate: '2026-01-15', billedAmount: '$195.00', planPaid: '$156.00', youOwe: '$39.00', issueDate: '2026-01-22' }
  ];
}
