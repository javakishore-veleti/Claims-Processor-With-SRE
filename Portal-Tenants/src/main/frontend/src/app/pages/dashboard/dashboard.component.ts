import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService, Tenant } from '../../services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  tenants: Tenant[] = [];
  totalTenants = 0;
  activeTenants = 0;
  trialTenants = 0;
  suspendedTenants = 0;
  loading = true;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.apiService.getTenants().subscribe({
      next: (response: any) => {
        // Handle both ApiResponse wrapper and plain array
        const tenants = Array.isArray(response) ? response : (response?.data || response?.content || []);
        this.tenants = Array.isArray(tenants) ? tenants : [];
        this.totalTenants = this.tenants.length;
        this.activeTenants = this.tenants.filter(t => t.status === 'Active' || t.status === 'ACTIVE').length;
        this.trialTenants = this.tenants.filter(t => t.status === 'Trial' || t.status === 'TRIAL').length;
        this.suspendedTenants = this.tenants.filter(t => t.status === 'Suspended' || t.status === 'SUSPENDED').length;
        this.loading = false;
      },
      error: () => {
        this.tenants = [];
        this.loading = false;
      }
    });
  }
}
