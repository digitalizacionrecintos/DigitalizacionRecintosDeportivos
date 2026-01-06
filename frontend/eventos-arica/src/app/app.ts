import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, CommonModule],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-arica-blue via-blue-700 to-cyan-500">
      <app-sidebar *ngIf="mostrarSidebar" />
      <router-outlet />
    </div>
  `,
  styles: []
})
export class AppComponent implements OnInit {
  title = 'eventos-arica';
  mostrarSidebar = false;

  constructor(
    private router: Router,
    private authService: AuthService
  ) {

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {

      this.actualizarSidebar();
    });


    this.authService.currentUser$.subscribe(() => {
      this.actualizarSidebar();
    });
  }

  ngOnInit(): void {
    this.actualizarSidebar();
  }

  private actualizarSidebar(): void {
    const isLoginPage = this.router.url.includes('/login');
    const isAuthenticated = this.authService.isLoggedIn();


    this.mostrarSidebar = isAuthenticated && !isLoginPage;
  }
}