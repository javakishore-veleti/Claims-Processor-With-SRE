import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService, ServiceHealth, Incident, Deployment, DashboardStats } from '../../services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = { servicesUp: 0, totalServices: 0, activeIncidents: 0, deploymentsToday: 0, errorBudgetRemaining: 0 };
  services: ServiceHealth[] = [];
  incidents: Incident[] = [];
  deployments: Deployment[] = [];
  loading = true;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.apiService.getDashboard().subscribe(stats => {
      this.stats = stats;
    });

    this.apiService.getServiceHealth().subscribe(services => {
      this.services = services;
    });

    this.apiService.getActiveIncidents().subscribe(incidents => {
      this.incidents = incidents;
    });

    this.apiService.getRecentDeployments().subscribe(deployments => {
      this.deployments = deployments;
      this.loading = false;
    });
  }

  isServiceUp(service: ServiceHealth): boolean {
    return service.status === 'UP';
  }

  getServiceStatusText(service: ServiceHealth): string {
    return service.status === 'UP' ? 'Operational' : 'Degraded';
  }
}
