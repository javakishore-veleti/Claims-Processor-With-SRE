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
  private directUrl = 'http://localhost:8086/api/v1/tenants';

  constructor(private http: HttpClient) {}

  getTenants(): Observable<Tenant[]> {
    return this.http.get<Tenant[]>(`${this.baseUrl}/tenants`).pipe(
      catchError(() =>
        this.http.get<Tenant[]>(this.directUrl).pipe(
          catchError(() => of(this.getMockTenants()))
        )
      )
    );
  }

  createTenant(tenant: Partial<Tenant>): Observable<Tenant> {
    return this.http.post<Tenant>(`${this.baseUrl}/tenants`, tenant).pipe(
      catchError(() => of({ ...this.getMockTenants()[0], ...tenant } as Tenant))
    );
  }

  private getMockTenants(): Tenant[] {
    return [
      {
        tenantId: 'TNT-001',
        name: 'Acme Healthcare',
        domain: 'acme.health.io',
        plan: 'Enterprise',
        planClass: 'info',
        status: 'Active',
        statusClass: 'success',
        users: 245,
        createdDate: 'Jan 15, 2025'
      },
      {
        tenantId: 'TNT-002',
        name: 'Blue Cross Regional',
        domain: 'bluecross.health.io',
        plan: 'Enterprise',
        planClass: 'info',
        status: 'Active',
        statusClass: 'success',
        users: 189,
        createdDate: 'Feb 1, 2025'
      },
      {
        tenantId: 'TNT-003',
        name: 'Sunrise Medical',
        domain: 'sunrise.health.io',
        plan: 'Professional',
        planClass: 'primary',
        status: 'Active',
        statusClass: 'success',
        users: 67,
        createdDate: 'Mar 10, 2025'
      },
      {
        tenantId: 'TNT-004',
        name: 'Valley Health Network',
        domain: 'valley.health.io',
        plan: 'Trial',
        planClass: 'warning',
        status: 'Trial',
        statusClass: 'warning',
        users: 12,
        createdDate: 'Feb 28, 2026'
      },
      {
        tenantId: 'TNT-005',
        name: 'MedFirst Clinic',
        domain: 'medfirst.health.io',
        plan: 'Starter',
        planClass: 'gray',
        status: 'Suspended',
        statusClass: 'danger',
        users: 8,
        createdDate: 'Dec 5, 2025'
      },
      {
        tenantId: 'TNT-006',
        name: 'Pacific Health Group',
        domain: 'pacific.health.io',
        plan: 'Professional',
        planClass: 'primary',
        status: 'Active',
        statusClass: 'success',
        users: 94,
        createdDate: 'Jul 20, 2025'
      }
    ];
  }
}
