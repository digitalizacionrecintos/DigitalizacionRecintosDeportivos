import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { InscripcionDTO, InscripcionDetalleDTO } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class InscripcionesService {
    private apiUrl = `${environment.apiUrl}/inscripcion`;

    constructor(private http: HttpClient) { }


    registrar(inscripcion: InscripcionDTO): Observable<any> {
        return this.http.post(`${this.apiUrl}/register`, inscripcion);
    }


    getInscripcionesPorEvento(eventoId: number): Observable<InscripcionDetalleDTO[]> {
        return this.http.get<InscripcionDetalleDTO[]>(`${this.apiUrl}/evento/${eventoId}`);
    }
}