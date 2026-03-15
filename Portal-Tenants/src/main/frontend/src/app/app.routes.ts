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
    path: 'tenants',
    loadComponent: () => import('./pages/tenants/tenants.component').then(m => m.TenantsComponent)
  },
  {
    path: 'create',
    loadComponent: () => import('./pages/create-tenant/create-tenant.component').then(m => m.CreateTenantComponent)
  },
  {
    path: 'plans',
    loadComponent: () => import('./pages/plans/plans.component').then(m => m.PlansComponent)
  },
  {
    path: 'settings',
    loadComponent: () => import('./pages/settings/settings.component').then(m => m.SettingsComponent)
  }
];
