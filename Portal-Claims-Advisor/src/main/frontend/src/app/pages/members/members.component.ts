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
    this.api.getMembers().subscribe({
      next: (res) => {
        this.members = res?.data || [];
        this.loading = false;
      },
      error: () => {
        this.members = [];
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
