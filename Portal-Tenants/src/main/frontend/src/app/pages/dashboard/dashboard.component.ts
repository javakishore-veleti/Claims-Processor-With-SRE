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
    this.apiService.getTenants().subscribe(tenants => {
      this.tenants = tenants;
      this.totalTenants = tenants.length;
      this.activeTenants = tenants.filter(t => t.status === 'Active').length;
      this.trialTenants = tenants.filter(t => t.status === 'Trial').length;
      this.suspendedTenants = tenants.filter(t => t.status === 'Suspended').length;
      this.loading = false;
    });
  }
}
