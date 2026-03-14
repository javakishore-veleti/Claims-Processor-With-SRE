import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService, BatchJob, BatchStats } from '../../services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  jobs: BatchJob[] = [];
  stats: BatchStats = { totalImportsToday: 0, membersCreated: 0, jobsRunning: 0, failedJobs: 0 };
  loading = true;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.apiService.getStats().subscribe(stats => {
      this.stats = stats;
    });
    this.apiService.getJobs().subscribe(jobs => {
      this.jobs = jobs;
      this.loading = false;
    });
  }

  getProgressBarClass(status: string): string {
    switch (status) {
      case 'Failed': return 'progress-fill danger';
      case 'Completed': return 'progress-fill success';
      default: return 'progress-fill primary';
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.apiService.importMembers(input.files[0]).subscribe(() => {
        this.apiService.getJobs().subscribe(jobs => this.jobs = jobs);
      });
    }
  }
}
