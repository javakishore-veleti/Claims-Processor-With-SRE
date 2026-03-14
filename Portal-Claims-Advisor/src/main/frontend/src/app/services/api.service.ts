import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = '/api/v1/advisor';

  constructor(private http: HttpClient) {}

  // Dashboard
  getDashboard(tenantId: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/dashboard`, {
      headers: { 'X-Tenant-Id': tenantId }
    });
  }

  // Claims
  getClaims(page = 0, size = 20): Observable<any> {
    return this.http.get(`${this.baseUrl}/claims/search`, {
      params: new HttpParams().set('page', page).set('size', size)
    });
  }

  getClaimFull(id: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/claims/${id}/full`);
  }

  intakeClaim(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/claims/intake`, data);
  }

  // Members
  searchMembers(query: string): Observable<any> {
    return this.http.get('/api/v1/members', {
      params: new HttpParams().set('firstName', query)
    });
  }

  // Direct API calls (for when orchestration endpoints aren't available yet)
  getClaimsDirect(page = 0, size = 20): Observable<any> {
    return this.http.get('http://localhost:8083/api/v1/claims', {
      params: new HttpParams().set('page', page).set('size', size)
    });
  }

  getMembersDirect(): Observable<any> {
    return this.http.get('http://localhost:8084/api/v1/members');
  }

  getTenantsDirect(): Observable<any> {
    return this.http.get('http://localhost:8086/api/v1/tenants/active');
  }
}
