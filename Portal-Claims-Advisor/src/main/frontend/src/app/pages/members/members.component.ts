import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-members',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './members.component.html'
})
export class MembersComponent implements OnInit {
  members: any[] = [];
  searchQuery = '';
  loading = true;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadMembers();
  }

  loadMembers() {
    this.loading = true;
    this.api.getMembersDirect().subscribe({
      next: (res) => {
        this.members = res?.data || [];
        this.loading = false;
      },
      error: () => {
        this.members = [
          { memberId: 'MBR-100001', name: 'John Smith', dob: '1985-04-12', policyNumber: 'POL-2025-4421', status: 'ACTIVE' },
          { memberId: 'MBR-100002', name: 'Jane Doe', dob: '1990-08-23', policyNumber: 'POL-2025-4422', status: 'ACTIVE' },
          { memberId: 'MBR-100003', name: 'Robert Johnson', dob: '1978-01-15', policyNumber: 'POL-2025-4423', status: 'ACTIVE' },
          { memberId: 'MBR-100004', name: 'Maria Garcia', dob: '1995-11-30', policyNumber: 'POL-2025-4424', status: 'INACTIVE' },
          { memberId: 'MBR-100005', name: 'David Wilson', dob: '1982-06-07', policyNumber: 'POL-2025-4425', status: 'ACTIVE' },
          { memberId: 'MBR-100006', name: 'Sarah Brown', dob: '1988-09-19', policyNumber: 'POL-2025-4426', status: 'PENDING' },
        ];
        this.loading = false;
      }
    });
  }

  get filteredMembers() {
    if (!this.searchQuery) return this.members;
    const q = this.searchQuery.toLowerCase();
    return this.members.filter(m =>
      m.memberId?.toLowerCase().includes(q) ||
      m.name?.toLowerCase().includes(q) ||
      m.policyNumber?.toLowerCase().includes(q)
    );
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      'ACTIVE': 'badge-success',
      'INACTIVE': 'badge-danger',
      'PENDING': 'badge-warning',
    };
    return map[status] || 'badge-info';
  }
}
