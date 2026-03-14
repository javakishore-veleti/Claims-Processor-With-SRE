import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { TracingService } from './services/tracing.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Tenant Management Portal';
  activeNav = 'dashboard';

  constructor(private tracing: TracingService) {
    this.tracing.init('portal-tenants');
  }

  setActiveNav(nav: string): void {
    this.activeNav = nav;
  }
}
