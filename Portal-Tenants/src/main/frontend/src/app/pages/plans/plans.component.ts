import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-plans',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './plans.component.html'
})
export class PlansComponent {
  plans = [
    { planId: 'PLN-STARTER', name: 'Starter', maxMembers: '2,000', priceMonthly: '$499', activeTenants: 12, features: 'Basic claims, Email support' },
    { planId: 'PLN-PRO', name: 'Professional', maxMembers: '10,000', priceMonthly: '$1,499', activeTenants: 28, features: 'AI adjudication, Priority support, Analytics' },
    { planId: 'PLN-ENT', name: 'Enterprise', maxMembers: 'Unlimited', priceMonthly: '$4,999', activeTenants: 15, features: 'Full AI suite, 24/7 support, Custom SLOs, Dedicated infra' }
  ];
}
