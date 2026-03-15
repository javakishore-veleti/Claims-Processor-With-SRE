import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.component.html'
})
export class ProfileComponent {
  member = {
    name: 'John Doe',
    memberId: 'MEM-88421',
    email: 'john.doe@email.com',
    phone: '(555) 234-5678',
    dob: '1985-06-15',
    address: '1234 Elm Street, Suite 200, Springfield, IL 62704',
    planName: 'Gold PPO Plan',
    planId: 'PLN-2026-GOLD-PPO',
    groupNumber: 'GRP-44210',
    effectiveDate: '2026-01-01',
    deductible: '$1,500.00',
    deductibleMet: '$475.00',
    outOfPocketMax: '$6,000.00',
    outOfPocketUsed: '$219.50'
  };
}
