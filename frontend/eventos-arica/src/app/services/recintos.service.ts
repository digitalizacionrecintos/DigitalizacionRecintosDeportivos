import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CrearRecintoDTO, EditarRecintoDTO, RecintoDTO } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class RecintosService {
    private apiUrl = `${environment.apiUrl}/recinto`;

    constructor(private http: HttpClient) { }


    getRecintos(): Observable<RecintoDTO[]> {
        return this.http.get<RecintoDTO[]>(`${this.apiUrl}/all`);
    }


    getRecintoById(id: number): Observable<RecintoDTO> {
        return this.http.get<RecintoDTO>(`${this.apiUrl}/${id}`);
    }


    createRecinto(recinto: CrearRecintoDTO): Observable<RecintoDTO> {
        return this.http.post<RecintoDTO>(`${this.apiUrl}/create`, recinto);
    }


    updateRecinto(id: number, recinto: EditarRecintoDTO): Observable<RecintoDTO> {
        return this.http.put<RecintoDTO>(`${this.apiUrl}/edit/${id}`, recinto);
    }


    deleteRecinto(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${id}`);
    }


    getRecintosDisponibles(): Observable<RecintoDTO[]> {
        return this.http.get<RecintoDTO[]>(`${this.apiUrl}/available`);
    }
}