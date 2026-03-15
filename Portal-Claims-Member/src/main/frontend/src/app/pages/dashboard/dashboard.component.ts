import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService, Claim } from '../../services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  claims: Claim[] = [];
  activeClaims = 0;
  latestStatus = '';
  latestClaimNumber = '';
  pendingActions = 0;
  loading = true;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.apiService.getMyClaims().subscribe({
      next: (response: any) => {
        const data = Array.isArray(response) ? response : (response?.data || response?.content || []);
        const claims = Array.isArray(data) ? data : [];
        this.claims = claims;
        this.activeClaims = claims.filter((c: any) => c.stage !== 'Approved' && c.stage !== 'Denied').length;
        if (claims.length > 0) {
          this.latestStatus = claims[0].stage;
          this.latestClaimNumber = claims[0].claimNumber;
        }
        this.pendingActions = claims.filter((c: any) => c.stage === 'Pending Info').length;
        this.loading = false;
      },
      error: () => {
        this.claims = [];
        this.loading = false;
      }
    });
  }

  getBadgeClass(stage: string): string {
    switch (stage) {
      case 'In Review': return 'badge badge-primary';
      case 'Pending Info': return 'badge badge-warning';
      case 'Approved': return 'badge badge-success';
      case 'Denied': return 'badge badge-danger';
      default: return 'badge badge-gray';
    }
  }
}
