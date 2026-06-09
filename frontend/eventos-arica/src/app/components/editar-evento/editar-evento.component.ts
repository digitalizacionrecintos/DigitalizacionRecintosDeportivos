import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { EventosService } from '../../services/eventos.service';
import { EncargadosService } from '../../services/encargados.service';
import { CategoriasService } from '../../services/categorias.service';
import { StorageService } from '../../services/storage.service';
import { EncargadoDTO, Usuario, EditarEventoDTO, CategoriaDTO, RecintoDTO } from '../../models/api.models';
import { Curso } from '../../models/curso';
import { environment } from '../../../environments/environment';
import { RecintosService } from '../../services/recintos.service';
import { CursoService } from '../../services/curso.service';

@Component({
  selector: 'app-editar-evento',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './editar-evento.component.html',
  styles: []
})

export class EditarEventoComponent implements OnInit {
  encargados: Usuario[] = [];
  categorias: CategoriaDTO[] = [];
  recintos: RecintoDTO[] = [];
  cursos: Curso[] = [];
  eventoId: number | null = null;
  cargando = true;
  hoy = new Date();
  evento = {
    titulo: '',
    descripcion: '',
    recinto: '',
    recintoId: null as number | null,
    encargado: '',
    encargadoId: null as number | null,
    categoriaId: null as number | null,
    categoria: null as CategoriaDTO | null,
    fecha: '',
    fechaFin: '',
    horaInicio: '',
    horaFin: '',
    capacidad: '',
    imagen: '',
    cursoId: null as number | null,
    cursoNombre: '', 
    maximoPorInscripcion: null as number | null
  };

  selectedFile: File | null = null;

  editando = {
    titulo: false,
    descripcion: false,
    recinto: false,
    encargado: false,
    categoria: false,
    curso: false
  };


  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private eventosService: EventosService,
    private encargadosService: EncargadosService,
    private categoriasService: CategoriasService,
    private cursoService: CursoService,
    private storageService: StorageService,
    private cdr: ChangeDetectorRef,
    private recintosService: RecintosService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.eventoId = parseInt(id, 10);
      this.cargarEvento();
    }
    this.cargarEncargados();
    this.cargarCategorias();
    this.cargarRecintos();
    this.cargarCursos();
  }

  cargarCursos(): void {
    this.cursoService.listarCursos().subscribe({
      next: (data) => this.cursos = data,
      error: (err) => console.error('Error cargando cursos', err)
    });
  }


  toggleEdit(campo: keyof typeof this.editando): void {
    this.editando[campo] = !this.editando[campo];
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        alert('La imagen no puede pesar mÃ¡s de 5MB');
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


  cargarEvento(): void {
    if (!this.eventoId) {
      console.error(' No hay eventoId');
      return;
    }

    console.log(' Cargando evento ID:', this.eventoId);
    this.cargando = true;

    this.eventosService.getEventoById(this.eventoId).subscribe({
      next: (eventoDTO) => {
        console.log(' Evento cargado:', eventoDTO);


        const fechaInicio = eventoDTO.fechaInicio;
        const horaInicio = eventoDTO.horaInicio;
        const horaFin = eventoDTO.horaFin;

        console.log(' Fecha inicio:', fechaInicio);
        console.log(' Hora inicio:', horaInicio);
        console.log(' Hora fin:', horaFin);
        console.log(' Encargado completo:', eventoDTO.encargado);
        console.log(' Encargado ID:', eventoDTO.encargadoId);
        console.log(' Encargado keys:', eventoDTO.encargado ? Object.keys(eventoDTO.encargado) : 'no encargado');


        const horaInicioStr = horaInicio.split('T')[1]?.substring(0, 5) || '';
        const horaFinStr = horaFin.split('T')[1]?.substring(0, 5) || '';

        this.evento = {
          titulo: eventoDTO.titulo,
          descripcion: eventoDTO.descripcion,
          recinto: eventoDTO.recinto?.nombre || '',
          recintoId: eventoDTO.recintoId || eventoDTO.recinto?.idRecinto || null,
          encargado: eventoDTO.encargado?.nombre
            ? `${eventoDTO.encargado.nombre} ${eventoDTO.encargado.apellido || ''}`.trim()
            : '',
          encargadoId: eventoDTO.encargadoId || eventoDTO.encargado?.idUsuario || null,
          categoriaId: eventoDTO.categoriaId || eventoDTO.categoria?.id || null,
          categoria: eventoDTO.categoria || null,
          fecha: fechaInicio,
          fechaFin: eventoDTO.fechaFin || '',
          horaInicio: horaInicioStr,
          horaFin: horaFinStr,
          capacidad: eventoDTO.cupoMaximo.toString(),
          imagen: eventoDTO.imagen || eventoDTO.imagenUrl || '',
          cursoId: eventoDTO.cursoId || (eventoDTO as any).curso?.idCurso || null,
          cursoNombre: eventoDTO.cursoNombre || (eventoDTO as any).curso?.nombre || '',
          maximoPorInscripcion: eventoDTO.maximoPorInscripcion || null
        };

        console.log(' Evento parseado:', this.evento);
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error(' Error cargando evento:', error);
        console.error(' Detalles del error:', error.message);
        console.error(' Status:', error.status);
        alert(`Error al cargar el evento: ${error.message || 'Revisa la consola'}`);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }


  cargarRecintos(): void {
    this.recintosService.getRecintos().subscribe({
      next: (recintos) => {
        this.recintos = recintos;
        console.log('Recintos disponibles cargados:', recintos);
      },
      error: (error) => {
        console.error('Error cargando recintos:', error);
      }
    });
  }
  cargarEncargados(): void {
    this.encargadosService.getEncargados().subscribe({
      next: (encargados) => {
        this.encargados = encargados;
        console.log('Encargados disponibles cargados:', encargados);
      },
      error: (error) => {
        console.error('Error cargando encargados:', error);
      }
    });
  }

  cargarCategorias(): void {
    this.categoriasService.getCategorias().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
        console.log('CategorÃ­as disponibles cargadas:', categorias);
      },
      error: (error) => {
        console.error('Error cargando categorÃ­as:', error);
      }
    });
  }

  volver(): void {
    this.router.navigate(['/eventos']);
  }

  guardarCambios(): void {
    if (!this.eventoId) {
      alert('Error: No se pudo identificar el evento');
      return;
    }

    if (this.evento.descripcion && this.evento.descripcion.length > 255) {
      alert('La descripciÃ³n no puede exceder los 255 caracteres');
      return;
    }

    if (this.evento.horaFin <= this.evento.horaInicio) {
      alert('La hora de fin debe ser posterior a la hora de inicio');
      return;
    }
    if (this.selectedFile) {
      this.cargando = true;
      this.storageService.uploadImage(this.selectedFile).subscribe({
        next: (imageUrl: string) => {
          this.evento.imagen = imageUrl;
          this.selectedFile = null;
          this.ejecutarGuardar();
        },
        error: (error: any) => {
          console.error(' Error subiendo imagen:', error);
          alert('Error al subir la imagen. Intente nuevamente.');
          this.cargando = false;
        }
      });
    } else {
      this.ejecutarGuardar();
    }
  }

  private ejecutarGuardar(): void {
    console.log(' Guardando cambios del evento:', this.evento);
    this.cargando = true;


    const editarEventoDTO: EditarEventoDTO = {
      descripcion: this.evento.descripcion,
      horaInicio: `${this.evento.fecha}T${this.evento.horaInicio}:00`,
      horaFin: `${this.evento.fechaFin || this.evento.fecha}T${this.evento.horaFin}:00`,
      fechaInicio: this.evento.fecha,
      fechaFin: this.evento.fechaFin || this.evento.fecha,
      cupoMaximo: parseInt(this.evento.capacidad),
      recintoId: parseInt(this.evento.recinto) || 1,
      encargadoId: this.evento.encargadoId!,
      publicoObjetivo: 'Todos',
      categoriaId: this.evento.categoriaId!,
      imagen: this.evento.imagen || '',
      imagenUrl: this.evento.imagen || '',
      cursoId: this.evento.cursoId || undefined,
      maximoPorInscripcion: this.evento.maximoPorInscripcion || undefined 
    };

    console.log(' Enviando al backend:', editarEventoDTO);

    this.eventosService.updateEvento(this.eventoId!, editarEventoDTO).subscribe({
      next: (response) => {
        console.log(' Evento actualizado exitosamente:', response);
        alert('Evento actualizado exitosamente');
        this.cargando = false;


        Object.keys(this.editando).forEach(key => {
          this.editando[key as keyof typeof this.editando] = false;
        });

        this.cargarEvento();
      },
      error: (error) => {
        console.error(' Error actualizando evento:', error);
        alert(`Error al actualizar el evento: ${error.error?.message || error.message || 'Error desconocido'}`);
        this.cargando = false;
      }
    });
  }

  getImagenUrl(imagen: string | undefined): string {
    if (!imagen) return '';
    if (imagen.startsWith('http') || imagen.startsWith('data:')) {
      return imagen;
    }

    return `${environment.apiUrl}/uploads/${imagen}`;
  }

  verificarFecha(horaInicio: string, horaFin: string): void {
    if (horaFin <= horaInicio) {
      alert('La hora de fin debe ser posterior a la hora de inicio');
      this.evento.horaInicio = '';
    }
  }
}