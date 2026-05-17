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
}

@Component({
  selector: 'app-recintos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recintos.component.html',
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
      imagenUrl: this.recintoForm.imagen || '',
      estado: this.recintoForm.estado.toUpperCase() || '',
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