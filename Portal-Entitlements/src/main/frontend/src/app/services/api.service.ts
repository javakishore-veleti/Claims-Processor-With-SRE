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

  constructor(private http: HttpClient) {}

  getUsers(): Observable<any> {
    return this.http.get(`${this.baseUrl}/users`).pipe(
      catchError(() => of({ data: [] }))
    );
  }

  getRoles(): Observable<any> {
    return this.http.get(`${this.baseUrl}/roles`).pipe(
      catchError(() => of({ data: [] }))
    );
  }

  getPrivileges(): Observable<any> {
    return this.http.get(`${this.baseUrl}/privileges`).pipe(
      catchError(() => of({ data: [] }))
    );
  }
}
