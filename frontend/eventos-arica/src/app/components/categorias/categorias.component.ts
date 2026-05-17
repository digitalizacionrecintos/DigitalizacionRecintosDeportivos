import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoriasService } from '../../services/categorias.service';
import { CategoriaDTO } from '../../models/api.models';

@Component({
  selector: 'app-categorias',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl:'./categorias.component.html',
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