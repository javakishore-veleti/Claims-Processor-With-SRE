import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-extraction-review',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './extraction-review.component.html'
})
export class ExtractionReviewComponent implements OnInit {
  claims: any[] = [];
  loading = true;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadExtractionQueue();
  }

  loadExtractionQueue() {
    this.loading = true;
    this.api.getClaims().subscribe({
      next: (res) => {
        const all = res?.data || [];
        this.claims = all.filter((c: any) => c.stage === 'EXTRACTION_REVIEW' || c.stage === 'DATA_EXTRACTION');
        this.loading = false;
      },
      error: () => {
        this.claims = [];
        this.loading = false;
      }
    });
  }

  getConfidenceClass(confidence: number): string {
    if (confidence >= 90) return 'badge-success';
    if (confidence >= 70) return 'badge-warning';
    return 'badge-danger';
  }

  getStatusClass(status: string): string {
    return status === 'COMPLETE' ? 'badge-success' : 'badge-warning';
  }
}
