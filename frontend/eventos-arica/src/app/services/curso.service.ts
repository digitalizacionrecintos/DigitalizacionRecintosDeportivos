import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Curso } from '../models/curso';

@Injectable({
    providedIn: 'root'
})
export class CursoService {
    private apiUrl = `${environment.apiUrl}/curso`;

    constructor(private http: HttpClient) { }

    listarCursos(): Observable<Curso[]> {
        return this.http.get<Curso[]>(`${this.apiUrl}/all`);
    }

    crearCurso(curso: Curso): Observable<Curso> {
        return this.http.post<Curso>(`${this.apiUrl}/create`, curso);
    }

    obtenerCurso(id: number): Observable<Curso> {
        return this.http.get<Curso>(`${this.apiUrl}/${id}`);
    }

    editarCurso(id: number, curso: Curso): Observable<Curso> {
        return this.http.put<Curso>(`${this.apiUrl}/edit/${id}`, curso);
    }

    eliminarCurso(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    publicarCurso(id: number): Observable<Curso> {
        return this.http.post<Curso>(`${this.apiUrl}/${id}/publicar`, {});
    }

    cancelarCurso(id: number): Observable<Curso> {
        return this.http.post<Curso>(`${this.apiUrl}/${id}/cancelar`, {});
    }
}
