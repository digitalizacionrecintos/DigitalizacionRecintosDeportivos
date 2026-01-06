import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EventosService } from '../../services/eventos.service';
import { WebsocketService } from '../../services/websocket.service';
import { InscripcionesService } from '../../services/inscripciones.service';
import { Subscription } from 'rxjs';
import { environment } from '../../../environments/environment';


interface Evento {
  id: number;
  titulo: string;
  fecha: string;
  fechaInicio?: string;
  fechaFin?: string;
  horaInicio?: string;
  horaFin?: string;
  ubicacion: string;
  direccion: string;
  color: string;
  estado: 'EN_ESPERA' | 'DISPONIBLE' | 'TERMINADO' | 'TRANSCURRIENDO';
  descripcion: string;
  imagen: string;
  encargado: string;
  cupoMaximo: number;
  inscritosActuales: number;
  inscripciones?: any[];
  cargandoInscritos?: boolean;
  categoria?: {
    id: number;
    nombre: string;
    descripcion: string;
  };
}

@Component({
  selector: 'app-eventos-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `

    <ng-template #placeholder>
      <div class="w-full h-full flex items-center justify-center bg-gray-200">
        <svg class="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
        </svg>
      </div>
    </ng-template>


    <ng-template #placeholderLarge>
      <div class="w-full h-64 flex items-center justify-center bg-gray-200">
        <svg class="w-32 h-32 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
        </svg>
      </div>
    </ng-template>

    <div class="flex h-screen ml-64 overflow-hidden bg-gray-50">


      <div class="w-1/2 bg-[#0045AB] flex flex-col shadow-2xl z-10 relative">

        <div class="p-6 pb-2">


          <div class="flex justify-between items-center mb-6">
            <div class="flex items-center gap-3">
              <h1 class="text-4xl font-bold text-white">Eventos</h1>
              <button
                (click)="mostrarModalInfo = true"
                class="text-white/70 hover:text-white transition-colors p-1 rounded-full hover:bg-white/10"
                title="Información de estados"
              >
                <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </button>
            </div>
            <button
              (click)="irACrearEvento()"
              class="bg-[#39A853] hover:bg-green-600 text-white font-bold py-2 px-4 rounded-lg transition-colors flex items-center gap-2 shadow-lg text-sm"
            >
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd"/>
              </svg>
              Crear Evento
            </button>
          </div>


          <div class="relative mb-4">
            <input
              type="text"
              [(ngModel)]="searchTerm"
              placeholder=""
              class="w-full h-12 rounded-lg pl-4 pr-10 text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-300"
            >
            <button class="absolute right-0 top-0 h-12 w-12 bg-[#9ca3af] rounded-r-lg flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
              </svg>
            </button>
          </div>


          <div class="flex flex-wrap items-center gap-2">
            <div class="bg-gray-300 text-gray-700 px-3 py-1.5 rounded font-bold text-xs">FILTROS</div>
            <button
              (click)="filtroActivo = 'EN_ESPERA'"
              [class.ring-2]="filtroActivo === 'EN_ESPERA'"
              class="bg-[#FFB81C] hover:bg-yellow-400 text-white px-4 py-1.5 rounded-full text-xs font-bold transition-all"
            >
              EN ESPERA
            </button>
            <button
              (click)="filtroActivo = 'DISPONIBLE'"
              [class.ring-2]="filtroActivo === 'DISPONIBLE'"
              class="bg-[#39A853] hover:bg-green-600 text-white px-4 py-1.5 rounded-full text-xs font-bold transition-all"
            >
              DISPONIBLE
            </button>
            <button
              (click)="filtroActivo = 'TERMINADO'"
              [class.ring-2]="filtroActivo === 'TERMINADO'"
              class="bg-[#E33E2B] hover:bg-red-600 text-white px-4 py-1.5 rounded-full text-xs font-bold transition-all"
            >
              TERMINADO
            </button>
            <button
              (click)="filtroActivo = 'TRANSCURRIENDO'"
              [class.ring-2]="filtroActivo === 'TRANSCURRIENDO'"
              class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-1.5 rounded-full text-xs font-bold transition-all"
            >
              EN CURSO
            </button>
            <button
              (click)="filtroActivo = 'TODOS'"
              class="bg-white text-gray-700 px-4 py-1.5 rounded-full text-xs font-bold hover:bg-gray-100 transition-all border border-gray-200"
            >
              TODOS
            </button>
          </div>


            <div class="flex items-center gap-2 mt-2">
              <span class="text-white text-xs font-bold">ORDENAR:</span>
              <button
                (click)="cambiarOrdenFecha()"
                class="flex items-center gap-1 px-3 py-1 rounded-full text-xs font-bold transition-all bg-blue-500 text-white"
              >
                <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                </svg>
                {{ ordenFecha === 'asc' ? 'Menos reciente' : 'Más reciente' }}
                <svg *ngIf="ordenFecha === 'asc'" class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"/>
                </svg>
                <svg *ngIf="ordenFecha === 'desc'" class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                </svg>
              </button>
            </div>
        </div>


        <div class="flex-1 overflow-y-auto p-4 space-y-3 custom-scrollbar">


          <div *ngIf="cargando" class="text-white text-center py-4">Cargando eventos...</div>


          <div
            *ngFor="let evento of eventosPaginados"
            (click)="seleccionarEvento(evento)"
            (contextmenu)="onRightClick($event, evento)"
            class="relative bg-[#fefce8] rounded-xl p-4 shadow-md cursor-pointer transform transition-transform hover:scale-[1.02] overflow-hidden min-h-[120px]"
          >

            <div
              class="absolute right-0 top-0 bottom-0 w-8"
              [ngClass]="{
                'bg-[#FFB81C]': evento.estado === 'EN_ESPERA',
                'bg-[#39A853]': evento.estado === 'DISPONIBLE',
                'bg-[#E33E2B]': evento.estado === 'TERMINADO',
                'bg-blue-600': evento.estado === 'TRANSCURRIENDO'
              }"
            ></div>

            <div class="pr-10">
              <h3 class="text-[#0045AB] text-lg font-bold leading-tight mb-3">{{ evento.titulo }}</h3>

                <div class="flex items-center gap-2 mb-2" *ngIf="evento.categoria">
                  <span class="bg-[#FFCB4A] text-[#85690F] px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider border border-purple-200">
                    {{ evento.categoria.nombre }}
                  </span>
                </div>

              <div class="flex flex-col gap-2">

                <div class="flex items-center gap-2">
                  <div class="bg-[#0070BC] text-white px-3 py-0.5 rounded-full text-xs font-semibold flex items-center gap-1 w-fit">
                    <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/></svg>
                    {{ evento.fecha }}
                  </div>
                </div>


                <div class="flex items-center gap-2">
                  <div class="bg-[#00AAC8] text-white px-3 py-0.5 rounded-full text-xs font-semibold flex items-center gap-1 w-fit">
                    <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/></svg>
                    {{ evento.ubicacion }}
                  </div>
                </div>
              </div>
            </div>
          </div>


          <div *ngIf="!cargando && eventosFiltrados.length === 0" class="text-white text-center py-8 opacity-80">
            No se encontraron eventos
          </div>


          <div *ngIf="!cargando && totalPaginas > 1" class="flex items-center justify-center gap-2 pt-4 pb-2">
            <button
              (click)="paginaAnterior()"
              [disabled]="paginaActual === 1"
              class="p-2 rounded-lg bg-white/20 hover:bg-white/30 text-white disabled:opacity-30 disabled:cursor-not-allowed transition-all"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
              </svg>
            </button>

            <div class="flex items-center gap-1">
              <ng-container *ngFor="let p of paginas">

                <button
                  *ngIf="p !== '...'"
                  (click)="irAPagina(p)"
                  class="w-8 h-8 rounded-lg text-xs font-bold transition-all"
                  [ngClass]="{
                    'bg-white text-[#0045AB]': paginaActual === p,
                    'bg-white/20 text-white hover:bg-white/30': paginaActual !== p
                  }"
                >
                  {{ p }}
                </button>


                <span
                  *ngIf="p === '...'"
                  class="w-8 h-8 flex items-center justify-center text-white/60 text-xs font-bold"
                >
                  ...
                </span>
              </ng-container>
            </div>

            <button
              (click)="paginaSiguiente()"
              [disabled]="paginaActual === totalPaginas"
              class="p-2 rounded-lg bg-white/20 hover:bg-white/30 text-white disabled:opacity-30 disabled:cursor-not-allowed transition-all"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </button>
          </div>


          <div *ngIf="!cargando && eventosFiltrados.length > 0" class="text-center text-white/60 text-xs pb-2">
            Mostrando {{ (paginaActual - 1) * eventosPorPagina + 1 }} - {{ paginaActual * eventosPorPagina > eventosFiltrados.length ? eventosFiltrados.length : paginaActual * eventosPorPagina }} de {{ eventosFiltrados.length }} eventos
          </div>

        </div>
      </div>


      <div class="flex-1 bg-gray-50 flex flex-col overflow-y-auto p-8" *ngIf="eventoSeleccionado">
        <div class="max-w-4xl mx-auto w-full space-y-6">

            <div class="relative overflow-hidden bg-white rounded-2xl shadow-lg border border-gray-100">
              <img
                *ngIf="eventoSeleccionado.imagen; else placeholderLarge"
                [src]="eventoSeleccionado.imagen"
                [alt]="eventoSeleccionado.titulo"
                class="w-full h-64 object-cover"
              >
              <div class="absolute top-4 right-4">
                <span class="px-4 py-2 rounded-full text-sm font-bold shadow-lg text-white"
                      [ngClass]="{
                        'bg-[#FFB81C]': eventoSeleccionado.estado === 'EN_ESPERA',
                        'bg-[#39A853]': eventoSeleccionado.estado === 'DISPONIBLE',
                        'bg-[#E33E2B]': eventoSeleccionado.estado === 'TERMINADO',
                        'bg-blue-600': eventoSeleccionado.estado === 'TRANSCURRIENDO'
                      }">
                  {{ getEstadoTexto(eventoSeleccionado.estado) }}
                </span>
              </div>
            </div>


            <div class="bg-white rounded-2xl shadow-lg p-8 border border-gray-100">
              <div class="mb-2">
                <img src="images/cuatro_colores.png" alt="" class="w-48 h-auto object-contain">
              </div>
              <div class="mb-6">
                <h2 class="text-2xl font-bold text-gray-900">{{ eventoSeleccionado.titulo }}</h2>
              </div>


              <div class="space-y-3 mb-6">

                <div class="flex items-center gap-3 p-3 bg-[#00AAC8]/10 rounded-lg border border-[#00AAC8]/20">
                  <div class="bg-[#00AAC8] p-2 rounded-lg flex-shrink-0">
                    <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
                    </svg>
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-bold text-[#00AAC8] truncate">{{ eventoSeleccionado.ubicacion }}</p>
                    <p class="text-xs text-gray-600 truncate">{{ eventoSeleccionado.direccion }}</p>
                  </div>
                </div>


                <div class="flex items-center gap-3 p-3 bg-[#0070BC]/10 rounded-lg border border-[#0070BC]/20">
                  <div class="bg-[#0070BC] p-2 rounded-lg flex-shrink-0">
                     <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/></svg>
                  </div>
                  <span class="text-sm font-bold text-[#0070BC]">{{ eventoSeleccionado.fecha }}</span>
                </div>


                <div class="flex items-center gap-3 p-3 bg-[#FFCB4A]/50 rounded-lg border border-[#FFCB4A]/20" *ngIf="eventoSeleccionado.categoria">
                  <div class="bg-[#FFCB4A] p-2 rounded-lg flex-shrink-0">
                    <svg class="w-5 h-5 text-[#85690F]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                    </svg>
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="text-xs text-[#85690F] font-bold uppercase">Categoría</p>
                    <p class="text-sm font-bold text-gray-900 truncate">{{ eventoSeleccionado.categoria.nombre }}</p>
                  </div>
                </div>


                <div class="flex items-center gap-3 p-3 bg-gray-50 rounded-lg border border-gray-100">
                  <div class="w-9 h-9 bg-gray-200 rounded-full flex items-center justify-center flex-shrink-0 text-gray-500">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/></svg>
                  </div>
                  <div class="flex-1 min-w-0">
                     <p class="text-xs text-gray-500">Encargado</p>
                     <p class="text-sm font-semibold text-gray-900 truncate">{{ eventoSeleccionado.encargado }}</p>
                  </div>
                </div>
              </div>


              <div *ngIf="eventoSeleccionado.estado === 'EN_ESPERA'" class="p-6 bg-gradient-to-br from-yellow-50 to-amber-100 rounded-xl border border-yellow-300 mb-6">
                <div class="text-center">
                  <div class="mb-4">
                    <svg class="w-12 h-12 mx-auto text-yellow-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                    </svg>
                  </div>
                  <h4 class="text-lg font-bold text-yellow-700 mb-2">Evento en Espera</h4>
                  <p class="text-sm text-yellow-600 mb-4">Este evento aún no está disponible para inscripciones</p>
                  <button
                    (click)="activarEvento()"
                    [disabled]="activandoEvento"
                    class="w-full bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white font-bold py-3 px-6 rounded-xl transition-all duration-300 flex items-center justify-center gap-2 shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <svg *ngIf="!activandoEvento" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                    </svg>
                    <svg *ngIf="activandoEvento" class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    {{ activandoEvento ? 'Activando...' : 'Activar Evento' }}
                  </button>
                </div>
              </div>


              <div *ngIf="eventoSeleccionado.estado !== 'EN_ESPERA'" class="p-6 bg-gradient-to-br from-yellow-50 to-orange-50 rounded-xl border border-yellow-200 mb-6">
                <div class="flex items-center justify-between mb-4">
                  <span class="text-sm font-bold text-gray-700">Capacidad</span>
                  <svg class="w-6 h-6 text-orange-500" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z"/>
                  </svg>
                </div>

                <div class="flex items-baseline justify-center gap-2 mb-3">
                  <span class="text-3xl font-bold bg-gradient-to-r from-orange-600 to-red-600 bg-clip-text text-transparent">{{ eventoSeleccionado.inscritosActuales }}</span>
                  <span class="text-xl text-gray-400">/</span>
                  <span class="text-2xl font-semibold text-gray-600">{{ eventoSeleccionado.cupoMaximo }}</span>
                </div>

                <div class="w-full bg-gray-200 rounded-full h-2.5 mb-2 overflow-hidden">
                  <div
                    class="h-full rounded-full transition-all duration-300"
                    [ngClass]="{
                      'bg-gradient-to-r from-green-400 to-green-500': (eventoSeleccionado.inscritosActuales / eventoSeleccionado.cupoMaximo) < 0.7,
                      'bg-gradient-to-r from-yellow-400 to-orange-400': (eventoSeleccionado.inscritosActuales / eventoSeleccionado.cupoMaximo) >= 0.7 && (eventoSeleccionado.inscritosActuales / eventoSeleccionado.cupoMaximo) < 0.9,
                      'bg-gradient-to-r from-red-400 to-red-500': (eventoSeleccionado.inscritosActuales / eventoSeleccionado.cupoMaximo) >= 0.9
                    }"
                    [style.width.%]="(eventoSeleccionado.inscritosActuales / eventoSeleccionado.cupoMaximo) * 100">
                  </div>
                </div>

                <div class="flex items-center justify-between text-xs">
                  <span class="text-gray-600 font-medium">{{ ((eventoSeleccionado.inscritosActuales / eventoSeleccionado.cupoMaximo) * 100).toFixed(0) }}% ocupado</span>
                  <span class="font-bold"
                    [ngClass]="{
                      'text-green-600': (eventoSeleccionado.inscritosActuales / eventoSeleccionado.cupoMaximo) < 0.7,
                      'text-orange-600': (eventoSeleccionado.inscritosActuales / eventoSeleccionado.cupoMaximo) >= 0.7 && (eventoSeleccionado.inscritosActuales / eventoSeleccionado.cupoMaximo) < 0.9,
                      'text-red-600': (eventoSeleccionado.inscritosActuales / eventoSeleccionado.cupoMaximo) >= 0.9
                    }">
                    {{ eventoSeleccionado.cupoMaximo - eventoSeleccionado.inscritosActuales }} disponibles
                  </span>
                </div>
              </div>


              <div class="mb-6">
                <h3 class="text-sm font-bold text-gray-700 mb-2">Descripción</h3>
                <p class="text-sm text-gray-600 leading-relaxed">{{ eventoSeleccionado.descripcion }}</p>
              </div>


              <button
                (click)="editarEventoSeleccionado()"
                [disabled]="eventoSeleccionado.estado === 'TRANSCURRIENDO'"
                [ngClass]="{'opacity-50 cursor-not-allowed': eventoSeleccionado.estado === 'TRANSCURRIENDO'}"
                class="w-full bg-gradient-to-r from-[#00AAC8] to-[#0045AB] hover:from-[#0045AB] hover:to-[#051C9C] text-white font-bold py-3 px-6 rounded-xl transition-all duration-300 flex items-center justify-center gap-2 shadow-lg hover:shadow-xl"
              >
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
                </svg>
                Editar Evento
              </button>
            </div>
        </div>
      </div>


      <div class="flex-1 bg-white flex items-center justify-center flex-col text-gray-400" *ngIf="!eventoSeleccionado">
        <svg class="w-24 h-24 mb-4 opacity-20" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/></svg>
        <p class="text-xl font-semibold">Selecciona un evento para ver el detalle</p>
      </div>

    </div>


    <div
      *ngIf="showActionMenu && menuPosition"
      [style.top.px]="menuPosition.y"
      [style.left.px]="menuPosition.x"
      class="fixed bg-white rounded-lg shadow-xl py-2 z-50 min-w-[160px] border border-gray-100"
      (click)="$event.stopPropagation()"
    >
      <button (click)="editarEvento()" class="w-full text-left px-4 py-2 hover:bg-blue-50 text-[#0045AB] font-semibold flex items-center gap-2">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"/></svg>
        Editar
      </button>
      <button (click)="confirmarEliminarEvento()" class="w-full text-left px-4 py-2 hover:bg-red-50 text-red-600 font-semibold flex items-center gap-2">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
        Eliminar
      </button>
    </div>


    <div
      *ngIf="mostrarModalEliminar"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      (click)="cancelarEliminarEvento()"
    >
      <div
        (click)="$event.stopPropagation()"
        class="bg-white rounded-lg shadow-2xl max-w-md w-full p-8"
      >
        <div class="text-center">
          <div class="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-red-100 mb-4">
            <svg class="h-10 w-10 text-red-600" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
            </svg>
          </div>
          <h3 class="text-2xl font-bold text-gray-900 mb-2">¿Eliminar evento?</h3>
          <p class="text-gray-600 mb-6">
            ¿Estás seguro de que deseas eliminar <strong>{{ eventoAEliminar?.titulo }}</strong>?
            Esta acción no se puede deshacer.
          </p>
          <div class="flex gap-4">
            <button
              (click)="eliminarEvento()"
              [disabled]="eliminando"
              class="flex-1 bg-red-600 hover:bg-red-700 text-white font-bold py-3 px-6 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              <svg *ngIf="eliminando" class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ eliminando ? 'Eliminando...' : 'Eliminar' }}
            </button>
            <button
              (click)="cancelarEliminarEvento()"
              [disabled]="eliminando"
              class="flex-1 bg-gray-300 hover:bg-gray-400 text-gray-700 font-bold py-3 px-6 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Cancelar
            </button>
          </div>
        </div>
      </div>
    </div>


    <div *ngIf="showActionMenu" (click)="closeActionMenu()" class="fixed inset-0 z-40"></div>


    <div
      *ngIf="mostrarModalInfo"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      (click)="mostrarModalInfo = false"
    >
      <div
        (click)="$event.stopPropagation()"
        class="bg-white rounded-2xl shadow-2xl max-w-lg w-full overflow-hidden"
      >

        <div class="bg-[#0045AB] px-6 py-4 flex justify-between items-center">
          <h3 class="text-xl font-bold text-white flex items-center gap-2">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
            Estados de los Eventos
          </h3>
          <button (click)="mostrarModalInfo = false" class="text-white/70 hover:text-white transition-colors">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>


        <div class="p-6 space-y-4">
          <p class="text-gray-600 text-sm mb-4">
            Esta guía te ayudará a entender en qué etapa se encuentra cada evento según su color y etiqueta.
          </p>


          <div class="flex items-start gap-4 p-3 rounded-lg bg-yellow-50 border border-yellow-100">
            <div class="bg-[#FFB81C] text-white px-3 py-1 rounded-full text-xs font-bold shrink-0 mt-0.5">
              EN ESPERA
            </div>
            <div>
              <p class="text-gray-800 font-bold text-sm">Próximamente</p>
              <p class="text-gray-600 text-xs">El evento aún no ha comenzado. Las inscripciones pueden no estar abiertas todavía.</p>
            </div>
          </div>


          <div class="flex items-start gap-4 p-3 rounded-lg bg-green-50 border border-green-100">
            <div class="bg-[#39A853] text-white px-3 py-1 rounded-full text-xs font-bold shrink-0 mt-0.5">
              DISPONIBLE
            </div>
            <div>
              <p class="text-gray-800 font-bold text-sm">Abierto a inscripciones</p>
              <p class="text-gray-600 text-xs">El evento está activo y vigente. ¡Puedes inscribirte y participar!</p>
            </div>
          </div>


          <div class="flex items-start gap-4 p-3 rounded-lg bg-blue-50 border border-blue-100">
            <div class="bg-blue-600 text-white px-3 py-1 rounded-full text-xs font-bold shrink-0 mt-0.5">
              EN CURSO
            </div>
            <div>
              <p class="text-gray-800 font-bold text-sm">Transcurriendo ahora</p>
              <p class="text-gray-600 text-xs">El evento se está llevando a cabo en este preciso momento.</p>
            </div>
          </div>


          <div class="flex items-start gap-4 p-3 rounded-lg bg-red-50 border border-red-100">
            <div class="bg-[#E33E2B] text-white px-3 py-1 rounded-full text-xs font-bold shrink-0 mt-0.5">
              TERMINADO
            </div>
            <div>
              <p class="text-gray-800 font-bold text-sm">Finalizado</p>
              <p class="text-gray-600 text-xs">El evento ha concluido y ya no admite nuevas inscripciones.</p>
            </div>
          </div>
        </div>


        <div class="px-6 py-4 bg-gray-50 flex justify-end">
          <button
            (click)="mostrarModalInfo = false"
            class="bg-[#0045AB] hover:bg-blue-700 text-white font-bold py-2 px-6 rounded-lg transition-colors shadow-sm"
          >
            Entendido
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .custom-scrollbar::-webkit-scrollbar {
      width: 6px;
    }
    .custom-scrollbar::-webkit-scrollbar-track {
      background: rgba(0,0,0,0.1);
    }
    .custom-scrollbar::-webkit-scrollbar-thumb {
      background: rgba(255,255,255,0.3);
      border-radius: 3px;
    }
    .custom-scrollbar::-webkit-scrollbar-thumb:hover {
      background: rgba(255,255,255,0.5);
    }
  `]
})
export class EventosListComponent implements OnInit, OnDestroy {
  searchTerm = '';
  filtroActivo = 'TODOS';
  showActionMenu = false;
  menuPosition: { x: number; y: number } | null = null;
  eventoSeleccionado: Evento | null = null;
  eventos: Evento[] = [];
  cargando = true;
  error = '';
  private wsSubscription?: Subscription;


  mostrarModalEliminar = false;
  eliminando = false;

  eventoAEliminar: Evento | null = null;


  mostrarModalInfo = false;


  activandoEvento = false;


  paginaActual = 1;
  eventosPorPagina = 5;


  ordenFecha: 'asc' | 'desc' = 'asc';

  constructor(
    private router: Router,
    private eventosService: EventosService,
    private websocketService: WebsocketService,
    private inscripcionesService: InscripcionesService,
    private cdr: ChangeDetectorRef
  ) { }


  calcularEstadoEvento(fechaInicio?: string, horaInicio?: string, fechaFin?: string, horaFin?: string): 'EN_ESPERA' | 'DISPONIBLE' | 'TERMINADO' {

    if (!fechaInicio || !horaInicio || !fechaFin || !horaFin) {
      console.warn(' Fechas incompletas:', { fechaInicio, horaInicio, fechaFin, horaFin });
      return 'DISPONIBLE';
    }

    const ahora = new Date();


    const inicioStr = `${fechaInicio}T${horaInicio}`;
    const inicio = new Date(inicioStr);


    const finStr = `${fechaFin}T${horaFin}`;
    const fin = new Date(finStr);

    console.log(' Calculando estado:');
    console.log('  Ahora:', ahora.toISOString());
    console.log('  Inicio:', inicioStr, '->', inicio.toISOString());
    console.log('  Fin:', finStr, '->', fin.toISOString());

    let estado: 'EN_ESPERA' | 'DISPONIBLE' | 'TERMINADO';
    if (ahora < inicio) {
      estado = 'EN_ESPERA';
    } else if (ahora >= inicio && ahora <= fin) {
      estado = 'DISPONIBLE';
    } else {
      estado = 'TERMINADO';
    }

    console.log('  Estado calculado:', estado);
    return estado;
  }


  getEstadoColor(estado: string): string {
    switch (estado) {
      case 'EN_ESPERA':
        return 'bg-yellow-500';
      case 'DISPONIBLE':
        return 'bg-green-500';
      case 'TERMINADO':
        return 'bg-red-500';
      case 'TRANSCURRIENDO':
        return 'bg-blue-600';
      default:
        return 'bg-blue-500';
    }
  }


  getEstadoTexto(estado: string): string {
    switch (estado) {
      case 'EN_ESPERA':
        return 'En Espera';
      case 'DISPONIBLE':
        return 'Disponible';
      case 'TERMINADO':
        return 'Terminado';
      case 'TRANSCURRIENDO':
        return 'En Curso';
      default:
        return estado;
    }
  }


  getEstadoBorderColor(estado: string): string {
    switch (estado) {
      case 'EN_ESPERA':
        return '#eab308';
      case 'DISPONIBLE':
        return '#22c55e';
      case 'TERMINADO':
        return '#ef4444';
      case 'TRANSCURRIENDO':
        return '#2563eb';
      default:
        return '#3b82f6';
    }
  }

  ngOnInit(): void {
    this.cargarEventos();
    this.subscribeToWebSocket();
  }

  ngOnDestroy(): void {

    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }

    this.websocketService.unsubscribeAll();
  }


  private subscribeToWebSocket(): void {
    this.wsSubscription = this.websocketService.getCuposUpdates().subscribe({
      next: (update) => {
        console.log(' Actualización de cupos recibida:', update);


        const evento = this.eventos.find(e => e.id === update.eventoId);
        if (evento) {
          evento.inscritosActuales = update.inscritosActuales;


          if (this.eventoSeleccionado && this.eventoSeleccionado.id === update.eventoId) {
            this.eventoSeleccionado.inscritosActuales = update.inscritosActuales;
          }

          console.log(` Evento ${evento.titulo} actualizado: ${update.inscritosActuales} inscritos`);


          this.cdr.detectChanges();
        }
      },
      error: (error) => {
        console.error(' Error en WebSocket subscription:', error);
      }
    });
  }


  cargarEventos(): void {
    this.cargando = true;
    this.error = '';

    console.log(' Cargando eventos...');

    this.eventosService.getEventos().subscribe({
      next: (eventosDTO) => {
        console.log(' Eventos recibidos del backend:', eventosDTO);
        console.log(' Primer evento completo:', eventosDTO[0]);
        console.log(' Claves del primer evento:', eventosDTO[0] ? Object.keys(eventosDTO[0]) : 'No hay eventos');


        this.eventos = eventosDTO.map(dto => {
          console.log(`   Evento "${dto.titulo}":`, dto);
          console.log(`      dto.idEvento = ${dto.idEvento}`);
          console.log(`      dto.inscritosActuales = ${dto.inscritosActuales}`);
          console.log(`      dto.inscritos = ${(dto as any).inscritos}`);
          console.log(`      Recinto:`, dto.recinto);
          console.log(`      Encargado:`, dto.encargado);
          console.log(`      Fechas para estado:`, {
            fechaInicio: dto.fechaInicio,
            horaInicio: dto.horaInicio,
            fechaFin: dto.fechaFin,
            horaFin: dto.horaFin
          });

          let estado = dto.estado;
          const estadoUpper = estado ? estado.toUpperCase() : '';

          if (estadoUpper === 'EN_CURSO' || estadoUpper === 'IN_PROGRESS' || estadoUpper === 'TRANSCURRIENDO') {
            estado = 'TRANSCURRIENDO';
          } else if (estadoUpper === 'EN_ESPERA') {
            estado = 'EN_ESPERA';
          } else if (estadoUpper === 'DISPONIBLE') {
            estado = 'DISPONIBLE';
          } else if (estadoUpper === 'TERMINADO') {
            estado = 'TERMINADO';
          }

          return {
            id: dto.idEvento || dto.id || 0,
            titulo: dto.titulo,
            fecha: this.formatearFecha(dto.fechaInicio, dto.horaInicio),
            fechaInicio: dto.fechaInicio,
            fechaFin: dto.fechaFin,
            horaInicio: dto.horaInicio,
            horaFin: dto.horaFin,
            ubicacion: dto.recinto?.nombre || 'Recinto no especificado',
            direccion: dto.recinto?.ubicacion || '',
            color: this.getColorPorEstado(estado),
            estado: estado,
            descripcion: dto.descripcion,
            imagen: (() => {

              const eventoImg = dto.imagenUrl || dto.imagen;
              const recintoImg = dto.recinto?.imagenUrl || dto.recinto?.imagen;
              const imgToUse = eventoImg || recintoImg;

              if (!imgToUse) return '';

              return imgToUse.startsWith('http')
                ? imgToUse
                : `${environment.apiUrl}/uploads/${imgToUse}`;
            })(),
            encargado: dto.encargado?.nombre
              ? `${dto.encargado.nombre} ${dto.encargado.apellido || ''}`.trim()
              : 'Encargado no especificado',
            cupoMaximo: dto.cupoMaximo,
            inscritosActuales: dto.inscritos || dto.inscritosActuales || 0,
            categoria: dto.categoria
          };
        });

        console.log(' Eventos mapeados:', this.eventos);


        this.eventos.forEach(evento => {
          this.websocketService.subscribeToEvent(evento.id);
        });


        if (this.eventos.length > 0 && !this.eventoSeleccionado) {
          this.eventoSeleccionado = this.eventos[0];
        }


        this.cargando = false;
        this.cdr.detectChanges();
        console.log(' Carga completada, cargando =', this.cargando);
      },
      error: (error) => {
        console.error(' Error cargando eventos:', error);
        this.error = 'Error al cargar eventos. Verifica tu conexión con el backend.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }


  private formatearFecha(fecha: string, hora: string): string {
    const fechaObj = new Date(fecha);
    const horaObj = new Date(hora);
    const dia = fechaObj.getDate().toString().padStart(2, '0');
    const mes = (fechaObj.getMonth() + 1).toString().padStart(2, '0');
    const año = fechaObj.getFullYear().toString().slice(-2);
    const horas = horaObj.getHours().toString().padStart(2, '0');
    const minutos = horaObj.getMinutes().toString().padStart(2, '0');
    return `${dia} / ${mes} / ${año} - ${horas}: ${minutos}`;
  }


  private getColorPorEstado(estado: string): string {
    switch (estado) {
      case 'EN_ESPERA':
        return 'bg-yellow-400';
      case 'DISPONIBLE':
        return 'bg-green-400';
      case 'TERMINADO':
        return 'bg-red-400';
      case 'TRANSCURRIENDO':
        return 'bg-blue-400';
      default:
        return 'bg-blue-400';
    }
  }

  filtros = [
    { label: 'TODOS', valor: 'TODOS', color: 'bg-gray-500' },
    { label: 'EN ESPERA', valor: 'EN_ESPERA', color: 'bg-yellow-500' },
    { label: 'DISPONIBLE', valor: 'DISPONIBLE', color: 'bg-green-500' },
    { label: 'EN CURSO', valor: 'TRANSCURRIENDO', color: 'bg-blue-600' },
    { label: 'TERMINADO', valor: 'TERMINADO', color: 'bg-red-600' }
  ];

  get eventosFiltrados(): Evento[] {
    let filtered = this.eventos;

    if (this.filtroActivo !== 'TODOS') {
      filtered = filtered.filter(e => e.estado === this.filtroActivo);
    }

    if (this.searchTerm.trim()) {
      const search = this.searchTerm.toLowerCase();
      filtered = filtered.filter(e =>
        e.titulo.toLowerCase().includes(search) ||
        e.ubicacion.toLowerCase().includes(search)
      );
    }


    filtered = [...filtered].sort((a, b) => {
      const fechaA = new Date(a.fechaInicio || '').getTime();
      const fechaB = new Date(b.fechaInicio || '').getTime();
      return this.ordenFecha === 'asc' ? fechaA - fechaB : fechaB - fechaA;
    });

    return filtered;
  }


  get eventosPaginados(): Evento[] {
    const inicio = (this.paginaActual - 1) * this.eventosPorPagina;
    const fin = inicio + this.eventosPorPagina;
    return this.eventosFiltrados.slice(inicio, fin);
  }


  get totalPaginas(): number {
    return Math.ceil(this.eventosFiltrados.length / this.eventosPorPagina);
  }


  irAPagina(pagina: any): void {
    if (typeof pagina === 'number' && pagina >= 1 && pagina <= this.totalPaginas) {
      this.paginaActual = pagina;
    }
  }


  get paginas(): (number | string)[] {
    const total = this.totalPaginas;
    const actual = this.paginaActual;
    const paginas: (number | string)[] = [];

    if (total <= 7) {
      for (let i = 1; i <= total; i++) paginas.push(i);
    } else {

      paginas.push(1);

      if (actual <= 4) {

        for (let i = 2; i <= 5; i++) paginas.push(i);
        paginas.push('...');
        paginas.push(total);
      } else if (actual >= total - 3) {

        paginas.push('...');
        for (let i = total - 4; i <= total; i++) paginas.push(i);
      } else {

        paginas.push('...');
        for (let i = actual - 1; i <= actual + 1; i++) paginas.push(i);
        paginas.push('...');
        paginas.push(total);
      }
    }
    return paginas;
  }

  paginaAnterior(): void {
    if (this.paginaActual > 1) {
      this.paginaActual--;
    }
  }

  paginaSiguiente(): void {
    if (this.paginaActual < this.totalPaginas) {
      this.paginaActual++;
    }
  }


  cambiarOrdenFecha(): void {
    this.ordenFecha = this.ordenFecha === 'asc' ? 'desc' : 'asc';
    this.paginaActual = 1;
  }

  getFiltroClass(valor: string): string {
    const filtro = this.filtros.find(f => f.valor === valor);
    const isActive = this.filtroActivo === valor;
    return `${filtro?.color} ${isActive ? 'ring-2 ring-offset-2 ring-white' : ''} text - white`;
  }

  seleccionarEvento(evento: Evento): void {
    this.eventoSeleccionado = evento;
  }

  openActionMenu(event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.menuPosition = { x: event.clientX, y: event.clientY };
    this.showActionMenu = true;
  }

  closeActionMenu(): void {
    this.showActionMenu = false;
    this.menuPosition = null;
  }

  onRightClick(event: MouseEvent, evento: Evento): void {
    event.preventDefault();
    this.eventoSeleccionado = evento;
    this.openActionMenu(event);
  }

  verEvento(): void {
    if (this.eventoSeleccionado) {

      this.closeActionMenu();
    }
  }

  editarEvento(): void {
    if (this.eventoSeleccionado) {
      this.router.navigate(['/eventos', this.eventoSeleccionado.id, 'editar']);
    }
    this.closeActionMenu();
  }

  editarEventoSeleccionado(): void {
    if (this.eventoSeleccionado) {
      this.router.navigate(['/eventos', this.eventoSeleccionado.id, 'editar']);
    }
  }

  irACrearEvento(): void {
    this.router.navigate(['/crear']);
  }

  confirmarEliminarEvento(): void {
    this.eventoAEliminar = this.eventoSeleccionado;
    this.mostrarModalEliminar = true;
    this.closeActionMenu();
  }

  eliminarEvento(): void {
    if (this.eventoAEliminar) {
      this.eliminando = true;
      const eventoId = this.eventoAEliminar.id;

      this.eventosService.deleteEvento(eventoId).subscribe({
        next: () => {
          console.log(' Evento eliminado exitosamente:', eventoId);


          this.eventos = this.eventos.filter(e => e.id !== eventoId);


          if (this.eventoSeleccionado?.id === eventoId) {
            this.eventoSeleccionado = this.eventos.length > 0 ? this.eventos[0] : null;
          }


          this.eliminando = false;
          this.cancelarEliminarEvento();


          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error(' Error eliminando evento:', err);
          this.eliminando = false;
          this.cancelarEliminarEvento();


          if (err.status === 403) {
            alert(' No se puede eliminar este evento.\n\nPosibles razones:\n• El evento tiene inscripciones activas\n• No tienes permisos suficientes\n\nPrimero cancela las inscripciones o contacta al administrador.');
          } else if (err.status === 404) {
            alert(' El evento no existe o ya fue eliminado.');

            this.cargarEventos();
          } else {
            alert('Error eliminando evento: ' + (err.message || 'Error desconocido'));
          }


          this.cdr.detectChanges();
        }
      });
    }
  }

  cancelarEliminarEvento(): void {
    this.mostrarModalEliminar = false;
    this.eventoAEliminar = null;
  }


  activarEvento(): void {
    if (!this.eventoSeleccionado) return;

    this.activandoEvento = true;
    const eventoId = this.eventoSeleccionado.id;

    console.log(' Activando evento:', eventoId);

    this.eventosService.cambiarEstado(eventoId, 'DISPONIBLE').subscribe({
      next: () => {
        console.log(' Evento activado exitosamente');


        if (this.eventoSeleccionado) {
          this.eventoSeleccionado.estado = 'DISPONIBLE';
          this.eventoSeleccionado.color = this.getColorPorEstado('DISPONIBLE');
        }


        const eventoEnLista = this.eventos.find(e => e.id === eventoId);
        if (eventoEnLista) {
          eventoEnLista.estado = 'DISPONIBLE';
          eventoEnLista.color = this.getColorPorEstado('DISPONIBLE');
        }

        this.activandoEvento = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(' Error activando evento:', err);
        alert('Error al activar el evento: ' + (err.error?.message || err.message || 'Error desconocido'));
        this.activandoEvento = false;
        this.cdr.detectChanges();
      }
    });
  }
}