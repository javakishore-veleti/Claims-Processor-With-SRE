import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  stats = { totalClaims: 0, pendingReview: 0, aiSuccessRate: 0, avgProcessingTime: '0m' };
  recentClaims: any[] = [];
  loading = true;
  error = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadDashboard();
    this.loadClaims();
  }

  loadDashboard() {
    this.api.getDashboard('default-tenant').subscribe({
      next: (res) => {
        if (res?.data) this.stats = res.data;
      },
      error: (err) => {
        console.warn('Dashboard API not available, using mock data');
        this.stats = { totalClaims: 142, pendingReview: 28, aiSuccessRate: 98.2, avgProcessingTime: '4.2m' };
      }
    });
  }

  loadClaims() {
    this.api.getClaimsDirect().subscribe({
      next: (res) => {
        this.recentClaims = res?.data || [];
        this.loading = false;
      },
      error: () => {
        // Fallback to mock data if API not available
        this.recentClaims = [
          { claimNumber: 'CLM-2026-0142', memberName: 'John Smith', provider: 'Pacific Wellness', stage: 'EXTRACTION_REVIEW', amount: 1245.00, confidence: 96.8, submittedDate: '2026-03-14' },
          { claimNumber: 'CLM-2026-0141', memberName: 'Jane Doe', provider: 'Summit Care', stage: 'ADJUDICATION', amount: 3780.00, confidence: 82.1, submittedDate: '2026-03-14' },
          { claimNumber: 'CLM-2026-0140', memberName: 'Robert Johnson', provider: 'Horizon Health', stage: 'APPROVED', amount: 892.50, confidence: 99.1, submittedDate: '2026-03-14' },
        ];
        this.loading = false;
      }
    });
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
