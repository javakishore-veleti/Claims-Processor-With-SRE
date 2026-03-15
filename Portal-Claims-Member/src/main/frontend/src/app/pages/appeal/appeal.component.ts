import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-appeal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './appeal.component.html'
})
export class AppealComponent {
  claimNumber = '';
  reason = '';
  additionalInfo = '';
}
