import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface User {
  username: string;
  name: string;
  email: string;
  status: string;
  statusClass: string;
  groups: string;
  lastLogin: string;
}

export interface Role {
  name: string;
  type: string;
  typeClass: string;
  usersAssigned: number;
  privileges: string;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = '/api/v1/entitlement-mgmt';
  private directUrl = 'http://localhost:8087/api/v1/entitlements';

  constructor(private http: HttpClient) {}

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.baseUrl}/users`).pipe(
      catchError(() =>
        this.http.get<User[]>(`${this.directUrl}/users`).pipe(
          catchError(() => of(this.getMockUsers()))
        )
      )
    );
  }

  getRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(`${this.baseUrl}/roles`).pipe(
      catchError(() =>
        this.http.get<Role[]>(`${this.directUrl}/roles`).pipe(
          catchError(() => of(this.getMockRoles()))
        )
      )
    );
  }

  getPrivileges(): Observable<any[]> {
    return this.http.get<any[]>(`${this.directUrl}/privileges`).pipe(
      catchError(() => of([]))
    );
  }

  private getMockUsers(): User[] {
    return [
      {
        username: 'jsmith',
        name: 'John Smith',
        email: 'john.smith@acme.com',
        status: 'Active',
        statusClass: 'success',
        groups: 'Admin, Claims-Ops',
        lastLogin: 'Mar 14, 2026 09:12 AM'
      },
      {
        username: 'mjohnson',
        name: 'Maria Johnson',
        email: 'maria.j@acme.com',
        status: 'Active',
        statusClass: 'success',
        groups: 'Claims-Reviewer',
        lastLogin: 'Mar 14, 2026 08:45 AM'
      },
      {
        username: 'rwilliams',
        name: 'Robert Williams',
        email: 'r.williams@acme.com',
        status: 'Active',
        statusClass: 'success',
        groups: 'Claims-Ops, Audit',
        lastLogin: 'Mar 13, 2026 04:30 PM'
      },
      {
        username: 'lchen',
        name: 'Lisa Chen',
        email: 'l.chen@acme.com',
        status: 'Locked',
        statusClass: 'warning',
        groups: 'Claims-Reviewer',
        lastLogin: 'Mar 10, 2026 02:15 PM'
      },
      {
        username: 'dbrown',
        name: 'David Brown',
        email: 'd.brown@acme.com',
        status: 'Disabled',
        statusClass: 'danger',
        groups: '--',
        lastLogin: 'Feb 28, 2026 11:00 AM'
      },
      {
        username: 'kpatel',
        name: 'Kavita Patel',
        email: 'k.patel@acme.com',
        status: 'Active',
        statusClass: 'success',
        groups: 'Admin, SRE',
        lastLogin: 'Mar 14, 2026 10:05 AM'
      }
    ];
  }

  private getMockRoles(): Role[] {
    return [
      { name: 'Super Admin', type: 'System', typeClass: 'gray', usersAssigned: 3, privileges: 'All (142)' },
      { name: 'Claims Administrator', type: 'System', typeClass: 'gray', usersAssigned: 12, privileges: '45' },
      { name: 'Claims Reviewer', type: 'System', typeClass: 'gray', usersAssigned: 28, privileges: '22' },
      { name: 'Custom Auditor', type: 'Custom', typeClass: 'info', usersAssigned: 5, privileges: '18' }
    ];
  }
}
