import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface Tenant {
  tenantId: string;
  name: string;
  domain: string;
  plan: string;
  planClass: string;
  status: string;
  statusClass: string;
  users: number;
  createdDate: string;
}

export interface TenantStats {
  total: number;
  active: number;
  trial: number;
  suspended: number;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = '/api/v1/tenant-mgmt';

  constructor(private http: HttpClient) {}

  getTenants(): Observable<any> {
    return this.http.get(`${this.baseUrl}/tenants`).pipe(
      catchError(() => of({ data: [] }))
    );
  }

  createTenant(tenant: Partial<Tenant>): Observable<any> {
    return this.http.post(`${this.baseUrl}/tenants`, tenant).pipe(
      catchError(() => of({ data: null }))
    );
  }
}
