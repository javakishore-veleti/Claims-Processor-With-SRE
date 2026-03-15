import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-adjudication',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './adjudication.component.html'
})
export class AdjudicationComponent implements OnInit {
  claims: any[] = [];
  loading = true;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadAdjudicationQueue();
  }

  loadAdjudicationQueue() {
    this.loading = true;
    this.api.getClaims().subscribe({
      next: (res) => {
        const all = res?.data || [];
        this.claims = all.filter((c: any) => c.stage === 'ADJUDICATION_REVIEW' || c.stage === 'ADJUDICATION');
        this.loading = false;
      },
      error: () => {
        this.claims = [];
        this.loading = false;
      }
    });
  }

  getConfidenceClass(confidence: number): string {
    if (confidence >= 90) return 'badge-success';
    if (confidence >= 70) return 'badge-warning';
    return 'badge-danger';
  }

  getRecommendationClass(rec: string): string {
    const map: Record<string, string> = {
      'APPROVE': 'badge-success',
      'PARTIAL_APPROVE': 'badge-warning',
      'DENY': 'badge-danger',
    };
    return map[rec] || 'badge-info';
  }

  approve(claim: any) {
    claim.stage = 'APPROVED';
    this.claims = this.claims.filter(c => c !== claim);
  }

  deny(claim: any) {
    claim.stage = 'DENIED';
    this.claims = this.claims.filter(c => c !== claim);
  }
}
