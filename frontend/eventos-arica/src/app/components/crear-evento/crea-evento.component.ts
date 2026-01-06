import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EventosService } from '../../services/eventos.service';
import { RecintosService } from '../../services/recintos.service';
import { StorageService } from '../../services/storage.service';
import { EncargadosService } from '../../services/encargados.service';
import { CategoriasService } from '../../services/categorias.service';
import { CrearEventoDTO, Usuario, CategoriaDTO } from '../../models/api.models';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-crear-evento',
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
            Crear evento
          </h1>


          <div *ngIf="mensaje"
               [class]="mensaje.tipo === 'error' ? 'bg-red-50 border-l-4 border-red-500' : 'bg-green-50 border-l-4 border-green-500'"
               class="p-4 mb-6 rounded">
            <p [class]="mensaje.tipo === 'error' ? 'text-red-700' : 'text-green-700'"
               class="font-medium">{{mensaje.texto}}</p>
          </div>

          <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">

            <div class="lg:col-span-2 space-y-6">

              <div>
                <label class="block text-arica-blue font-bold text-lg mb-2">Titulo</label>
                <input
                  type="text"
                  [(ngModel)]="evento.titulo"
                  class="w-full px-4 py-3 bg-arica-cyan rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                  placeholder="Ingrese el título del evento"
                >
              </div>


              <div>
                <label class="block text-arica-blue font-bold text-lg mb-2">Descripcion</label>
                <textarea
                  [(ngModel)]="evento.descripcion"
                  rows="4"
                  class="w-full px-4 py-3 bg-arica-cyan rounded-lg outline-none focus:ring-2 focus:ring-arica-blue resize-none"
                  placeholder="Ingrese la descripción"
                ></textarea>
              </div>


              <div>
                <label class="block text-arica-blue font-bold text-lg mb-2">Recinto</label>
                <select
                  [(ngModel)]="evento.recintoId"
                  class="w-full px-4 py-3 bg-arica-cyan rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                >
                  <option [value]="null" disabled>-- Seleccione recinto</option>
                  <option *ngFor="let recinto of recintos" [value]="recinto.idRecinto">
                    {{recinto.nombre}} - {{recinto.ubicacion}}
                  </option>
                </select>
              </div>


              <div>
                <label class="block text-arica-blue font-bold text-lg mb-2">Encargado evento</label>
                <select
                  [(ngModel)]="evento.encargadoId"
                  class="w-full px-4 py-3 bg-arica-cyan rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                >
                  <option [value]="null" disabled>-- Seleccione encargado</option>
                  <option *ngFor="let encargado of encargados" [value]="encargado.idUsuario">
                    {{encargado.nombre}} {{encargado.apellido}}
                  </option>
                </select>
              </div>


              <div>
                <label class="block text-arica-blue font-bold text-lg mb-2">Categoría</label>
                <select
                  [(ngModel)]="evento.categoriaId"
                  class="w-full px-4 py-3 bg-arica-cyan rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                >
                  <option [value]="null" disabled>-- Seleccione categoría</option>
                  <option *ngFor="let categoria of categorias" [value]="categoria.id">
                    {{categoria.nombre}}
                  </option>
                </select>
              </div>


              <div>
                <label class="block text-arica-blue font-bold text-lg mb-2">Público Objetivo</label>
                <input
                  type="text"
                  [(ngModel)]="evento.publicoObjetivo"
                  placeholder="Ej: Adultos, Niños, Todos"
                  class="w-full px-4 py-3 bg-arica-cyan rounded-lg outline-none focus:ring-2 focus:ring-arica-blue"
                >
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
                  (click)="guardarEvento()"
                  [disabled]="guardando"
                  [class.opacity-50]="guardando"
                  [class.cursor-not-allowed]="guardando"
                  class="w-full bg-arica-blue hover:bg-arica-blue-dark text-white font-bold py-3 px-8 rounded-lg transition-colors"
                >
                  {{ guardando ? 'Guardando...' : 'Guardar' }}
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
                    type="number"
                    [(ngModel)]="evento.capacidad"
                    placeholder="100"
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
    imagen: '' as string | undefined
  };

  selectedFile: File | null = null;

  recintos: any[] = [];
  encargados: Usuario[] = [];
  categorias: CategoriaDTO[] = [];
  guardando = false;
  mensaje: { tipo: 'error' | 'success', texto: string } | null = null;

  constructor(
    private router: Router,
    private eventosService: EventosService,
    private recintosService: RecintosService,
    private encargadosService: EncargadosService,
    private categoriasService: CategoriasService,
    private storageService: StorageService
  ) { }

  ngOnInit(): void {
    this.cargarRecintos();
    this.cargarEncargados();
    this.cargarCategorias();
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
      imagenUrl: this.evento.imagen || ''
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