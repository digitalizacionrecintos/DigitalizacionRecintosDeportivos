import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { EncargadoDTO, RegistroUsuarioDTO, UpdateEncargadoDTO, ResponseUsuarioDTO, Usuario } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class EncargadosService {
    private apiUrl = `${environment.apiUrl}/encargados`;

    constructor(private http: HttpClient) { }


    getEncargados(): Observable<Usuario[]> {
        return this.http.get<Usuario[]>(this.apiUrl);
    }


    getEncargado(id: number): Observable<ResponseUsuarioDTO> {
        return this.http.get<ResponseUsuarioDTO>(`${this.apiUrl}/${id}`);
    }


    createEncargado(encargado: RegistroUsuarioDTO): Observable<ResponseUsuarioDTO> {
        return this.http.post<ResponseUsuarioDTO>(this.apiUrl, encargado);
    }


    updateEncargado(id: number, encargado: UpdateEncargadoDTO): Observable<ResponseUsuarioDTO> {
        return this.http.put<ResponseUsuarioDTO>(`${this.apiUrl}/${id}`, encargado);
    }


    deleteEncargado(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${id}`, {
            responseType: 'text'
        });
    }


    getEncargadosDisponibles(): Observable<EncargadoDTO[]> {

        return this.http.get<EncargadoDTO[]>(`${environment.apiUrl}/user/managers/available`);
    }
}