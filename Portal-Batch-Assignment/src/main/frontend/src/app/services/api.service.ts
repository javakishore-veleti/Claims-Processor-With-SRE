import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface BatchJob {
  jobId: string;
  type: string;
  status: string;
  records: number;
  progress: number;
  startedAt: string;
  statusClass: string;
}

export interface BatchStats {
  totalImportsToday: number;
  membersCreated: number;
  jobsRunning: number;
  failedJobs: number;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = '/api/v1/batch';

  constructor(private http: HttpClient) {}

  importMembers(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.baseUrl}/members/import`, formData).pipe(
      catchError(() => of({ status: 'mock', message: 'Import queued (mock)' }))
    );
  }

  getJobs(): Observable<BatchJob[]> {
    return this.http.get<BatchJob[]>(`${this.baseUrl}/jobs`).pipe(
      catchError(() => of(this.getMockJobs()))
    );
  }

  getStats(): Observable<BatchStats> {
    return this.http.get<BatchStats>(`${this.baseUrl}/stats`).pipe(
      catchError(() => of(this.getMockStats()))
    );
  }

  private getMockStats(): BatchStats {
    return {
      totalImportsToday: 14,
      membersCreated: 2847,
      jobsRunning: 3,
      failedJobs: 1
    };
  }

  private getMockJobs(): BatchJob[] {
    return [
      {
        jobId: 'JOB-20260314-001',
        type: 'Member Import',
        status: 'Running',
        records: 1250,
        progress: 72,
        startedAt: '10:24 AM',
        statusClass: 'primary'
      },
      {
        jobId: 'JOB-20260314-002',
        type: 'Claims Batch',
        status: 'Running',
        records: 890,
        progress: 45,
        startedAt: '10:38 AM',
        statusClass: 'primary'
      },
      {
        jobId: 'JOB-20260314-003',
        type: 'EOB Generation',
        status: 'Queued',
        records: 3400,
        progress: 0,
        startedAt: '--',
        statusClass: 'warning'
      },
      {
        jobId: 'JOB-20260314-004',
        type: 'Member Import',
        status: 'Failed',
        records: 500,
        progress: 23,
        startedAt: '09:15 AM',
        statusClass: 'danger'
      },
      {
        jobId: 'JOB-20260313-018',
        type: 'Claims Batch',
        status: 'Completed',
        records: 2100,
        progress: 100,
        startedAt: 'Yesterday',
        statusClass: 'success'
      }
    ];
  }
}
