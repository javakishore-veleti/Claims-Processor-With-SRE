import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-service-health',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './service-health.component.html'
})
export class ServiceHealthComponent {
  services = [
    { name: 'API-Claims', port: 8083, status: 'Healthy', uptime: '99.97%', cpu: '24%', memory: '512 MB', p99Latency: '142ms', errorRate: '0.02%' },
    { name: 'API-Members', port: 8084, status: 'Healthy', uptime: '99.99%', cpu: '18%', memory: '384 MB', p99Latency: '98ms', errorRate: '0.01%' },
    { name: 'API-Tenants', port: 8086, status: 'Healthy', uptime: '99.98%', cpu: '12%', memory: '256 MB', p99Latency: '67ms', errorRate: '0.00%' },
    { name: 'API-Entitlements', port: 8087, status: 'Degraded', uptime: '99.82%', cpu: '78%', memory: '920 MB', p99Latency: '485ms', errorRate: '1.24%' },
    { name: 'Portal-Claims-Advisor', port: 8081, status: 'Healthy', uptime: '99.95%', cpu: '15%', memory: '320 MB', p99Latency: '55ms', errorRate: '0.00%' },
    { name: 'Portal-Claims-Member', port: 8082, status: 'Healthy', uptime: '99.96%', cpu: '14%', memory: '310 MB', p99Latency: '52ms', errorRate: '0.00%' },
    { name: 'PostgreSQL', port: 5432, status: 'Healthy', uptime: '99.99%', cpu: '32%', memory: '2.1 GB', p99Latency: '12ms', errorRate: '0.00%' },
    { name: 'Redis Cache', port: 6379, status: 'Healthy', uptime: '100.00%', cpu: '5%', memory: '128 MB', p99Latency: '2ms', errorRate: '0.00%' },
    { name: 'Kafka Broker', port: 9092, status: 'Healthy', uptime: '99.99%', cpu: '28%', memory: '1.4 GB', p99Latency: '8ms', errorRate: '0.00%' }
  ];

  getBadgeClass(status: string): string {
    switch (status) {
      case 'Healthy': return 'badge badge-success';
      case 'Degraded': return 'badge badge-warning';
      case 'Down': return 'badge badge-danger';
      default: return 'badge badge-gray';
    }
  }
}
