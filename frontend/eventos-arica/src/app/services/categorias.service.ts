import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CategoriaDTO } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class CategoriasService {
    private apiUrl = `${environment.apiUrl}/categoria`;

    constructor(private http: HttpClient) { }


    getCategorias(): Observable<CategoriaDTO[]> {
        return this.http.get<CategoriaDTO[]>(`${this.apiUrl}/all`);
    }


    getCategoriaById(id: number): Observable<CategoriaDTO> {
        return this.http.get<CategoriaDTO>(`${this.apiUrl}/${id}`);
    }


    createCategoria(categoria: CategoriaDTO): Observable<CategoriaDTO> {
        return this.http.post<CategoriaDTO>(`${this.apiUrl}/create`, categoria);
    }


    updateCategoria(id: number, categoria: CategoriaDTO): Observable<CategoriaDTO> {
        return this.http.put<CategoriaDTO>(`${this.apiUrl}/edit/${id}`, categoria);
    }


    deleteCategoria(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${id}`);
    }
}