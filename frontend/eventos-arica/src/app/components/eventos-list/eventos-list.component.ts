import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EventosService } from '../../services/eventos.service';
import { WebsocketService } from '../../services/websocket.service';
import { InscripcionesService } from '../../services/inscripciones.service';
import { CategoriasService } from '../../services/categorias.service';
import { RecintosService } from '../../services/recintos.service';
import { Subscription } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Evento } from '../../models/evento.model';
import { EstadoEvento } from '../../enums/estado-evento.enum';




@Component({
  selector: 'app-eventos-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './eventos-list.component.html',
  styleUrl: './eventos-list.component.css'
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

  
  categoriaFiltro: number | null = null;
  fechaInicioFiltro: string = '';
  fechaFinFiltro: string = '';
  inscritosMin: number | null = null;
  inscritosMax: number | null = null;
  mostrarFiltros: boolean = false;

  
  categorias: any[] = [];
  recintos: any[] = [];

  constructor(
    private router: Router,
    private eventosService: EventosService,
    private websocketService: WebsocketService,
    private inscripcionesService: InscripcionesService,
    private cdr: ChangeDetectorRef,
    private categoriasService: CategoriasService,
    private recintosService: RecintosService
  ) { }


  calcularEstadoEvento(fechaInicio?: string, horaInicio?: string, fechaFin?: string, horaFin?: string): EstadoEvento {
    if (!fechaInicio || !horaInicio || !fechaFin || !horaFin) {
      console.warn(' Fechas incompletas:', { fechaInicio, horaInicio, fechaFin, horaFin });
      return EstadoEvento.DISPONIBLE;
    }

    const ahora = new Date();


    const inicioStr = `${fechaInicio}T${horaInicio}`;
    const inicio = new Date(inicioStr);


    const finStr = `${fechaFin}T${horaFin}`;
    const fin = new Date(finStr);


    let estado: EstadoEvento;
    if (ahora < inicio) {
      estado = EstadoEvento.EN_ESPERA;
    } else if (ahora >= inicio && ahora <= fin) {
      estado = EstadoEvento.DISPONIBLE;
    } else {
      estado = EstadoEvento.TERMINADO;
    }

    console.log('  Estado calculado:', estado);
    return estado;
  }


  getEstadoColor(estado: string | EstadoEvento): string {
    switch (estado) {
      case EstadoEvento.EN_ESPERA:
        return 'bg-yellow-500';
      case EstadoEvento.DISPONIBLE:
        return 'bg-green-500';
      case EstadoEvento.TERMINADO:
        return 'bg-red-500';
      case EstadoEvento.TRANSCURRIENDO:
        return 'bg-blue-600';
      case 'Cancelado':
        return 'bg-gray-500';
      default:
        return 'bg-blue-500';
    }
  }


  getEstadoTexto(estado: string | EstadoEvento): string {
    switch (estado) {
      case EstadoEvento.EN_ESPERA:
        return 'En Espera';
      case EstadoEvento.DISPONIBLE:
        return 'Disponible';
      case EstadoEvento.TERMINADO:
        return 'Terminado';
      case EstadoEvento.TRANSCURRIENDO:
        return 'En Curso';
      case 'Cancelado':
        return 'Cancelado';
      default:
        return estado;
    }
  }


  getEstadoBorderColor(estado: string | EstadoEvento): string {
    switch (estado) {
      case EstadoEvento.EN_ESPERA:
        return '#eab308';
      case EstadoEvento.DISPONIBLE:
        return '#22c55e';
      case EstadoEvento.TERMINADO:
        return '#ef4444';
      case EstadoEvento.TRANSCURRIENDO:
        return '#2563eb';
      default:
        return '#3b82f6';
    }
  }

  ngOnInit(): void {
    this.cargarEventos();
    this.cargarCategorias();
    this.cargarRecintos();
    this.suscribirWebSocket();
  }

  ngOnDestroy(): void {

    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }

    this.websocketService.unsubscribeAll();
  }

  private listenToWebSocketUpdates(): void {
    this.wsSubscription = this.websocketService.getCuposUpdates().subscribe({
      next: (update) => {
        console.log(' ActualizaciÃ³n de cupos recibida:', update);

        const evento = this.eventos.find(e => e.id === update.eventoId);
        if (evento) {
          evento.inscritosActuales = update.inscritosActuales;
          console.log(` Evento ${evento.titulo} actualizado: ${update.inscritosActuales} inscritos`);
        }

        if (this.eventoSeleccionado && this.eventoSeleccionado.id === update.eventoId) {
          this.eventoSeleccionado.inscritosActuales = update.inscritosActuales;
        }

        this.cdr.detectChanges();
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
        this.eventos = eventosDTO.map(dto => {
          const fixDate = new Date(dto.fechaInicio);


          let estado = dto.estado;
          const estadoUpper = estado ? estado.toUpperCase() : '';

          if (estadoUpper === 'EN_CURSO' || estadoUpper === 'IN_PROGRESS' || estadoUpper === 'TRANSCURRIENDO') {
            estado = EstadoEvento.TRANSCURRIENDO;
          } else if (estadoUpper === 'EN_ESPERA') {
            estado = EstadoEvento.EN_ESPERA;
          } else if (estadoUpper === 'DISPONIBLE') {
            estado = EstadoEvento.DISPONIBLE;
          } else if (estadoUpper === 'TERMINADO') {
            estado = EstadoEvento.TERMINADO;
          }

          return {
            id: dto.idEvento || dto.id || 0,
            titulo: dto.titulo,
            fecha: this.formatearFecha(dto.fechaInicio, dto.horaInicio),
            fechaInicio: dto.fechaInicio,
            fechaFin: dto.fechaFin,
            horaInicio: this.formatearHoras(dto.horaInicio),
            horaFin: this.formatearHoras(dto.horaFin),
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
            categoria: dto.categoria,
            cursoId: dto.cursoId || (dto as any).curso?.idCurso,
            cursoNombre: dto.cursoNombre || (dto as any).curso?.nombre
          };
        });

        this.suscribirseAEventosVisibles();

        this.listenToWebSocketUpdates();

        if (this.eventos.length > 0 && !this.eventoSeleccionado) {
          this.eventoSeleccionado = this.eventos[0];
        }

        this.cargando = false;
        this.cdr.detectChanges();
        console.log(' Carga completada, cargando =', this.cargando);
      },
      error: (error) => {
        console.error(' Error cargando eventos:', error);
        this.error = 'Error al cargar eventos. Verifica tu conexiÃ³n con el backend.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }


  private suscribirseAEventosVisibles(): void {
    this.websocketService.unsubscribeAll();

    this.eventosPaginados.forEach(evento => {
      this.websocketService.subscribeToEvent(evento.id);
    });

    console.log(` Suscrito a ${this.eventosPaginados.length} eventos visibles`);
  }

  private formatearFecha(fecha: string, hora: string): string {
    const fechaObj = new Date(fecha);
    const horaObj = new Date(hora);
    const dia = fechaObj.getDate().toString().padStart(2, '0');
    const mes = (fechaObj.getMonth() + 1).toString().padStart(2, '0');
    const aÃ±o = fechaObj.getFullYear().toString().slice(-2);
    const horas = horaObj.getHours().toString().padStart(2, '0');
    const minutos = horaObj.getMinutes().toString().padStart(2, '0');
    return `${dia} / ${mes} / ${aÃ±o}`;
  }


  private getColorPorEstado(estado: string | EstadoEvento): string {
    switch (estado) {
      case EstadoEvento.EN_ESPERA:
        return 'bg-yellow-400';
      case EstadoEvento.DISPONIBLE:
        return 'bg-green-400';
      case EstadoEvento.TERMINADO:
        return 'bg-red-400';
      case EstadoEvento.TRANSCURRIENDO:
        return 'bg-blue-400';
      case 'Cancelado':
        return 'bg-gray-400';
      default:
        return 'bg-blue-400';
    }
  }

  filtros = [
    { label: 'TODOS', valor: 'TODOS', color: 'bg-gray-500' },
    { label: 'EN ESPERA', valor: EstadoEvento.EN_ESPERA, color: 'bg-yellow-500' },
    { label: 'DISPONIBLE', valor: EstadoEvento.DISPONIBLE, color: 'bg-green-500' },
    { label: 'EN CURSO', valor: EstadoEvento.TRANSCURRIENDO, color: 'bg-blue-600' },
    { label: 'TERMINADO', valor: EstadoEvento.TERMINADO, color: 'bg-red-600' }
  ];

  get eventosFiltrados(): Evento[] {
    let resultado = this.eventos;

    
    if (this.filtroActivo !== 'TODOS') {
      resultado = resultado.filter(e => e.estado === this.filtroActivo);
    }

    
    if (this.searchTerm.trim()) {
      const busq = this.searchTerm.toLowerCase();
      resultado = resultado.filter(e =>
        e.titulo.toLowerCase().includes(busq) ||
        e.descripcion?.toLowerCase().includes(busq) ||
        e.ubicacion?.toLowerCase().includes(busq)
      );
    }

    
    if (this.categoriaFiltro) {
      resultado = resultado.filter(e =>
        e.categoria?.id === this.categoriaFiltro
      );
    }

    
    if (this.fechaInicioFiltro) {
      resultado = resultado.filter(e =>
        e.fechaInicio && e.fechaInicio >= this.fechaInicioFiltro
      );
    }
    if (this.fechaFinFiltro) {
      resultado = resultado.filter(e =>
        e.fechaInicio && e.fechaInicio <= this.fechaFinFiltro
      );
    }

    
    if (this.inscritosMin !== null) {
      resultado = resultado.filter(e =>
        (e.inscritosActuales || 0) >= this.inscritosMin!
      );
    }
    if (this.inscritosMax !== null) {
      resultado = resultado.filter(e =>
        (e.inscritosActuales || 0) <= this.inscritosMax!
      );
    }

    
    resultado = [...resultado].sort((a, b) => {
      const fechaA = new Date(a.fechaInicio || '').getTime();
      const fechaB = new Date(b.fechaInicio || '').getTime();
      return this.ordenFecha === 'asc' ? fechaA - fechaB : fechaB - fechaA;
    });

    return resultado;
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
      this.suscribirseAEventosVisibles();
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
      this.suscribirseAEventosVisibles();
    }
  }

  paginaSiguiente(): void {
    if (this.paginaActual < this.totalPaginas) {
      this.paginaActual++;
      this.suscribirseAEventosVisibles();
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

  irACursos(): void {
    this.router.navigate(['/cursos']);
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
          this.websocketService.unsubscribeFromEvent(eventoId);
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
            alert(' No se puede eliminar este evento.\n\nPosibles razones:\nâ€¢ El evento tiene inscripciones activas\nâ€¢ No tienes permisos suficientes\n\nPrimero cancela las inscripciones o contacta al administrador.');
          } else if (err.status === 404) {
            alert(' El evento no existe o ya fue eliminado.');

            this.cargarEventos();
          } else {
            alert('Error eliminando evento: ' + (err.message || 'Error desconocido'));
          }
          this.websocketService.unsubscribeFromEvent(eventoId);

          this.cdr.detectChanges();
        }
      });
    }
  }

  cancelarEliminarEvento(): void {
    this.mostrarModalEliminar = false;
    this.eventoAEliminar = null;
  }

  formatearHoras(hora: string): string {
    const fixHora = new Date(hora);
    return new Intl.DateTimeFormat('es-CL', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false,
      timeZone: 'America/Santiago'
    }).format(new Date(fixHora));
  };


  activarEvento(): void {
    if (!this.eventoSeleccionado) return;

    this.activandoEvento = true;
    const eventoId = this.eventoSeleccionado.id;

    console.log(' Activando evento:', eventoId);

    this.eventosService.cambiarEstado(eventoId, EstadoEvento.DISPONIBLE).subscribe({
      next: () => {
        console.log(' Evento activado exitosamente');


        if (this.eventoSeleccionado) {
          this.eventoSeleccionado.estado = EstadoEvento.DISPONIBLE;
          this.eventoSeleccionado.color = this.getColorPorEstado(EstadoEvento.DISPONIBLE);
        }


        const eventoEnLista = this.eventos.find(e => e.id === eventoId);
        if (eventoEnLista) {
          eventoEnLista.estado = EstadoEvento.DISPONIBLE;
          eventoEnLista.color = this.getColorPorEstado(EstadoEvento.DISPONIBLE);
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

  // Filter helper methods
  limpiarFiltrosAdicionales(): void {
    this.categoriaFiltro = null;
    this.fechaInicioFiltro = '';
    this.fechaFinFiltro = '';
    this.inscritosMin = null;
    this.inscritosMax = null;
    this.paginaActual = 1;
  }

  tieneFiltrosAdicionalesActivos(): boolean {
    return this.categoriaFiltro !== null ||
      this.fechaInicioFiltro !== '' ||
      this.fechaFinFiltro !== '' ||
      this.inscritosMin !== null ||
      this.inscritosMax !== null;
  }

  contarFiltrosAdicionalesActivos(): number {
    let count = 0;
    if (this.categoriaFiltro) count++;
    if (this.fechaInicioFiltro) count++;
    if (this.fechaFinFiltro) count++;
    if (this.inscritosMin !== null) count++;
    if (this.inscritosMax !== null) count++;
    return count;
  }

  // Data loading methods
  cargarCategorias(): void {
    this.categoriasService.getCategorias().subscribe({
      next: (categorias: any[]) => {
        this.categorias = categorias;
      },
      error: (error: any) => {
        console.error('Error cargando categorÃ­as:', error);
      }
    });
  }

  cargarRecintos(): void {
    this.recintosService.getRecintos().subscribe({
      next: (recintos: any[]) => {
        this.recintos = recintos;
      },
      error: (error: any) => {
        console.error('Error cargando recintos:', error);
      }
    });
  }

  suscribirWebSocket(): void {
    this.listenToWebSocketUpdates();
  }
}