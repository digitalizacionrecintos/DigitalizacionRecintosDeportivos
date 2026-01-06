import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { timeout, catchError, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { User, LoginUsuarioDTO, UsuarioDTO } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = `${environment.apiUrl}/user`;
    private currentUserSubject: BehaviorSubject<User | null>;
    public currentUser$: Observable<User | null>;
    private sessionTimeout: any;
    private readonly SESSION_DURATION = 30 * 60 * 1000;

    constructor(
        private router: Router,
        private http: HttpClient
    ) {
        const storedUser = this.getUserFromStorage();
        this.currentUserSubject = new BehaviorSubject<User | null>(storedUser);
        this.currentUser$ = this.currentUserSubject.asObservable();

        if (storedUser) {
            this.resetSessionTimeout();
            this.setupActivityListeners();
        }
    }


    login(email: string, password: string, rememberMe: boolean = false): Observable<boolean> {
        const loginData: LoginUsuarioDTO = {
            correo: email,
            contrasena: password
        };

        console.log(' Intentando login...');
        console.log('API URL:', `${this.apiUrl}/login`);


        return this.http.post<UsuarioDTO>(`${this.apiUrl}/login`, loginData).pipe(
            timeout(5000),
            map(response => {
                console.log(' Login exitoso:', response);


                const user: User = {
                    id: response.id,
                    email: response.correo,
                    nombre: response.nombre,
                    apellido: response.apellido
                };


                const storage = rememberMe ? localStorage : sessionStorage;
                storage.setItem('currentUser', JSON.stringify(user));
                storage.setItem('loginTime', new Date().toISOString());


                this.currentUserSubject.next(user);


                this.resetSessionTimeout();
                this.setupActivityListeners();

                return true;
            }),
            catchError(error => {

                let userMessage: string;

                if (error.name === 'TimeoutError') {
                    userMessage = 'El servidor no responde. Por favor, verifica tu conexión e intenta nuevamente.';
                    console.error(' Timeout del servidor (5s)');
                } else if (error.status === 0) {

                    userMessage = 'No se puede conectar al servidor. Verifica que el servidor esté funcionando.';
                    console.error(' Error de red - Servidor no alcanzable');
                } else if (error.status === 401 || error.status === 403) {

                    userMessage = 'Credenciales inválidas. Por favor, verifica tu email y contraseña.';
                    console.error(' Credenciales rechazadas');
                } else if (error.status >= 500) {

                    userMessage = 'Error en el servidor. Por favor, intenta más tarde.';
                    console.error(' Error del servidor:', error.status);
                } else if (error.error?.message) {

                    userMessage = error.error.message;
                    console.error(' Error del backend:', error.error.message);
                } else {

                    userMessage = 'Error al iniciar sesión. Por favor, intenta nuevamente.';
                    console.error(' Error desconocido:', error);
                }

                return throwError(() => userMessage);
            })
        );
    }


    logout(): void {

        localStorage.removeItem('currentUser');
        localStorage.removeItem('loginTime');
        sessionStorage.removeItem('currentUser');
        sessionStorage.removeItem('loginTime');


        if (this.sessionTimeout) {
            clearTimeout(this.sessionTimeout);
        }


        this.removeActivityListeners();


        this.currentUserSubject.next(null);


        this.router.navigate(['/login']);
    }


    isLoggedIn(): boolean {
        return this.currentUserSubject.value !== null;
    }


    getUserEmail(): string | null {
        return this.currentUserSubject.value?.email || null;
    }


    getCurrentUser(): User | null {
        return this.currentUserSubject.value;
    }


    getUserId(): number | null {
        return this.currentUserSubject.value?.id || null;
    }


    private getUserFromStorage(): User | null {

        let userStr = sessionStorage.getItem('currentUser');


        if (!userStr) {
            userStr = localStorage.getItem('currentUser');
        }

        if (userStr) {
            try {
                return JSON.parse(userStr);
            } catch {
                return null;
            }
        }

        return null;
    }


    private resetSessionTimeout(): void {
        if (this.sessionTimeout) {
            clearTimeout(this.sessionTimeout);
        }

        this.sessionTimeout = setTimeout(() => {
            console.log('Sesión expirada por inactividad');
            this.logout();
        }, this.SESSION_DURATION);
    }


    private setupActivityListeners(): void {
        const events = ['mousedown', 'keydown', 'scroll', 'touchstart'];

        events.forEach(event => {
            document.addEventListener(event, this.handleUserActivity);
        });
    }


    private removeActivityListeners(): void {
        const events = ['mousedown', 'keydown', 'scroll', 'touchstart'];

        events.forEach(event => {
            document.removeEventListener(event, this.handleUserActivity);
        });
    }


    private handleUserActivity = (): void => {
        this.resetSessionTimeout();
    }
}