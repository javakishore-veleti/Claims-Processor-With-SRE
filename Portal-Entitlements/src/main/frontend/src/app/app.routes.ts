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
    path: 'users',
    loadComponent: () => import('./pages/users/users.component').then(m => m.UsersComponent)
  },
  {
    path: 'groups',
    loadComponent: () => import('./pages/groups/groups.component').then(m => m.GroupsComponent)
  },
  {
    path: 'roles',
    loadComponent: () => import('./pages/roles/roles.component').then(m => m.RolesComponent)
  },
  {
    path: 'privileges',
    loadComponent: () => import('./pages/privileges/privileges.component').then(m => m.PrivilegesComponent)
  },
  {
    path: 'audit',
    loadComponent: () => import('./pages/audit/audit.component').then(m => m.AuditComponent)
  }
];
