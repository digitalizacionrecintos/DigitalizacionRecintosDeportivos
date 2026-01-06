import { Injectable, OnDestroy } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../environments/environment';
import { Client, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export interface CupoUpdate {
    eventoId: number;
    inscritosActuales: number;
}

@Injectable({
    providedIn: 'root'
})
export class WebsocketService implements OnDestroy {
    private stompClient: Client | null = null;
    private cuposSubject = new Subject<CupoUpdate>();
    private eventSubscriptions = new Map<number, StompSubscription>();
    private connected = false;

    constructor() {
        this.connect();
    }


    getCuposUpdates(): Observable<CupoUpdate> {
        return this.cuposSubject.asObservable();
    }


    private connect(): void {

        const wsUrl = environment.apiUrl.replace('/api', '') + '/ws-recintos';

        console.log(' Conectando a WebSocket STOMP:', wsUrl);

        this.stompClient = new Client({
            webSocketFactory: () => new SockJS(wsUrl) as any,
            debug: (str) => {
                console.log(' STOMP Debug:', str);
            },
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        this.stompClient.onConnect = () => {
            console.log(' WebSocket STOMP conectado');
            this.connected = true;
        };

        this.stompClient.onStompError = (frame) => {
            console.error(' Error STOMP:', frame.headers['message']);
            console.error('Detalles:', frame.body);
        };

        this.stompClient.onDisconnect = () => {
            console.log(' WebSocket STOMP desconectado');
            this.connected = false;
        };

        this.stompClient.activate();
    }


    subscribeToEvent(eventoId: number): void {
        if (!this.stompClient || !this.connected) {
            console.warn(' STOMP client no está conectado aún. Reintentando en 1s...');
            setTimeout(() => this.subscribeToEvent(eventoId), 1000);
            return;
        }


        if (this.eventSubscriptions.has(eventoId)) {
            console.log(` Ya suscrito al evento ${eventoId}`);
            return;
        }

        const topic = `/topic/event/${eventoId}/quota`;
        console.log(` Suscribiéndose al tópico: ${topic}`);

        try {
            const subscription = this.stompClient.subscribe(topic, (message) => {
                try {
                    const body = JSON.parse(message.body);
                    console.log(` Mensaje recibido para evento ${eventoId}:`, body);


                    let inscritosActuales: number;

                    if (typeof body === 'number') {

                        inscritosActuales = body;
                    } else if (body.inscritos !== undefined) {

                        inscritosActuales = body.inscritos;
                    } else if (body.inscritosActuales !== undefined) {
                        inscritosActuales = body.inscritosActuales;
                    } else if (body.count !== undefined) {
                        inscritosActuales = body.count;
                    } else if (body.quota !== undefined) {
                        inscritosActuales = body.quota;
                    } else {
                        console.warn(' Formato de mensaje no reconocido:', body);
                        return;
                    }

                    this.cuposSubject.next({
                        eventoId: body.idEvento || eventoId,
                        inscritosActuales: inscritosActuales
                    });

                    console.log(` Evento ${eventoId} actualizado: ${inscritosActuales} inscritos`);
                } catch (error) {
                    console.error(' Error parseando mensaje:', error);
                }
            });

            this.eventSubscriptions.set(eventoId, subscription);
            console.log(` Suscrito exitosamente al evento ${eventoId}`);
        } catch (error) {
            console.error(` Error suscribiéndose al evento ${eventoId}:`, error);
        }
    }


    unsubscribeFromEvent(eventoId: number): void {
        const subscription = this.eventSubscriptions.get(eventoId);
        if (subscription) {
            subscription.unsubscribe();
            this.eventSubscriptions.delete(eventoId);
            console.log(` Desuscrito del evento ${eventoId}`);
        }
    }


    unsubscribeAll(): void {
        this.eventSubscriptions.forEach((subscription, eventoId) => {
            subscription.unsubscribe();
            console.log(` Desuscrito del evento ${eventoId}`);
        });
        this.eventSubscriptions.clear();
    }


    disconnect(): void {
        if (this.stompClient) {
            this.unsubscribeAll();
            this.stompClient.deactivate();
            this.stompClient = null;
            this.connected = false;
        }
    }

    ngOnDestroy(): void {
        this.disconnect();
        this.cuposSubject.complete();
    }
}