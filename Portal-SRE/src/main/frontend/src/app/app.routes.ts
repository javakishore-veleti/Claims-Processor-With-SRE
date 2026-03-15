import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'service-health',
    loadComponent: () => import('./pages/service-health/service-health.component').then(m => m.ServiceHealthComponent)
  },
  {
    path: 'incidents',
    loadComponent: () => import('./pages/incidents/incidents.component').then(m => m.IncidentsComponent)
  },
  {
    path: 'deployments',
    loadComponent: () => import('./pages/deployments/deployments.component').then(m => m.DeploymentsComponent)
  },
  {
    path: 'cloud-resources',
    loadComponent: () => import('./pages/cloud-resources/cloud-resources.component').then(m => m.CloudResourcesComponent)
  },
  {
    path: 'tenant-analytics',
    loadComponent: () => import('./pages/tenant-analytics/tenant-analytics.component').then(m => m.TenantAnalyticsComponent)
  },
  {
    path: 'slo-compliance',
    loadComponent: () => import('./pages/slo-compliance/slo-compliance.component').then(m => m.SloComplianceComponent)
  },
  {
    path: 'cost-tracking',
    loadComponent: () => import('./pages/cost-tracking/cost-tracking.component').then(m => m.CostTrackingComponent)
  }
];
