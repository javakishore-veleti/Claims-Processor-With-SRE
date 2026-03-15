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
    this.api.getClaims().subscribe({
      next: (res) => {
        this.recentClaims = res?.data || [];
        this.loading = false;
      },
      error: () => {
        this.recentClaims = [];
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
