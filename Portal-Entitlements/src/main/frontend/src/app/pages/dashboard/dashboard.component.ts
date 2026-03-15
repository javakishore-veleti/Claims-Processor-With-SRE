import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService, User, Role } from '../../services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  users: User[] = [];
  roles: Role[] = [];
  totalUsers = 0;
  activeUsers = 0;
  totalGroups = 0;
  totalRoles = 0;
  loading = true;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.apiService.getUsers().subscribe({
      next: (response: any) => {
        const data = Array.isArray(response) ? response : (response?.data || response?.content || []);
        const users = Array.isArray(data) ? data : [];
        this.users = users;
        this.totalUsers = users.length;
        this.activeUsers = users.filter((u: any) => u.status === 'Active').length;
        // Count unique groups
        const groups = new Set<string>();
        users.forEach((u: any) => u.groups.split(', ').filter((g: string) => g !== '--').forEach((g: string) => groups.add(g)));
        this.totalGroups = groups.size;
      },
      error: () => {
        this.users = [];
      }
    });

    this.apiService.getRoles().subscribe({
      next: (response: any) => {
        const data = Array.isArray(response) ? response : (response?.data || response?.content || []);
        const roles = Array.isArray(data) ? data : [];
        this.roles = roles;
        this.totalRoles = roles.length;
        this.loading = false;
      },
      error: () => {
        this.roles = [];
        this.loading = false;
      }
    });
  }
}
