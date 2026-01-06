import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { EventosService } from '../../services/eventos.service';
import { EncargadosService } from '../../services/encargados.service';
import { CategoriasService } from '../../services/categorias.service';
import { StorageService } from '../../services/storage.service';
import { EncargadoDTO, Usuario, EditarEventoDTO, CategoriaDTO } from '../../models/api.models';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-editar-evento',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="ml-64 min-h-screen bg-gradient-to-br from-arica-blue via-blue-700 to-cyan-500 p-8">
      <div class="max-w-4xl mx-auto bg-white rounded-lg shadow-2xl overflow-hidden">

        <div class="h-2 overflow-hidden">
          <img src="images/cuatro_colores.png" alt="" class="w-full h-full object-cover">
        </div>

        <div class="p-8">
          <h1 class="text-3xl font-bold text-arica-blue mb-8 border-b-2 border-gray-200 pb-4">
            Editar Evento
          </h1>


          <div *ngIf="cargando" class="text-center py-8">
            <p class="text-gray-600">Cargando datos del evento...</p>
          </div>

          <div *ngIf="!cargando" class="grid grid-cols-1 lg:grid-cols-3 gap-8">

            <div class="lg:col-span-2 space-y-6">

              <div>
                <label class="block text-arica-blue font-bold text-lg mb-2">Titulo</label>
                <div class="flex items-center gap-2">
                  <input
                    type="text"
                    [(ngModel)]="evento.titulo"
                    [disabled]="!editando.titulo"
                    [class.bg-gray-100]="!editando.titulo"
                    class="flex-1 px-4 py-3 bg-arica-cyan rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                  >
                  <button
                    (click)="toggleEdit('titulo')"
                    class="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                  >
                    <svg class="w-6 h-6 text-gray-600" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
                    </svg>
                  </button>
                </div>
              </div>


              <div>
                <label class="block text-arica-blue font-bold text-lg mb-2">Descripcion</label>
                <div class="flex items-start gap-2">
                  <textarea
                    [(ngModel)]="evento.descripcion"
                    [disabled]="!editando.descripcion"
                    [class.bg-gray-100]="!editando.descripcion"
                    rows="4"
                    class="flex-1 px-4 py-3 bg-arica-cyan rounded-lg outline-none focus:ring-2 focus:ring-arica-blue resize-none"
                  ></textarea>
                  <button
                    (click)="toggleEdit('descripcion')"
                    class="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                  >
                    <svg class="w-6 h-6 text-gray-600" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
                    </svg>
                  </button>
                </div>
              </div>


              <div>
                <label class="block text-arica-blue font-bold text-lg mb-2">Recinto</label>
                <div class="flex items-center gap-2">
                  <input
                    type="text"
                    [(ngModel)]="evento.recinto"
                    [disabled]="!editando.recinto"
                    [class.bg-gray-100]="!editando.recinto"
                    class="flex-1 px-4 py-3 bg-arica-cyan rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                  >
                  <button
                    (click)="toggleEdit('recinto')"
                    class="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                  >
                    <svg class="w-6 h-6 text-gray-600" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
                    </svg>
                  </button>
                </div>
              </div>


              <div>
                <label class="block text-arica-blue font-bold text-lg mb-2">Encargado evento</label>
                <div class="flex items-center gap-2">
                  <select
                    [(ngModel)]="evento.encargadoId"
                    [disabled]="!editando.encargado"
                    [class.bg-gray-100]="!editando.encargado"
                    class="flex-1 px-4 py-3 bg-arica-cyan rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                  >
                    <option [value]="null" disabled>Seleccionar encargado</option>
                    <option *ngFor="let enc of encargados" [value]="enc.idUsuario">
                      {{enc.nombre}} {{enc.apellido}}
                    </option>
                  </select>
                  <button
                    (click)="toggleEdit('encargado')"
                    class="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                  >
                    <svg class="w-6 h-6 text-gray-600" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
                    </svg>
                  </button>
                </div>
              </div>


              <div>
                <label class="block text-arica-blue font-bold text-lg mb-2">Categoría</label>
                <div class="flex items-center gap-2">
                  <select
                    [(ngModel)]="evento.categoriaId"
                    [disabled]="!editando.categoria"
                    [class.bg-gray-100]="!editando.categoria"
                    class="flex-1 px-4 py-3 bg-arica-cyan rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                  >
                    <option [ngValue]="null" disabled>Seleccionar categoría</option>
                    <option *ngFor="let cat of categorias" [ngValue]="cat.id">
                      {{cat.nombre}}
                    </option>
                  </select>
                  <button
                    (click)="toggleEdit('categoria')"
                    class="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                  >
                    <svg class="w-6 h-6 text-gray-600" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
                    </svg>
                  </button>
                </div>
              </div>


              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-arica-blue font-bold text-lg mb-2">Fecha del Evento</label>
                  <input
                    type="date"
                    [(ngModel)]="evento.fecha"
                    class="w-full px-4 py-3 bg-arica-cyan rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                  >
                </div>

                <div>
                  <label class="block text-arica-blue font-bold text-lg mb-2">Hora de Inicio</label>
                  <input
                    type="time"
                    [(ngModel)]="evento.horaInicio"
                    class="w-full px-4 py-3 bg-blue-200 rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                  >
                </div>
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-arica-blue font-bold text-lg mb-2">Fecha de Fin (opcional)</label>
                  <input
                    type="date"
                    [(ngModel)]="evento.fechaFin"
                    class="w-full px-4 py-3 bg-arica-cyan rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                  >
                </div>

                <div>
                  <label class="block text-arica-blue font-bold text-lg mb-2">Hora de Fin</label>
                  <input
                    type="time"
                    [(ngModel)]="evento.horaFin"
                    class="w-full px-4 py-3 bg-blue-200 rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                  >
                </div>
              </div>


              <div class="pt-4">
                <button
                  (click)="guardarCambios()"
                  class="w-full bg-arica-blue hover:bg-arica-blue-dark text-white font-bold py-3 px-8 rounded-lg transition-colors"
                >
                  Guardar
                </button>
              </div>
            </div>


            <div class="flex flex-col justify-between">
              <div class="space-y-6">

                <div>
                  <label class="block text-arica-blue font-bold text-lg mb-2">Imagen del evento</label>
                  <div
                    (click)="fileInput.click()"
                    class="group relative bg-gray-100 border-2 border-dashed border-gray-300 rounded-xl h-64 flex flex-col items-center justify-center mb-4 overflow-hidden cursor-pointer hover:border-arica-blue hover:bg-arica-blue/5 transition-all"
                  >
                    <img
                      *ngIf="evento.imagen"
                      [src]="getImagenUrl(evento.imagen)"
                      alt="Evento"
                      class="absolute inset-0 w-full h-full object-cover group-hover:opacity-60 transition-opacity"
                    >

                    <div class="flex flex-col items-center gap-3 z-10 p-6 text-center">
                      <div class="bg-white p-4 rounded-full shadow-md group-hover:scale-110 transition-transform">
                        <svg class="w-8 h-8 text-gray-400 group-hover:text-arica-blue" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
                        </svg>
                      </div>
                      <div class="space-y-1">
                        <p class="text-sm font-bold text-gray-700 group-hover:text-arica-blue">
                          {{ evento.imagen ? 'Cambiar imagen' : 'Subir imagen' }}
                        </p>
                        <p class="text-xs text-gray-500">Haz clic para seleccionar un archivo</p>
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
                </div>


                <div>
                  <label class="block text-arica-blue font-bold text-lg mb-2 text-center">
                    Capacidad Maxima
                  </label>
                  <input
                    type="text"
                    [(ngModel)]="evento.capacidad"
                    class="w-24 mx-auto block px-4 py-3 bg-cyan-400 text-white rounded-lg outline-none focus:ring-2 focus:ring-arica-blue text-center text-xl font-bold"
                  >
                </div>
              </div>


              <div class="pt-4">
                <button
                  (click)="volver()"
                  class="w-full bg-arica-orange hover:bg-orange-600 text-white font-bold py-3 px-8 rounded-lg transition-colors"
                >
                  Salir
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class EditarEventoComponent implements OnInit {
  encargados: Usuario[] = [];
  categorias: CategoriaDTO[] = [];
  eventoId: number | null = null;
  cargando = true;

  evento = {
    titulo: '',
    descripcion: '',
    recinto: '',
    encargado: '',
    encargadoId: null as number | null,
    categoriaId: null as number | null,
    categoria: null as CategoriaDTO | null,
    fecha: '',
    fechaFin: '',
    horaInicio: '',
    horaFin: '',
    capacidad: '',
    imagen: ''
  };

  selectedFile: File | null = null;

  editando = {
    titulo: false,
    descripcion: false,
    recinto: false,
    encargado: false,
    categoria: false
  };

  toggleEdit(campo: keyof typeof this.editando): void {
    this.editando[campo] = !this.editando[campo];
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {

      if (file.size > 5 * 1024 * 1024) {
        alert('La imagen no puede pesar más de 5MB');
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


  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private eventosService: EventosService,
    private encargadosService: EncargadosService,
    private categoriasService: CategoriasService,
    private storageService: StorageService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.eventoId = parseInt(id, 10);
      this.cargarEvento();
    }
    this.cargarEncargados();
    this.cargarCategorias();
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
          imagen: eventoDTO.imagen || eventoDTO.imagenUrl || ''
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
        console.log('Categorías disponibles cargadas:', categorias);
      },
      error: (error) => {
        console.error('Error cargando categorías:', error);
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
      alert('La descripción no puede exceder los 255 caracteres');
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
      imagenUrl: this.evento.imagen || ''
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

  modificarEvento() {
  }

  getImagenUrl(imagen: string | undefined): string {
    if (!imagen) return '';
    if (imagen.startsWith('http') || imagen.startsWith('data:')) {
      return imagen;
    }

    return `${environment.apiUrl}/uploads/${imagen}`;
  }
}