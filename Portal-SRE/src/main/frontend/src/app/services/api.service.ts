import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface ServiceHealth {
  name: string;
  status: string;
  avgResponseTime: string;
  p99ResponseTime: string;
  port?: number;
}

export interface Incident {
  id: string;
  title: string;
  severity: string;
  severityClass: string;
  status: string;
  statusClass: string;
  affectedServices: string;
  duration: string;
}

export interface Deployment {
  service: string;
  version: string;
  environment: string;
  envClass: string;
  status: string;
  statusClass: string;
  deployedBy: string;
  time: string;
}

export interface DashboardStats {
  servicesUp: number;
  totalServices: number;
  activeIncidents: number;
  deploymentsToday: number;
  errorBudgetRemaining: number;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = '/api/v1/sre';

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.baseUrl}/dashboard`).pipe(
      catchError(() => of(this.getMockStats()))
    );
  }

  getServiceHealth(): Observable<ServiceHealth[]> {
    return this.http.get<ServiceHealth[]>(`${this.baseUrl}/service-health/overview`).pipe(
      catchError(() => of(this.getMockServices()))
    );
  }

  getActiveIncidents(): Observable<Incident[]> {
    return this.http.get<Incident[]>(`${this.baseUrl}/incidents/active`).pipe(
      catchError(() => of(this.getMockIncidents()))
    );
  }

  getRecentDeployments(): Observable<Deployment[]> {
    return this.http.get<Deployment[]>(`${this.baseUrl}/deployments/recent`).pipe(
      catchError(() => of(this.getMockDeployments()))
    );
  }

  checkServiceActuator(port: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/service-health/check/${port}`).pipe(
      catchError(() => of({ status: 'DOWN' }))
    );
  }

  private getMockStats(): DashboardStats {
    return {
      servicesUp: 9,
      totalServices: 10,
      activeIncidents: 2,
      deploymentsToday: 7,
      errorBudgetRemaining: 62
    };
  }

  private getMockServices(): ServiceHealth[] {
    return [
      { name: 'Claims API', status: 'UP', avgResponseTime: '45ms', p99ResponseTime: '120ms', port: 8081 },
      { name: 'Member Service', status: 'UP', avgResponseTime: '32ms', p99ResponseTime: '95ms', port: 8082 },
      { name: 'Auth Gateway', status: 'UP', avgResponseTime: '18ms', p99ResponseTime: '52ms', port: 8080 },
      { name: 'Batch Processor', status: 'UP', avgResponseTime: '210ms', p99ResponseTime: '890ms', port: 8085 },
      { name: 'Notification Svc', status: 'DEGRADED', avgResponseTime: '2,400ms', p99ResponseTime: '8,500ms', port: 8091 },
      { name: 'Document Store', status: 'UP', avgResponseTime: '67ms', p99ResponseTime: '240ms', port: 8092 },
      { name: 'Analytics Engine', status: 'UP', avgResponseTime: '150ms', p99ResponseTime: '420ms', port: 8093 },
      { name: 'Entitlements API', status: 'UP', avgResponseTime: '28ms', p99ResponseTime: '78ms', port: 8087 },
      { name: 'Tenant Manager', status: 'UP', avgResponseTime: '55ms', p99ResponseTime: '180ms', port: 8086 },
      { name: 'EOB Generator', status: 'UP', avgResponseTime: '340ms', p99ResponseTime: '1,200ms', port: 8094 }
    ];
  }

  private getMockIncidents(): Incident[] {
    return [
      {
        id: 'INC-0421',
        title: 'Notification service high latency',
        severity: 'P1',
        severityClass: 'danger',
        status: 'Investigating',
        statusClass: 'warning',
        affectedServices: 'Notification Svc, Email Gateway',
        duration: '47 min'
      },
      {
        id: 'INC-0420',
        title: 'Intermittent 502s on Claims API',
        severity: 'P2',
        severityClass: 'warning',
        status: 'Mitigated',
        statusClass: 'primary',
        affectedServices: 'Claims API',
        duration: '2h 15min'
      }
    ];
  }

  private getMockDeployments(): Deployment[] {
    return [
      {
        service: 'Claims API',
        version: 'v3.14.2',
        environment: 'Production',
        envClass: 'success',
        status: 'Healthy',
        statusClass: 'success',
        deployedBy: 'CI/CD Pipeline',
        time: 'Today 11:30 AM'
      },
      {
        service: 'Member Service',
        version: 'v2.8.1',
        environment: 'Production',
        envClass: 'success',
        status: 'Healthy',
        statusClass: 'success',
        deployedBy: 'k.patel',
        time: 'Today 10:15 AM'
      },
      {
        service: 'Analytics Engine',
        version: 'v1.22.0',
        environment: 'Staging',
        envClass: 'primary',
        status: 'Healthy',
        statusClass: 'success',
        deployedBy: 'CI/CD Pipeline',
        time: 'Today 09:42 AM'
      },
      {
        service: 'Auth Gateway',
        version: 'v4.1.0',
        environment: 'Production',
        envClass: 'success',
        status: 'Healthy',
        statusClass: 'success',
        deployedBy: 'j.smith',
        time: 'Today 08:00 AM'
      },
      {
        service: 'Notification Svc',
        version: 'v2.3.5',
        environment: 'Production',
        envClass: 'success',
        status: 'Degraded',
        statusClass: 'danger',
        deployedBy: 'CI/CD Pipeline',
        time: 'Yesterday 04:30 PM'
      },
      {
        service: 'Batch Processor',
        version: 'v1.9.3',
        environment: 'Canary',
        envClass: 'warning',
        status: 'Healthy',
        statusClass: 'success',
        deployedBy: 'r.williams',
        time: 'Yesterday 02:00 PM'
      },
      {
        service: 'Document Store',
        version: 'v3.0.1',
        environment: 'Production',
        envClass: 'success',
        status: 'Healthy',
        statusClass: 'success',
        deployedBy: 'CI/CD Pipeline',
        time: 'Yesterday 11:20 AM'
      }
    ];
  }
}
