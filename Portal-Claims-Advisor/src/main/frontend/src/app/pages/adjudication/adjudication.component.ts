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
    this.api.getClaimsDirect().subscribe({
      next: (res) => {
        const all = res?.data || [];
        this.claims = all.filter((c: any) => c.stage === 'ADJUDICATION_REVIEW' || c.stage === 'ADJUDICATION');
        this.loading = false;
      },
      error: () => {
        this.claims = [
          { claimNumber: 'CLM-2026-0141', memberName: 'Jane Doe', confidence: 82.1, aiRecommendation: 'APPROVE', amount: 3780.00, reason: 'All codes valid, within policy limits' },
          { claimNumber: 'CLM-2026-0135', memberName: 'Michael Chen', confidence: 67.4, aiRecommendation: 'PARTIAL_APPROVE', amount: 5200.00, reason: 'Procedure code 99214 exceeds usual and customary' },
          { claimNumber: 'CLM-2026-0133', memberName: 'Emily Taylor', confidence: 45.2, aiRecommendation: 'DENY', amount: 12800.00, reason: 'Pre-authorization not found for inpatient stay' },
          { claimNumber: 'CLM-2026-0130', memberName: 'James Williams', confidence: 91.5, aiRecommendation: 'APPROVE', amount: 450.00, reason: 'Routine office visit, covered under preventive care' },
        ];
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
