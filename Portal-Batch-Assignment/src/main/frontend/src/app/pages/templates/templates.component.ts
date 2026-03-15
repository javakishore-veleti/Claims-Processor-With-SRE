import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-templates',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './templates.component.html'
})
export class TemplatesComponent {
  templates = [
    { name: 'Standard Member Enrollment', format: 'Excel (.xlsx)', columns: 18, lastUpdated: '2026-02-15', downloads: 342 },
    { name: 'Provider Network Update', format: 'CSV (.csv)', columns: 12, lastUpdated: '2026-01-20', downloads: 128 },
    { name: 'EDI 834 Enrollment', format: 'EDI X12 834', columns: 24, lastUpdated: '2026-03-01', downloads: 89 },
    { name: 'Member Termination', format: 'Excel (.xlsx)', columns: 8, lastUpdated: '2026-02-28', downloads: 67 },
    { name: 'Dependent Add/Remove', format: 'CSV (.csv)', columns: 14, lastUpdated: '2026-01-10', downloads: 156 }
  ];
}
