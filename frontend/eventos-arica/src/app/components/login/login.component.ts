import { Component, OnInit, ChangeDetectorRef, NgZone, ApplicationRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl:'./login.component.html',
  styles: [`
    :host {
  display: block;
  height: 100vh;
}
`]
})
export class LoginComponent {
  email = '';
  password = '';
  recordarme = false;
  cargando = false;
  errorMensaje = '';
  returnUrl = '/eventos';
  mostrarPassword = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private appRef: ApplicationRef
  ) {

    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/eventos';
  }

  login(event: Event): void {
    event.preventDefault();


    this.errorMensaje = '';


    if (!this.email || !this.password) {
      this.errorMensaje = 'Por favor completa todos los campos';
      return;
    }


    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) {
      this.errorMensaje = 'Por favor ingresa un correo electrónico válido';
      return;
    }


    this.cargando = true;
    this.errorMensaje = '';


    this.authService.login(this.email, this.password, this.recordarme)
      .subscribe({
        next: () => {

          this.cargando = false;
          console.log(' Login exitoso, redirigiendo a:', this.returnUrl);
          this.router.navigate([this.returnUrl]);
        },
        error: (error) => {

          this.errorMensaje = error;
          this.cargando = false;
        }
      });
  }
}