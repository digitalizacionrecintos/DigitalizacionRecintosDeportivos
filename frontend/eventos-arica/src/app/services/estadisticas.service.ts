import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { EstadisticaDTO, EstadisticasCursosDTO } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class EstadisticasService {
    private apiUrl = `${environment.apiUrl}/estadisticas`;

    constructor(private http: HttpClient) { }


    getEstadisticasGenerales(anio?: number): Observable<EstadisticaDTO> {
        let url = `${this.apiUrl}/general`;
        if (anio) {
            url += `?anio=${anio}`;
        }
        return this.http.get<EstadisticaDTO>(url);
    }

    getEstadisticasCursos(anio?: number): Observable<EstadisticasCursosDTO> {
        let url = `${this.apiUrl}/cursos`;
        if (anio) {
            url += `?anio=${anio}`;
        }
        return this.http.get<EstadisticasCursosDTO>(url);
    }
}