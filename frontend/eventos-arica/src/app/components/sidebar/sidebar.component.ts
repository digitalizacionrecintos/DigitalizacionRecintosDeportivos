import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface MenuItem {
  label: string;
  route: string;
  icon: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styles: []
})
export class SidebarComponent {
  userEmail = '';

  menuItems: MenuItem[] = [
    { label: 'Eventos', route: '/eventos', icon: '' },
    { label: 'Cursos', route: '/cursos', icon: '' },
    { label: 'Estadísticas', route: '/estadisticas', icon: '' },
    { label: 'Recintos', route: '/recintos', icon: '' },
    { label: 'Encargados', route: '/encargados', icon: '' },
    { label: 'Categorías', route: '/categorias', icon: '' }
  ];

  constructor(private authService: AuthService) {

    this.userEmail = this.authService.getUserEmail() || 'Usuario';
  }

  logout(): void {
    if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
      this.authService.logout();
    }
  }
}