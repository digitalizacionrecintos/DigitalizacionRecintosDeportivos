import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RecintosService } from '../../services/recintos.service';
import { StorageService } from '../../services/storage.service';
import { finalize } from 'rxjs';
import { environment } from '../../../environments/environment';

interface Recinto {
  id: number;
  nombre: string;
  direccion: string;
  capacidad: number;
  tipo: string;
  descripcion: string;
  imagen: string;
  imagenUrl?: string;
  estado: 'ACTIVO' | 'INACTIVO';
  telefono: string;
  email: string;
}

@Component({
  selector: 'app-recintos',
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

    <div class="ml-64 min-h-screen bg-gradient-to-br from-arica-blue via-blue-700 to-cyan-500 p-8">
      <div class="max-w-7xl mx-auto">


        <div class="bg-white rounded-lg shadow-2xl overflow-hidden mb-8">
        <div class="h-2 overflow-hidden">
          <img src="images/cuatro_colores.png" alt="" class="w-full h-full object-cover">
        </div>
          <div class="p-8 flex justify-between items-center">
            <div>
              <h1 class="text-4xl font-bold text-arica-blue mb-2">Gestión de Recintos</h1>
              <p class="text-gray-600">Administra los espacios disponibles para eventos</p>
            </div>
            <button
              (click)="abrirModalCrear()"
              class="bg-arica-green hover:bg-green-600 text-white font-bold py-3 px-6 rounded-lg transition-colors flex items-center gap-2 shadow-lg"
            >
              <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd"/>
              </svg>
              Crear Recinto
            </button>
          </div>
        </div>


        <div class="bg-white rounded-lg shadow-lg p-6 mb-8">
          <div class="flex flex-wrap gap-4">
            <div class="flex-1 min-w-[300px]">
              <div class="flex items-center bg-gray-100 rounded-lg px-4 py-3">
                <svg class="w-5 h-5 text-gray-500" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd"/>
                </svg>
                <input
                  type="text"
                  [(ngModel)]="busqueda"
                  placeholder="Buscar recintos por nombre, tipo o dirección..."
                  class="flex-1 bg-transparent outline-none text-gray-700 ml-3"
                >
              </div>
            </div>

            <div class="flex gap-2">
              <button
                *ngFor="let filtro of filtrosEstado"
                (click)="filtroEstadoActivo = filtro.valor"
                [class.bg-arica-blue]="filtroEstadoActivo === filtro.valor"
                [class.text-white]="filtroEstadoActivo === filtro.valor"
                [class.bg-gray-200]="filtroEstadoActivo !== filtro.valor"
                [class.text-gray-700]="filtroEstadoActivo !== filtro.valor"
                class="px-4 py-2 rounded-lg font-semibold transition-colors"
              >
                {{ filtro.label }}
              </button>
            </div>
          </div>
        </div>


        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            *ngFor="let recinto of recintosFiltrados"
            class="bg-white rounded-lg shadow-xl overflow-hidden hover:shadow-2xl transition-all hover:scale-105 cursor-pointer"
          >

            <div class="relative h-48 overflow-hidden">
              <img
                *ngIf="recinto.imagen; else placeholder"
                [src]="getImagenUrl(recinto.imagen)"
                [alt]="recinto.nombre"
                class="w-full h-full object-cover"
              >
              <div class="absolute top-3 right-3">
                <span
                  [class]="getEstadoClass(recinto.estado)"
                  class="px-3 py-1 rounded-full text-xs font-bold"
                >
                  {{ recinto.estado }}
                </span>
              </div>
            </div>


            <div class="p-6">
              <div class="flex justify-between items-start mb-3">
                <h3 class="text-xl font-bold text-arica-blue">{{ recinto.nombre }}</h3>
                <span class="bg-arica-cyan text-arica-blue px-2 py-1 rounded text-xs font-semibold">
                  {{ recinto.tipo }}
                </span>
              </div>

              <div class="space-y-2 text-sm text-gray-600 mb-4">
                <div class="flex items-start gap-2">
                  <svg class="w-4 h-4 mt-0.5 text-arica-blue flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clip-rule="evenodd"/>
                  </svg>
                  <span class="line-clamp-2">{{ recinto.direccion }}</span>
                </div>

                <div class="flex items-center gap-2">
                  <svg class="w-4 h-4 text-arica-blue" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z"/>
                  </svg>
                  <span><strong>Capacidad:</strong> {{ recinto.capacidad }} personas</span>
                </div>

                <div class="flex items-center gap-2">
                  <svg class="w-4 h-4 text-arica-blue" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z"/>
                  </svg>
                  <span>{{ recinto.telefono }}</span>
                </div>
              </div>

              <p class="text-sm text-gray-500 line-clamp-2 mb-4">
                {{ recinto.descripcion }}
              </p>


              <div class="flex gap-2">
                <button
                  (click)="verDetalle(recinto)"
                  class="flex-1 bg-arica-blue hover:bg-arica-blue-dark text-white font-semibold py-2 px-4 rounded-lg transition-colors flex items-center justify-center gap-2"
                >
                  <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M10 12a2 2 0 100-4 2 2 0 000 4z"/>
                    <path fill-rule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clip-rule="evenodd"/>
                  </svg>
                  Ver
                </button>
                <button
                  (click)="abrirModalEditar(recinto)"
                  class="flex-1 bg-arica-yellow hover:bg-yellow-500 text-white font-semibold py-2 px-4 rounded-lg transition-colors flex items-center justify-center gap-2"
                >
                  <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
                  </svg>
                  Editar
                </button>
                <button
                  (click)="confirmarEliminar(recinto)"
                  class="bg-arica-coral hover:bg-red-600 text-white font-semibold p-2 rounded-lg transition-colors"
                >
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>


        <div *ngIf="recintosFiltrados.length === 0" class="bg-white rounded-lg shadow-xl p-12 text-center">
          <svg class="w-24 h-24 mx-auto text-gray-400 mb-4" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h8a2 2 0 012 2v12a1 1 0 110 2h-3a1 1 0 01-1-1v-2a1 1 0 00-1-1H9a1 1 0 00-1 1v2a1 1 0 01-1 1H4a1 1 0 110-2V4zm3 1h2v2H7V5zm2 4H7v2h2V9zm2-4h2v2h-2V5zm2 4h-2v2h2V9z" clip-rule="evenodd"/>
          </svg>
          <h3 class="text-2xl font-bold text-gray-700 mb-2">No se encontraron recintos</h3>
          <p class="text-gray-500">Intenta ajustar los filtros o crear un nuevo recinto</p>
        </div>

      </div>
    </div>


    <div
      *ngIf="mostrarModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      (click)="cerrarModal()"
    >
      <div
        (click)="$event.stopPropagation()"
        class="bg-white rounded-lg shadow-2xl max-w-3xl w-full max-h-[90vh] overflow-y-auto"
      >
        <div class="h-2 overflow-hidden">
          <img src="images/cuatro_colores.png" alt="" class="w-full h-full object-cover">
        </div>

        <div class="p-8">
          <h2 class="text-3xl font-bold text-arica-blue mb-6">
            {{ modoEdicion ? 'Editar Recinto' : 'Crear Nuevo Recinto' }}
          </h2>

          <form class="space-y-6">

            <div>
              <label class="block text-arica-blue font-bold mb-2">Nombre del Recinto *</label>
              <input
                type="text"
                [(ngModel)]="recintoForm.nombre"
                name="nombre"
                placeholder="Ej: Estadio Municipal"
                class="w-full px-4 py-3 bg-arica-cyan/30 rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
              >
            </div>


            <div>
              <label class="block text-arica-blue font-bold mb-2">Capacidad *</label>
              <input
                type="number"
                [(ngModel)]="recintoForm.capacidad"
                name="capacidad"
                placeholder="Número de personas"
                [disabled]="modoEdicion"
                [class.bg-gray-200]="modoEdicion"
                [class.cursor-not-allowed]="modoEdicion"
                class="w-full px-4 py-3 bg-arica-cyan/30 rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
              >
              <p *ngIf="modoEdicion" class="text-xs text-gray-500 mt-1">La capacidad no se puede modificar</p>
            </div>


            <div>
              <label class="block text-arica-blue font-bold mb-2">Dirección *</label>
              <input
                type="text"
                [(ngModel)]="recintoForm.direccion"
                name="direccion"
                placeholder="Ej: Avenida Principal 1234, Arica"
                [disabled]="modoEdicion"
                [class.bg-gray-200]="modoEdicion"
                [class.cursor-not-allowed]="modoEdicion"
                class="w-full px-4 py-3 bg-arica-cyan/30 rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
              >
              <p *ngIf="modoEdicion" class="text-xs text-gray-500 mt-1">La dirección no se puede modificar</p>
            </div>


            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-arica-blue font-bold mb-2">Teléfono</label>
                <input
                  type="tel"
                  [(ngModel)]="recintoForm.telefono"
                  name="telefono"
                  placeholder="+56 9 1234 5678"
                  class="w-full px-4 py-3 bg-arica-cyan/30 rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                >
              </div>

              <div>
                <label class="block text-arica-blue font-bold mb-2">Email</label>
                <input
                  type="email"
                  [(ngModel)]="recintoForm.email"
                  name="email"
                  placeholder="recinto@arica.cl"
                  class="w-full px-4 py-3 bg-arica-cyan/30 rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                >
              </div>
            </div>


            <div>
              <label class="block text-arica-blue font-bold mb-2">Estado</label>
              <select
                [(ngModel)]="recintoForm.estado"
                name="estado"
                class="w-full px-4 py-3 bg-arica-cyan/30 rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
              >
                <option value="ACTIVO">Activo</option>
                <option value="INACTIVO">Inactivo</option>
              </select>
            </div>


            <div>
              <label class="block text-arica-blue font-bold mb-2">Descripción</label>
              <textarea
                [(ngModel)]="recintoForm.descripcion"
                name="descripcion"
                rows="4"
                placeholder="Describe las características y servicios del recinto..."
                class="w-full px-4 py-3 bg-arica-cyan/30 rounded-lg outline-none focus:ring-2 focus:ring-arica-blue resize-none"
              ></textarea>
            </div>


            <div>
              <label class="block text-arica-blue font-bold mb-2">Imagen del Recinto</label>
              <div
                (click)="fileInput.click()"
                class="group relative bg-gray-100 border-2 border-dashed border-gray-300 rounded-xl h-48 flex flex-col items-center justify-center mb-2 overflow-hidden cursor-pointer hover:border-arica-blue hover:bg-arica-blue/5 transition-all"
              >
                <img
                  *ngIf="recintoForm.imagen"
                  [src]="getImagenUrl(recintoForm.imagen)"
                  alt="Recinto"
                  class="absolute inset-0 w-full h-full object-cover group-hover:opacity-60 transition-opacity"
                >

                <div class="flex flex-col items-center gap-2 z-10 p-4 text-center">
                  <div class="bg-white p-3 rounded-full shadow-md group-hover:scale-110 transition-transform">
                    <svg class="w-6 h-6 text-gray-400 group-hover:text-arica-blue" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
                    </svg>
                  </div>
                  <div class="space-y-1">
                    <p class="text-xs font-bold text-gray-700 group-hover:text-arica-blue">
                      {{ recintoForm.imagen ? 'Cambiar imagen' : 'Subir imagen' }}
                    </p>
                  </div>
                </div>
              </div>
              <input
                #fileInput
                type="file"
                accept="image/*"
                (change)="onFileSelected($event)"
                class="hidden"
              >
              <p class="text-xs text-gray-500">Selecciona una foto representativa del recinto</p>
            </div>


            <div class="flex gap-4 pt-4">
              <button
                type="button"
                (click)="guardarRecinto()"
                [disabled]="guardando"
                class="flex-1 bg-arica-blue hover:bg-arica-blue-dark text-white font-bold py-3 px-6 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                <svg *ngIf="guardando" class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                {{ guardando ? 'Guardando...' : (modoEdicion ? 'Guardar Cambios' : 'Crear Recinto') }}
              </button>
              <button
                type="button"
                (click)="cerrarModal()"
                [disabled]="guardando"
                class="flex-1 bg-gray-300 hover:bg-gray-400 text-gray-700 font-bold py-3 px-6 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>


    <div
      *ngIf="mostrarModalEliminar"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      (click)="cancelarEliminar()"
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
          <h3 class="text-2xl font-bold text-gray-900 mb-2">¿Eliminar recinto?</h3>
          <p class="text-gray-600 mb-6">
            ¿Estás seguro de que deseas eliminar <strong>{{ recintoAEliminar?.nombre }}</strong>?
            Esta acción no se puede deshacer.
          </p>
          <div class="flex gap-4">
            <button
              (click)="eliminarRecinto()"
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
              (click)="cancelarEliminar()"
              [disabled]="eliminando"
              class="flex-1 bg-gray-300 hover:bg-gray-400 text-gray-700 font-bold py-3 px-6 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Cancelar
            </button>
          </div>
        </div>
      </div>
    </div>


    <div
      *ngIf="mostrarModalDetalle && recintoDetalle"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      (click)="cerrarDetalle()"
    >
      <div
        (click)="$event.stopPropagation()"
        class="bg-white rounded-lg shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto"
      >
        <div class="h-2 overflow-hidden">
          <img src="images/cuatro_colores.png" alt="" class="w-full h-full object-cover">
        </div>


        <div class="relative h-80">
          <img
            *ngIf="recintoDetalle.imagen"
            [src]="getImagenUrl(recintoDetalle.imagen)"
            [alt]="recintoDetalle.nombre"
            class="w-full h-full object-cover"
          >
          <div *ngIf="!recintoDetalle.imagen" class="w-full h-full flex items-center justify-center bg-gray-200">
             <svg class="h-32 w-32 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
               <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
               <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
             </svg>
          </div>
          <div class="absolute top-4 right-4">
            <span
              [class]="getEstadoClass(recintoDetalle.estado)"
              class="px-4 py-2 rounded-full text-sm font-bold"
            >
              {{ recintoDetalle.estado }}
            </span>
          </div>
        </div>

        <div class="p-8">
          <div class="flex justify-between items-start mb-6">
            <div>
              <h2 class="text-4xl font-bold text-arica-blue mb-2">{{ recintoDetalle.nombre }}</h2>
              <span class="bg-arica-cyan text-arica-blue px-3 py-1 rounded-lg text-sm font-semibold">
                {{ recintoDetalle.tipo }}
              </span>
            </div>
            <button
              (click)="cerrarDetalle()"
              class="text-gray-400 hover:text-gray-600 transition-colors"
            >
              <svg class="w-8 h-8" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
              </svg>
            </button>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
            <div class="space-y-4">
              <div class="flex items-start gap-3 p-4 bg-gray-50 rounded-lg">
                <svg class="w-6 h-6 text-arica-blue mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clip-rule="evenodd"/>
                </svg>
                <div>
                  <p class="text-sm text-gray-500">Dirección</p>
                  <p class="font-semibold text-gray-700">{{ recintoDetalle.direccion }}</p>
                </div>
              </div>

              <div class="flex items-start gap-3 p-4 bg-gray-50 rounded-lg">
                <svg class="w-6 h-6 text-arica-blue mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z"/>
                </svg>
                <div>
                  <p class="text-sm text-gray-500">Capacidad</p>
                  <p class="font-semibold text-gray-700">{{ recintoDetalle.capacidad }} personas</p>
                </div>
              </div>
            </div>

            <div class="space-y-4">
              <div class="flex items-start gap-3 p-4 bg-gray-50 rounded-lg">
                <svg class="w-6 h-6 text-arica-blue mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z"/>
                </svg>
                <div>
                  <p class="text-sm text-gray-500">Teléfono</p>
                  <p class="font-semibold text-gray-700">{{ recintoDetalle.telefono }}</p>
                </div>
              </div>

              <div class="flex items-start gap-3 p-4 bg-gray-50 rounded-lg">
                <svg class="w-6 h-6 text-arica-blue mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
                  <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
                </svg>
                <div>
                  <p class="text-sm text-gray-500">Email</p>
                  <p class="font-semibold text-gray-700">{{ recintoDetalle.email }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="mt-6 p-6 bg-gray-50 rounded-lg">
            <h3 class="text-xl font-bold text-arica-blue mb-3">Descripción</h3>
            <p class="text-gray-700 leading-relaxed">{{ recintoDetalle.descripcion }}</p>
          </div>


          <div class="mt-6">
            <button
              (click)="editarDesdeDetalle()"
              class="w-full bg-arica-yellow hover:bg-yellow-500 text-white font-bold py-3 px-6 rounded-lg transition-colors flex items-center justify-center gap-2"
            >
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
              </svg>
              Editar Recinto
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class RecintosComponent implements OnInit {
  filtroEstadoActivo = 'Todos';
  mostrarModal = false;
  mostrarModalEliminar = false;
  mostrarModalDetalle = false;
  modoEdicion = false;
  recintoDetalle: Recinto | null = null;
  recintoAEliminar: Recinto | null = null;
  guardando = false;
  eliminando = false;
  mensajeError = '';
  busqueda = '';
  selectedFile: File | null = null;

  constructor(
    private recintosService: RecintosService,
    private storageService: StorageService,
    private cdr: ChangeDetectorRef
  ) { }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.recintoForm.imagen = e.target.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  filtrosEstado = [
    { label: 'Todos', valor: 'Todos' },
    { label: 'Activo', valor: 'ACTIVO' },
    { label: 'Inactivo', valor: 'INACTIVO' }
  ];

  recintoForm: Recinto = {
    id: 0,
    nombre: '',
    direccion: '',
    capacidad: 0,
    tipo: '',
    descripcion: '',
    imagen: '',
    imagenUrl: '',
    estado: 'ACTIVO',
    telefono: '',
    email: ''
  };

  recintos: Recinto[] = [];

  ngOnInit(): void {
    this.cargarRecintos();
  }

  cargarRecintos(): void {
    console.log(' Cargando recintos...');
    this.recintosService.getRecintos().subscribe({
      next: (data) => {
        console.log(' Recintos recibidos del backend:', data);

        this.recintos = data.map(dto => ({
          id: dto.idRecinto,
          nombre: dto.nombre,
          direccion: dto.ubicacion,
          capacidad: dto.capacidad,
          descripcion: dto.descripcion,


          tipo: 'Recinto',
          imagen: dto.imagen || (dto as any).imagenUrl || '',
          imagenUrl: dto.imagen || (dto as any).imagenUrl || '',
          estado: ((dto as any).estado?.toUpperCase() === 'ACTIVO' || (dto as any).estado?.toUpperCase() === 'INACTIVO')
            ? (dto as any).estado.toUpperCase()
            : 'ACTIVO',
          telefono: '+56 58 220 2000',
          email: 'contacto@muniarica.cl'
        }));
        console.log(' Recintos mapeados:', this.recintos.length, 'recintos');


        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(' Error cargando recintos:', err);
      }
    });
  }

  get recintosFiltrados(): Recinto[] {
    let filtered = this.recintos;


    if (this.filtroEstadoActivo !== 'Todos') {
      filtered = filtered.filter(r => r.estado === this.filtroEstadoActivo);
    }


    if (this.busqueda.trim()) {
      const search = this.busqueda.toLowerCase();
      filtered = filtered.filter(r =>
        r.nombre.toLowerCase().includes(search) ||
        r.tipo.toLowerCase().includes(search) ||
        r.direccion.toLowerCase().includes(search)
      );
    }

    return filtered;
  }

  getEstadoClass(estado: string): string {
    const clases = {
      'ACTIVO': 'bg-green-500 text-white',
      'INACTIVO': 'bg-gray-500 text-white'
    };
    return clases[estado as keyof typeof clases] || 'bg-gray-500 text-white';
  }

  abrirModalCrear(): void {
    this.modoEdicion = false;
    this.recintoForm = {
      id: 0,
      nombre: '',
      direccion: '',
      capacidad: 0,
      tipo: 'Estadio',
      descripcion: '',
      imagen: '',
      imagenUrl: '',
      estado: 'ACTIVO',
      telefono: '',
      email: ''
    };
    this.mostrarModal = true;
  }

  abrirModalEditar(recinto: Recinto): void {
    this.modoEdicion = true;
    this.recintoForm = { ...recinto };
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
  }

  guardarRecinto(): void {
    if (!this.recintoForm.nombre || !this.recintoForm.direccion || !this.recintoForm.capacidad) {
      alert('Por favor completa los campos obligatorios');
      return;
    }

    if (this.selectedFile) {
      this.guardando = true;
      this.storageService.uploadImage(this.selectedFile).subscribe({
        next: (imageUrl: string) => {
          this.recintoForm.imagen = imageUrl;
          this.selectedFile = null;
          this.ejecutarGuardar();
        },
        error: (error: any) => {
          console.error('Error subiendo imagen:', error);
          alert('Error al subir la imagen');
          this.guardando = false;
        }
      });
    } else {
      this.ejecutarGuardar();
    }
  }

  private ejecutarGuardar(): void {
    this.guardando = true;

    const dto = {
      nombre: this.recintoForm.nombre,
      ubicacion: this.recintoForm.direccion,
      descripcion: this.recintoForm.descripcion,
      capacidad: this.recintoForm.capacidad,
      coordenadasGPS: "-18.4783,-70.3126",
      imagen: this.recintoForm.imagen || '',
      imagenUrl: this.recintoForm.imagen || ''
    };

    if (this.modoEdicion) {
      if (!this.recintoForm.id) {
        alert('Error: No se pudo obtener el ID del recinto');
        this.guardando = false;
        return;
      }

      this.recintosService.updateRecinto(this.recintoForm.id, dto).subscribe({
        next: () => {
          this.cargarRecintos();
          this.cerrarModal();
          this.guardando = false;
        },
        error: (err: any) => {
          this.guardando = false;
          console.error(' Error actualizando recinto:', err);
          if (err.status === 400) {
            const mensaje = err.error?.message || err.error || 'No se puede modificar el recinto.';
            const mensajeStr = typeof mensaje === 'string' ? mensaje : JSON.stringify(mensaje);
            if (mensajeStr.toLowerCase().includes('evento')) {
              alert(' No se puede cambiar el estado del recinto.\n\nEste recinto tiene eventos activos asociados. Primero debe finalizar o reasignar esos eventos.');
            } else {
              alert(' Error de validación: ' + mensajeStr);
            }
          } else {
            alert('Error actualizando recinto: ' + (err.error?.message || err.message || 'Error desconocido'));
          }
        }
      });
    } else {
      this.recintosService.createRecinto(dto).subscribe({
        next: () => {
          this.cargarRecintos();
          this.cerrarModal();
          this.guardando = false;
        },
        error: (err: any) => {
          console.error(' Error creando recinto:', err);
          alert('Error creando recinto: ' + (err.error?.message || err.message || 'Error desconocido'));
          this.guardando = false;
        }
      });
    }
  }

  confirmarEliminar(recinto: Recinto): void {
    this.recintoAEliminar = recinto;
    this.mostrarModalEliminar = true;
  }

  eliminarRecinto(): void {
    if (this.recintoAEliminar) {
      this.eliminando = true;
      const recintoId = this.recintoAEliminar.id;

      this.recintosService.deleteRecinto(recintoId).subscribe({
        next: () => {
          console.log(' Recinto eliminado exitosamente:', recintoId);


          this.cargarRecintos();


          this.eliminando = false;
          this.cancelarEliminar();


          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error(' Error eliminando recinto:', err);
          this.eliminando = false;
          this.cancelarEliminar();


          if (err.status === 403) {
            alert(' No se puede eliminar este recinto.\n\nPosibles razones:\n• El recinto está siendo usado por uno o más eventos\n• No tienes permisos suficientes\n\nPrimero elimina o reasigna los eventos que usan este recinto.');
          } else if (err.status === 404) {
            alert(' El recinto no existe o ya fue eliminado.');

            this.cargarRecintos();
          } else {
            alert('Error eliminando recinto: ' + (err.message || 'Error desconocido'));
          }


          this.cdr.detectChanges();
        }
      });
    }
  }

  getImagenUrl(imagen: string | undefined): string {
    if (!imagen) return '';
    if (imagen.startsWith('http') || imagen.startsWith('data:')) {
      return imagen;
    }
    return `${environment.apiUrl}/uploads/${imagen}`;
  }

  cancelarEliminar(): void {
    this.mostrarModalEliminar = false;
    this.recintoAEliminar = null;
  }

  verDetalle(recinto: Recinto): void {
    this.recintoDetalle = recinto;
    this.mostrarModalDetalle = true;
  }

  cerrarDetalle(): void {
    this.mostrarModalDetalle = false;
    this.recintoDetalle = null;
  }

  editarDesdeDetalle(): void {
    if (this.recintoDetalle) {
      this.cerrarDetalle();
      this.abrirModalEditar(this.recintoDetalle);
    }
  }
}