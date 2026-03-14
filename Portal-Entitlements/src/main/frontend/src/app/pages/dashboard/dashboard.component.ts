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
    this.apiService.getUsers().subscribe(users => {
      this.users = users;
      this.totalUsers = users.length;
      this.activeUsers = users.filter(u => u.status === 'Active').length;
      // Count unique groups
      const groups = new Set<string>();
      users.forEach(u => u.groups.split(', ').filter(g => g !== '--').forEach(g => groups.add(g)));
      this.totalGroups = groups.size;
    });

    this.apiService.getRoles().subscribe(roles => {
      this.roles = roles;
      this.totalRoles = roles.length;
      this.loading = false;
    });
  }
}
