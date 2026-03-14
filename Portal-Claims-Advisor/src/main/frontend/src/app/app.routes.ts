import { Routes } from '@angular/router';

export const routes: Routes = [
  // Lazy loaded routes placeholder
  // {
  //   path: 'claims',
  //   loadComponent: () => import('./features/claims/claims.component').then(m => m.ClaimsComponent)
  // },
  // {
  //   path: 'dashboard',
  //   loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
  // },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full'
  }
];
