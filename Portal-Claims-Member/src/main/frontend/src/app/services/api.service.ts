import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface Claim {
  id: string;
  claimNumber: string;
  stage: string;
  amount: number;
  filedDate: string;
  lastUpdated: string;
  status: string;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = '/api/v1/member';

  constructor(private http: HttpClient) {}

  getMyClaims(): Observable<Claim[]> {
    return this.http.get<Claim[]>(`${this.baseUrl}/my-claims`).pipe(
      catchError(() => of(this.getMockClaims()))
    );
  }

  getClaimById(id: string): Observable<Claim> {
    return this.http.get<Claim>(`${this.baseUrl}/claims/${id}`).pipe(
      catchError(() => of(this.getMockClaims().find(c => c.id === id) || this.getMockClaims()[0]))
    );
  }

  private getMockClaims(): Claim[] {
    return [
      {
        id: '1',
        claimNumber: 'CLM-2024-0892',
        stage: 'In Review',
        amount: 2450.00,
        filedDate: 'Mar 8, 2026',
        lastUpdated: 'Mar 12, 2026',
        status: 'primary'
      },
      {
        id: '2',
        claimNumber: 'CLM-2024-0876',
        stage: 'Pending Info',
        amount: 890.00,
        filedDate: 'Mar 2, 2026',
        lastUpdated: 'Mar 10, 2026',
        status: 'warning'
      },
      {
        id: '3',
        claimNumber: 'CLM-2024-0854',
        stage: 'Approved',
        amount: 1240.00,
        filedDate: 'Feb 20, 2026',
        lastUpdated: 'Mar 5, 2026',
        status: 'success'
      },
      {
        id: '4',
        claimNumber: 'CLM-2024-0831',
        stage: 'Approved',
        amount: 560.00,
        filedDate: 'Feb 10, 2026',
        lastUpdated: 'Feb 28, 2026',
        status: 'success'
      }
    ];
  }
}
