import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
    {
        path: '',
        redirectTo: '/login',
        pathMatch: 'full'
    },
    {
        path: 'login',
        loadComponent: () => import('./components/login/login.component')
            .then(m => m.LoginComponent)
    },
    {
        path: 'eventos',
        loadComponent: () => import('./components/eventos-list/eventos-list.component')
            .then(m => m.EventosListComponent),
        canActivate: [authGuard]
    },

    {
        path: 'eventos/:id/editar',
        loadComponent: () => import('./components/editar-evento/editar-evento.component')
            .then(m => m.EditarEventoComponent),
        canActivate: [authGuard]
    },
    {
        path: 'crear',
        loadComponent: () => import('./components/crear-evento/crea-evento.component')
            .then(m => m.CrearEventoComponent),
        canActivate: [authGuard]
    },
    {
        path: 'estadisticas',
        loadComponent: () => import('./components/estadisticas/estadistica.component')
            .then(m => m.EstadisticasComponent),
        canActivate: [authGuard]
    },
    {
        path: 'recintos',
        loadComponent: () => import('./components/recintos/recintos.component')
            .then(m => m.RecintosComponent),
        canActivate: [authGuard]
    },
    {
        path: 'encargados',
        loadComponent: () => import('./components/encargados/encargados.component')
            .then(m => m.EncargadosComponent),
        canActivate: [authGuard]
    },
    {
        path: 'categorias',
        loadComponent: () => import('./components/categorias/categorias.component')
            .then(m => m.CategoriasComponent),
        canActivate: [authGuard]
    },
    {
        path: 'cursos',
        loadComponent: () => import('./components/cursos/cursos.component')
            .then(m => m.CursosComponent),
        canActivate: [authGuard]
    }
];