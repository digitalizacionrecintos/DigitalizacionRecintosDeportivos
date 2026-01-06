import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CrearEventoDTO, EditarEventoDTO, EventoDTO } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class EventosService {
    private apiUrl = `${environment.apiUrl}/event`;

    constructor(private http: HttpClient) { }


    getEventos(): Observable<EventoDTO[]> {
        return this.http.get<EventoDTO[]>(`${this.apiUrl}/all`);
    }


    getEventoById(id: number): Observable<EventoDTO> {
        return this.http.get<EventoDTO>(`${this.apiUrl}/${id}`);
    }


    getEventosEnProgreso(): Observable<EventoDTO[]> {
        return this.http.get<EventoDTO[]>(`${this.apiUrl}/in-progress`);
    }


    createEvento(evento: CrearEventoDTO): Observable<EventoDTO> {
        return this.http.post<EventoDTO>(`${this.apiUrl}/create`, evento);
    }


    updateEvento(id: number, evento: EditarEventoDTO): Observable<EventoDTO> {
        return this.http.put<EventoDTO>(`${this.apiUrl}/edit/${id}`, evento);
    }


    cambiarEstado(id: number, estado: string): Observable<any> {
        return this.http.put(`${this.apiUrl}/change-status/${id}`, estado, {
            headers: { 'Content-Type': 'text/plain' }
        });
    }


    deleteEvento(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${id}`);
    }
}