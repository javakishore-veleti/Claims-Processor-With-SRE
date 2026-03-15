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
    path: 'my-claims',
    loadComponent: () => import('./pages/my-claims/my-claims.component').then(m => m.MyClaimsComponent)
  },
  {
    path: 'claim-detail',
    loadComponent: () => import('./pages/claim-detail/claim-detail.component').then(m => m.ClaimDetailComponent)
  },
  {
    path: 'documents',
    loadComponent: () => import('./pages/documents/documents.component').then(m => m.DocumentsComponent)
  },
  {
    path: 'eob',
    loadComponent: () => import('./pages/eob/eob.component').then(m => m.EobComponent)
  },
  {
    path: 'appeal',
    loadComponent: () => import('./pages/appeal/appeal.component').then(m => m.AppealComponent)
  },
  {
    path: 'profile',
    loadComponent: () => import('./pages/profile/profile.component').then(m => m.ProfileComponent)
  }
];
