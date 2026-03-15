import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-claims',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './claims.component.html'
})
export class ClaimsComponent implements OnInit {
  claims: any[] = [];
  searchQuery = '';
  loading = true;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadClaims();
  }

  loadClaims() {
    this.loading = true;
    this.api.getClaims().subscribe({
      next: (res) => {
        this.claims = res?.data || [];
        this.loading = false;
      },
      error: () => {
        this.claims = [];
        this.loading = false;
      }
    });
  }

  get filteredClaims() {
    if (!this.searchQuery) return this.claims;
    const q = this.searchQuery.toLowerCase();
    return this.claims.filter(c =>
      c.claimNumber?.toLowerCase().includes(q) ||
      c.memberName?.toLowerCase().includes(q) ||
      c.stage?.toLowerCase().includes(q)
    );
  }

  getStageClass(stage: string): string {
    const map: Record<string, string> = {
      'APPROVED': 'badge-success', 'SETTLEMENT': 'badge-success', 'CLOSED': 'badge-success',
      'DENIED': 'badge-danger',
      'INTAKE_RECEIVED': 'badge-info', 'DATA_EXTRACTION': 'badge-info', 'ADJUDICATION': 'badge-info',
      'EXTRACTION_REVIEW': 'badge-warning', 'ADJUDICATION_REVIEW': 'badge-warning', 'ELIGIBILITY_CHECK': 'badge-warning',
    };
    return map[stage] || 'badge-info';
  }
}
