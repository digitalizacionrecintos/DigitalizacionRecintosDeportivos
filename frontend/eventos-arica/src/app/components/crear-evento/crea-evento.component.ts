import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EventosService } from '../../services/eventos.service';
import { RecintosService } from '../../services/recintos.service';
import { StorageService } from '../../services/storage.service';
import { EncargadosService } from '../../services/encargados.service';
import { CategoriasService } from '../../services/categorias.service';
import { CursoService } from '../../services/curso.service';
import { CrearEventoDTO, Usuario, CategoriaDTO } from '../../models/api.models';
import { Curso } from '../../models/curso';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-crear-evento',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './crear-evento.component.html',
  styles: []
})
export class CrearEventoComponent implements OnInit {
  evento = {
    titulo: '',
    descripcion: '',
    recintoId: null as number | null,
    encargadoId: null as number | null,
    categoriaId: null as number | null,
    publicoObjetivo: '',
    fecha: '',
    fechaFin: '',
    horaInicio: '',
    horaFin: '',
    capacidad: null as number | null,
    imagen: '' as string | undefined,
    cursoId: null as number | null
  };

  selectedFile: File | null = null;

  recintos: any[] = [];
  encargados: Usuario[] = [];
  categorias: CategoriaDTO[] = [];
  cursos: Curso[] = [];
  guardando = false;
  mensaje: { tipo: 'error' | 'success', texto: string } | null = null;
  hoy = new Date();

  constructor(
    private router: Router,
    private eventosService: EventosService,
    private recintosService: RecintosService,
    private encargadosService: EncargadosService,
    private categoriasService: CategoriasService,
    private cursoService: CursoService,
    private storageService: StorageService
  ) { }

  ngOnInit(): void {
    this.cargarRecintos();
    this.cargarEncargados();
    this.cargarCategorias();
    this.cargarCursos();
  }

  cargarCursos(): void {
    this.cursoService.listarCursos().subscribe({
      next: (data) => this.cursos = data,
      error: (err) => console.error('Error cargando cursos', err)
    });
  }


  cargarRecintos(): void {
    this.recintosService.getRecintosDisponibles().subscribe({
      next: (recintos) => {
        this.recintos = recintos;
        console.log('Recintos disponibles:', recintos);
      },
      error: (error) => {
        console.error('Error cargando recintos:', error);
        this.mostrarMensaje('error', 'Error al cargar recintos');
      }
    });
  }

  cargarEncargados(): void {
    this.encargadosService.getEncargados().subscribe({
      next: (encargados) => {
        this.encargados = encargados;
        console.log('Encargados disponibles:', encargados);
      },
      error: (error) => {
        console.error('Error cargando encargados:', error);
        this.mostrarMensaje('error', 'Error al cargar encargados');
      }
    });
  }

  cargarCategorias(): void {
    this.categoriasService.getCategorias().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
        console.log('Categorías disponibles:', categorias);
      },
      error: (error) => {
        console.error('Error cargando categorías:', error);
        this.mostrarMensaje('error', 'Error al cargar categorías');
      }
    });
  }

  guardarEvento(): void {
    if (!this.validarFormulario()) return;

    if (this.selectedFile) {
      this.guardando = true;
      this.storageService.uploadImage(this.selectedFile).subscribe({
        next: (imageUrl: string) => {
          this.evento.imagen = imageUrl;
          this.selectedFile = null;
          this.ejecutarCreacion();
        },
        error: (error: any) => {
          console.error('Error subiendo imagen:', error);
          this.mostrarMensaje('error', 'Error al subir la imagen');
          this.guardando = false;
        }
      });
    } else {
      this.ejecutarCreacion();
    }
  }

  private ejecutarCreacion(): void {
    this.guardando = true;
    this.mensaje = null;
    const fechaInicio = this.evento.fecha;
    const horaInicioISO = `${this.evento.fecha}T${this.evento.horaInicio}:00`;
    const horaFinISO = `${this.evento.fecha}T${this.evento.horaFin}:00`;

    const eventoDTO: CrearEventoDTO = {
      titulo: this.evento.titulo,
      descripcion: this.evento.descripcion,
      horaInicio: horaInicioISO,
      horaFin: horaFinISO,
      fechaInicio: fechaInicio,
      categoriaId: this.evento.categoriaId!,
      cupoMaximo: this.evento.capacidad!,
      publicoObjetivo: this.evento.publicoObjetivo,
      recintoId: this.evento.recintoId!,
      encargadoId: this.evento.encargadoId!,
      imagen: this.evento.imagen || '',
      imagenUrl: this.evento.imagen || '',
      cursoId: this.evento.cursoId || undefined
    };

    console.log('Enviando evento:', eventoDTO);

    this.eventosService.createEvento(eventoDTO).subscribe({
      next: (response) => {
        console.log('Evento creado:', response);
        this.mostrarMensaje('success', '¡Evento creado exitosamente!');
        setTimeout(() => {
          this.router.navigate(['/eventos']);
        }, 1500);
      },
      error: (error: any) => {
        console.error('Error creando evento:', error);
        this.mostrarMensaje('error', error.error?.message || 'Error al crear el evento');
        this.guardando = false;
      }
    });
  }

  validarFormulario(): boolean {
    if (!this.evento.titulo.trim()) {
      this.mostrarMensaje('error', 'El título es obligatorio');
      return false;
    }
    if (!this.evento.descripcion.trim()) {
      this.mostrarMensaje('error', 'La descripción es obligatoria');
      return false;
    }
    if (this.evento.descripcion.length > 255) {
      this.mostrarMensaje('error', 'La descripción no puede exceder los 255 caracteres');
      return false;
    }
    if (!this.evento.recintoId) {
      this.mostrarMensaje('error', 'Debe seleccionar un recinto');
      return false;
    }
    if (!this.evento.encargadoId) {
      this.mostrarMensaje('error', 'Debe seleccionar un encargado');
      return false;
    }
    if (!this.evento.capacidad || this.evento.capacidad <= 0) {
      this.mostrarMensaje('error', 'La capacidad debe ser mayor a 0');
      return false;
    }
    if (!this.evento.fecha) {
      this.mostrarMensaje('error', 'La fecha es obligatoria');
      return false;
    }
    if (!this.evento.horaInicio) {
      this.mostrarMensaje('error', 'La hora de inicio es obligatoria');
      return false;
    }
    if (!this.evento.horaFin) {
      this.mostrarMensaje('error', 'La hora de fin es obligatoria');
      return false;
    }

    if (this.evento.horaFin <= this.evento.horaInicio) {
      this.mostrarMensaje('error', 'La hora de fin debe ser posterior a la hora de inicio');
      return false;
    }
    return true;
  }

  mostrarMensaje(tipo: 'error' | 'success', texto: string): void {
    this.mensaje = { tipo, texto };
    setTimeout(() => {
      this.mensaje = null;
    }, 5000);
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {

      if (file.size > 5 * 1024 * 1024) {
        this.mostrarMensaje('error', 'La imagen no puede pesar más de 5MB');
        event.target.value = '';
        return;
      }

      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.evento.imagen = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  volver(): void {
    this.router.navigate(['/eventos']);
  }

  getImagenUrl(imagen: string | undefined): string {
    if (!imagen) return '';
    if (imagen.startsWith('http') || imagen.startsWith('data:')) {
      return imagen;
    }
    return `${environment.apiUrl}/uploads/${imagen}`;
  }
}