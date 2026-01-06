import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoriasService } from '../../services/categorias.service';
import { CategoriaDTO } from '../../models/api.models';

@Component({
  selector: 'app-categorias',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="ml-64 min-h-screen bg-gradient-to-br from-arica-blue via-blue-700 to-cyan-500 p-8">
      <div class="max-w-7xl mx-auto">


        <div *ngIf="mensajeExito" class="mb-4 bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative">
          {{ mensajeExito }}
        </div>
        <div *ngIf="mensajeError" class="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative">
          {{ mensajeError }}
        </div>


        <div class="bg-white rounded-lg shadow-2xl overflow-hidden mb-8">
          <div class="h-2 overflow-hidden">
            <img src="images/cuatro_colores.png" alt="" class="w-full h-full object-cover">
          </div>
          <div class="p-8 flex justify-between items-center">
            <div>
              <h1 class="text-4xl font-bold text-arica-blue mb-2">Gestión de Categorías</h1>
              <p class="text-gray-600">Administra las categorías de eventos municipales</p>
            </div>
            <button
              (click)="abrirModalCrear()"
              class="bg-arica-green hover:bg-green-600 text-white font-bold py-3 px-6 rounded-lg transition-colors flex items-center gap-2 shadow-lg"
            >
              <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd"/>
              </svg>
              Crear Categoría
            </button>
          </div>
        </div>


        <div class="bg-white rounded-lg shadow-lg p-6 mb-8">
          <div class="flex items-center bg-gray-100 rounded-lg px-4 py-3">
            <svg class="w-5 h-5 text-gray-500" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd"/>
            </svg>
            <input
              type="text"
              [(ngModel)]="busqueda"
              placeholder="Buscar por nombre o descripción..."
              class="flex-1 bg-transparent outline-none text-gray-700 ml-3"
            >
          </div>
        </div>


        <div *ngIf="cargando" class="text-center py-8">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-white"></div>
          <p class="text-white mt-4">Cargando...</p>
        </div>


        <div *ngIf="!cargando" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            *ngFor="let categoria of categoriasFiltradas"
            class="bg-white rounded-lg shadow-xl overflow-hidden hover:shadow-2xl transition-all hover:scale-105"
          >

            <div class="bg-gradient-to-br from-arica-blue to-blue-600 p-6 text-white">
              <div class="flex items-center gap-4">

                <div class="w-16 h-16 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center">
                  <svg class="w-8 h-8" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M7 3a1 1 0 000 2h6a1 1 0 100-2H7zM4 7a1 1 0 011-1h10a1 1 0 110 2H5a1 1 0 01-1-1zM2 11a2 2 0 012-2h12a2 2 0 012 2v4a2 2 0 01-2 2H4a2 2 0 01-2-2v-4z"/>
                  </svg>
                </div>
                <div class="flex-1">
                  <h3 class="text-xl font-bold">{{ categoria.nombre }}</h3>
                </div>
              </div>
            </div>


            <div class="p-6">
              <p class="text-gray-700 text-sm line-clamp-3">{{ categoria.descripcion }}</p>
            </div>


            <div class="p-4 bg-gray-50 flex gap-2">
              <button
                (click)="abrirModalEditar(categoria)"
                class="flex-1 bg-arica-yellow hover:bg-yellow-500 text-white font-bold py-2 px-4 rounded-lg transition-colors flex items-center justify-center gap-2"
              >
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
                </svg>
                Editar
              </button>
              <button
                (click)="confirmarEliminar(categoria)"
                class="flex-1 bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded-lg transition-colors flex items-center justify-center gap-2"
              >
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd"/>
                </svg>
                Eliminar
              </button>
            </div>
          </div>
        </div>


        <div *ngIf="!cargando && categoriasFiltradas.length === 0" class="text-center py-12">
          <svg class="w-24 h-24 mx-auto text-white/50 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"/>
          </svg>
          <p class="text-white text-xl">No se encontraron categorías</p>
        </div>
      </div>


      <div
        *ngIf="mostrarModal"
        class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4"
        (click)="cerrarModal()"
      >
        <div
          (click)="$event.stopPropagation()"
          class="bg-white rounded-lg shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
        >
          <div class="h-2 overflow-hidden">
            <img src="images/cuatro_colores.png" alt="" class="w-full h-full object-cover">
          </div>

          <div class="p-8">
            <h2 class="text-3xl font-bold text-arica-blue mb-6">
              {{ modoEdicion ? 'Editar Categoría' : 'Crear Nueva Categoría' }}
            </h2>


            <div *ngIf="mensajeError" class="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
              {{ mensajeError }}
            </div>

            <form class="space-y-6">

              <div>
                <label class="block text-arica-blue font-bold mb-2">Nombre *</label>
                <input
                  type="text"
                  [(ngModel)]="categoriaForm.nombre"
                  name="nombre"
                  placeholder="Ej: Deportes, Cultura, Educación"
                  [disabled]="cargando"
                  class="w-full px-4 py-3 bg-arica-cyan/30 rounded-lg outline-none focus:ring-2 focus:ring-arica-blue disabled:opacity-50"
                >
              </div>


              <div>
                <label class="block text-arica-blue font-bold mb-2">Descripción *</label>
                <textarea
                  [(ngModel)]="categoriaForm.descripcion"
                  name="descripcion"
                  rows="4"
                  placeholder="Describe esta categoría..."
                  [disabled]="cargando"
                  class="w-full px-4 py-3 bg-arica-cyan/30 rounded-lg outline-none focus:ring-2 focus:ring-arica-blue disabled:opacity-50 resize-none"
                ></textarea>
              </div>


              <div class="flex gap-4 pt-4">
                <button
                  type="button"
                  (click)="guardarCategoria()"
                  [disabled]="cargando"
                  class="flex-1 bg-arica-blue hover:bg-arica-blue-dark text-white font-bold py-3 px-6 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <span *ngIf="!cargando">{{ modoEdicion ? 'Actualizar' : 'Crear' }}</span>
                  <span *ngIf="cargando">Guardando...</span>
                </button>
                <button
                  type="button"
                  (click)="cerrarModal()"
                  [disabled]="cargando"
                  class="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-bold py-3 px-6 rounded-lg transition-colors disabled:opacity-50"
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
        class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4"
        (click)="cancelarEliminar()"
      >
        <div
          (click)="$event.stopPropagation()"
          class="bg-white rounded-lg shadow-2xl max-w-md w-full p-8"
        >
          <div class="text-center">
            <svg class="w-16 h-16 text-red-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
            </svg>
            <h3 class="text-2xl font-bold text-gray-900 mb-2">¿Eliminar Categoría?</h3>
            <p class="text-gray-600 mb-6">
              ¿Estás seguro de que deseas eliminar la categoría <strong>{{ categoriaAEliminar?.nombre }}</strong>?
              Esta acción no se puede deshacer.
            </p>
            <div class="flex gap-4">
              <button
                (click)="eliminarCategoria()"
                [disabled]="cargando"
                class="flex-1 bg-red-500 hover:bg-red-600 text-white font-bold py-3 px-6 rounded-lg transition-colors disabled:opacity-50"
              >
                <span *ngIf="!cargando">Sí, Eliminar</span>
                <span *ngIf="cargando">Eliminando...</span>
              </button>
              <button
                (click)="cancelarEliminar()"
                [disabled]="cargando"
                class="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-bold py-3 px-6 rounded-lg transition-colors disabled:opacity-50"
              >
                Cancelar
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class CategoriasComponent implements OnInit {
  busqueda = '';
  mostrarModal = false;
  mostrarModalEliminar = false;
  modoEdicion = false;
  categoriaAEliminar: CategoriaDTO | null = null;


  cargando = false;
  mensajeError = '';
  mensajeExito = '';

  categoriaForm: CategoriaDTO = {
    id: 0,
    nombre: '',
    descripcion: ''
  };

  categorias: CategoriaDTO[] = [];

  constructor(private categoriasService: CategoriasService) { }

  ngOnInit(): void {
    this.cargarCategorias();
  }

  cargarCategorias(): void {
    this.cargando = true;
    this.mensajeError = '';

    this.categoriasService.getCategorias().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
        this.cargando = false;
        console.log(' Categorías cargadas:', this.categorias);
      },
      error: (error) => {
        console.error(' Error cargando categorías:', error);
        this.mensajeError = 'Error al cargar las categorías. Por favor intenta de nuevo.';
        this.cargando = false;
      }
    });
  }

  get categoriasFiltradas(): CategoriaDTO[] {
    let filtered = this.categorias;


    if (this.busqueda.trim()) {
      const search = this.busqueda.toLowerCase();
      filtered = filtered.filter(c =>
        c.nombre.toLowerCase().includes(search) ||
        c.descripcion.toLowerCase().includes(search)
      );
    }

    return filtered;
  }

  abrirModalCrear(): void {
    this.modoEdicion = false;
    this.categoriaForm = {
      id: 0,
      nombre: '',
      descripcion: ''
    };
    this.mensajeError = '';
    this.mensajeExito = '';
    this.mostrarModal = true;
  }

  abrirModalEditar(categoria: CategoriaDTO): void {
    this.modoEdicion = true;
    this.categoriaForm = { ...categoria };
    this.mensajeError = '';
    this.mensajeExito = '';
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
  }

  guardarCategoria(): void {

    if (!this.categoriaForm.nombre || !this.categoriaForm.descripcion) {
      this.mensajeError = 'Por favor completa todos los campos obligatorios';
      return;
    }

    this.cargando = true;
    this.mensajeError = '';

    if (this.modoEdicion) {

      this.categoriasService.updateCategoria(this.categoriaForm.id, this.categoriaForm).subscribe({
        next: (response) => {
          console.log(' Categoría actualizada:', response);
          this.mensajeExito = 'Categoría actualizada exitosamente';
          this.cargarCategorias();
          this.cerrarModal();
          this.cargando = false;


          setTimeout(() => this.mensajeExito = '', 3000);
        },
        error: (error) => {
          console.error(' Error actualizando categoría:', error);
          this.mensajeError = error.error?.message || 'Error al actualizar la categoría';
          this.cargando = false;
        }
      });
    } else {

      this.categoriasService.createCategoria(this.categoriaForm).subscribe({
        next: (response) => {
          console.log(' Categoría creada:', response);
          this.mensajeExito = 'Categoría creada exitosamente';
          this.cargarCategorias();
          this.cerrarModal();
          this.cargando = false;


          setTimeout(() => this.mensajeExito = '', 3000);
        },
        error: (error) => {
          console.error(' Error creando categoría:', error);
          this.mensajeError = error.error?.message || 'Error al crear la categoría';
          this.cargando = false;
        }
      });
    }
  }

  confirmarEliminar(categoria: CategoriaDTO): void {
    this.categoriaAEliminar = categoria;
    this.mostrarModalEliminar = true;
  }

  eliminarCategoria(): void {
    if (this.categoriaAEliminar) {
      this.cargando = true;

      console.log(' Intentando eliminar categoría:', this.categoriaAEliminar);

      this.categoriasService.deleteCategoria(this.categoriaAEliminar.id).subscribe({
        next: () => {
          console.log(' Categoría eliminada exitosamente');
          this.mensajeExito = 'Categoría eliminada exitosamente';
          this.cargarCategorias();
          this.cancelarEliminar();
          this.cargando = false;


          setTimeout(() => this.mensajeExito = '', 3000);
        },
        error: (error) => {
          console.error(' Error eliminando categoría:', error);

          let mensajeDetallado = 'Error al eliminar la categoría';

          if (error.status === 404) {
            mensajeDetallado = 'Categoría no encontrada';
          } else if (error.status === 403) {
            mensajeDetallado = 'No tienes permisos para eliminar esta categoría';
          } else if (error.status === 409 || error.status === 400) {
            mensajeDetallado = error.error?.message || 'No se puede eliminar la categoría (puede estar asignada a eventos)';
          } else if (error.error?.message) {
            mensajeDetallado = error.error.message;
          }

          this.mensajeError = mensajeDetallado;
          this.cargando = false;
          this.cancelarEliminar();


          setTimeout(() => this.mensajeError = '', 8000);
        }
      });
    }
  }

  cancelarEliminar(): void {
    this.mostrarModalEliminar = false;
    this.categoriaAEliminar = null;
  }
}