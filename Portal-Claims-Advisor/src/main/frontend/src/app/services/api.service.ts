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

  // Claims - via Portal BFF
  getClaims(page = 0, size = 20): Observable<any> {
    return this.http.get(`${this.baseUrl}/claims`, {
      params: new HttpParams().set('page', page).set('size', size)
    });
  }

  searchClaims(query: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/claims/search`, {
      params: new HttpParams().set('query', query)
    });
  }

  getClaimFull(id: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/claims/${id}/full`);
  }

  intakeClaim(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/claims/intake`, data);
  }

  // Members - via Portal BFF
  getMembers(): Observable<any> {
    return this.http.get(`${this.baseUrl}/members`);
  }

  searchMembers(query: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/members`, {
      params: new HttpParams().set('firstName', query)
    });
  }
}
