import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'intake',
    loadComponent: () => import('./pages/intake/intake.component').then(m => m.IntakeComponent)
  },
  {
    path: 'claims',
    loadComponent: () => import('./pages/claims/claims.component').then(m => m.ClaimsComponent)
  },
  {
    path: 'members',
    loadComponent: () => import('./pages/members/members.component').then(m => m.MembersComponent)
  },
  {
    path: 'adjudication',
    loadComponent: () => import('./pages/adjudication/adjudication.component').then(m => m.AdjudicationComponent)
  },
  {
    path: 'extraction-review',
    loadComponent: () => import('./pages/extraction-review/extraction-review.component').then(m => m.ExtractionReviewComponent)
  },
  {
    path: 'reports',
    loadComponent: () => import('./pages/reports/reports.component').then(m => m.ReportsComponent)
  },
  {
    path: 'audit',
    loadComponent: () => import('./pages/audit/audit.component').then(m => m.AuditComponent)
  },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full'
  }
];
