import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Curso } from '../../models/curso';
import { CursoService } from '../../services/curso.service';
import { RecintosService } from '../../services/recintos.service';
import { EncargadosService } from '../../services/encargados.service';

import { CategoriaDTO } from '../../models/api.models';
import { CategoriasService } from '../../services/categorias.service';
import { WebsocketService } from '../../services/websocket.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-cursos',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './cursos.component.html'
})
export class CursosComponent implements OnInit, OnDestroy {
    cursos: Curso[] = [];
    recintos: any[] = [];
    encargados: any[] = [];
    categorias: CategoriaDTO[] = [];
    private wsSubscription?: Subscription;

    cursoSeleccionado: Curso | null = null;
    modoEdicion = false;
    mostrarModal = false;

    // Tab state
    tabActiva: string = 'TODOS';
    tabs = [
        { label: 'Todos', value: 'TODOS', color: 'bg-gray-600' },
        { label: 'Borradores', value: 'BORRADOR', color: 'bg-gray-500' },
        { label: 'Publicados', value: 'PUBLICADO', color: 'bg-green-600' },
        { label: 'En Progreso', value: 'EN_PROGRESO', color: 'bg-blue-600' },
        { label: 'Finalizados', value: 'FINALIZADO', color: 'bg-red-600' },
        { label: 'Cancelados', value: 'CANCELADO', color: 'bg-orange-600' }
    ];

    // Search and filters
    busqueda: string = '';
    categoriaFiltro: number | null = null;
    fechaInicioFiltro: string = '';
    fechaFinFiltro: string = '';
    sesionesMin: number | null = null;
    sesionesMax: number | null = null;

    diasOptions = [
        { label: 'Lunes', value: 'MONDAY' },
        { label: 'Martes', value: 'TUESDAY' },
        { label: 'Miércoles', value: 'WEDNESDAY' },
        { label: 'Jueves', value: 'THURSDAY' },
        { label: 'Viernes', value: 'FRIDAY' },
        { label: 'Sábado', value: 'SATURDAY' },
        { label: 'Domingo', value: 'SUNDAY' }
    ];

    estadosOptions = [
        { label: 'Borrador', value: 'BORRADOR' },
        { label: 'Publicado', value: 'PUBLICADO' },
        { label: 'En Progreso', value: 'EN_PROGRESO' },
        { label: 'Finalizado', value: 'FINALIZADO' },
        { label: 'Cancelado', value: 'CANCELADO' }
    ];

    nuevoCurso: Curso = {
        nombre: '',
        descripcion: '',
        estado: 'BORRADOR',
        horarios: []
    };

    constructor(
        private cursoService: CursoService,
        private recintoService: RecintosService,
        private encargadosService: EncargadosService,
        private categoriasService: CategoriasService,
        private websocketService: WebsocketService
    ) { }

    ngOnInit(): void {
        this.cargarCursos();
        this.cargarAuxiliares();
        this.cargarCategorias();

        this.wsSubscription = this.websocketService.getCuposUpdates().subscribe(update => {
            if (this.mostrarModal && this.nuevoCurso.sesiones) {
                const sesion = this.nuevoCurso.sesiones.find((s: any) => s.idEvento === update.eventoId || s.id === update.eventoId);
                if (sesion) {
                    sesion.inscritosActuales = update.inscritosActuales;
                }
            }
        });
    }

    ngOnDestroy(): void {
        if (this.wsSubscription) {
            this.wsSubscription.unsubscribe();
        }
    }

    cargarCursos(): void {
        this.cursoService.listarCursos().subscribe({
            next: (data) => {
                this.cursos = data;
            },
            error: (err) => console.error('Error al cargar cursos', err)
        });
    }

    cargarAuxiliares(): void {
        this.recintoService.getRecintos().subscribe((data: any) => this.recintos = data);
        this.encargadosService.getEncargados().subscribe((data: any) => this.encargados = data);
    }

    cargarCategorias(): void {
        this.categoriasService.getCategorias().subscribe({
            next: (data) => this.categorias = data,
            error: (err) => console.error('Error al cargar categorías', err)
        });
    }

    abrirModalCrear(): void {
        this.modoEdicion = false;
        this.resetForm();
        this.nuevoCurso.horarios = [];
        this.mostrarModal = true;
    }

    abrirModalEditar(curso: Curso): void {
        this.modoEdicion = true;
        this.nuevoCurso = { ...curso };

        if (!this.nuevoCurso.horarios) {
            this.nuevoCurso.horarios = [];
        }

        if (this.nuevoCurso.recinto && this.recintos.length > 0) {
            const foundRecinto = this.recintos.find(r => r.idRecinto === this.nuevoCurso.recinto?.idRecinto);
            if (foundRecinto) this.nuevoCurso.recinto = foundRecinto;
        }

        if (this.nuevoCurso.encargado && this.encargados.length > 0) {
            let encargadoEncontrado = this.encargados.find(e => e.idUsuario === this.nuevoCurso.encargado?.id);

            if (!encargadoEncontrado) {
                const encargadoId = this.nuevoCurso.encargado.id || this.nuevoCurso.encargado.idUsuario;
                encargadoEncontrado = this.encargados.find(e => (e.id || e.idUsuario) === encargadoId);
            }

            if (encargadoEncontrado) this.nuevoCurso.encargado = encargadoEncontrado;
        }

        if (this.nuevoCurso.categoria && this.categorias.length > 0) {
            const foundCat = this.categorias.find(c => c.id === this.nuevoCurso.categoria?.id);
            if (foundCat) this.nuevoCurso.categoria = foundCat;
        }

        this.mostrarModal = true;

        if (this.nuevoCurso.sesiones) {
            this.nuevoCurso.sesiones.forEach((s: any) => {
                const id = s.idEvento || s.id;
                if (id) this.websocketService.subscribeToEvent(id);
            });
        }
    }

    compararPorId(o1: any, o2: any): boolean {
        if (!o1 || !o2) return false;

        if (o1.idRecinto && o2.idRecinto) return o1.idRecinto === o2.idRecinto;
        if (o1.id && o2.id && !o1.idRecinto && !o1.idUsuario) return o1.id === o2.id;

        const id1 = o1.id || o1.idUsuario;
        const id2 = o2.id || o2.idUsuario;
        if (id1 && id2) return id1 === id2;

        return o1 === o2;
    }

    cerrarModal(): void {
        if (this.nuevoCurso.sesiones) {
            this.nuevoCurso.sesiones.forEach((s: any) => {
                const id = s.idEvento || s.id;
                if (id) this.websocketService.unsubscribeFromEvent(id);
            });
        }
        this.mostrarModal = false;
        this.resetForm();
    }

    resetForm(): void {
        this.nuevoCurso = { nombre: '', descripcion: '', estado: 'BORRADOR', horarios: [] };
    }

    agregarHorario(): void {
        if (!this.nuevoCurso.horarios) {
            this.nuevoCurso.horarios = [];
        }
        this.nuevoCurso.horarios.push({ dia: 'MONDAY', horaInicio: '09:00:00', horaFin: '10:00:00' });
    }

    eliminarHorario(index: number): void {
        this.nuevoCurso.horarios?.splice(index, 1);
    }

    getDiaLabel(diaValue: string): string {
        const d = this.diasOptions.find(o => o.value === diaValue);
        return d ? d.label : diaValue;
    }

    getEstadoLabel(estadoValue?: string): string {
        if (!estadoValue) return 'Sin Estado';
        const e = this.estadosOptions.find(o => o.value === estadoValue);
        return e ? e.label : estadoValue;
    }

    getEstadoClass(estadoValue?: string): string {
        switch (estadoValue) {
            case 'PUBLICADO': return 'bg-green-100 text-green-800 border-green-200';
            case 'EN_PROGRESO': return 'bg-blue-100 text-blue-800 border-blue-200';
            case 'FINALIZADO': return 'bg-gray-100 text-gray-800 border-gray-200';
            case 'CANCELADO': return 'bg-red-100 text-red-800 border-red-200';
            default: return 'bg-yellow-100 text-yellow-800 border-yellow-200'; // BORRADOR
        }
    }

    formatFecha(fecha: string): string {
        return new Date(fecha).toLocaleDateString('es-CL');
    }

    formatHora(hora: string): string {
        if (!hora) return '';
        if (hora.length === 5) return hora;
        return hora.substring(0, 5);
    }

    guardarCurso(): void {
        if (this.nuevoCurso.horarios) {
            this.nuevoCurso.horarios.forEach(h => {
                if (h.horaInicio.length === 5) h.horaInicio += ':00';
                if (h.horaFin.length === 5) h.horaFin += ':00';
            });
        }

        // Transform data to match backend DTO expectations
        const cursoData: any = {
            nombre: this.nuevoCurso.nombre,
            descripcion: this.nuevoCurso.descripcion,
            fechaInicio: this.nuevoCurso.fechaInicio,
            fechaFin: this.nuevoCurso.fechaFin,
            horaInicio: this.nuevoCurso.horaInicio,
            horaFin: this.nuevoCurso.horaFin,
            dias: this.nuevoCurso.dias,
            cupo: this.nuevoCurso.cupo,
            maximoPorInscripcion: this.nuevoCurso.maximoPorInscripcion,
            estado: this.nuevoCurso.estado || 'BORRADOR',
            horarios: this.nuevoCurso.horarios,
            // Extract IDs from objects
            idRecinto: this.nuevoCurso.recinto?.idRecinto || null,
            idEncargado: this.nuevoCurso.encargado?.idUsuario || this.nuevoCurso.encargado?.id || null,
            idCategoria: this.nuevoCurso.categoria?.id || null
        };

        if (this.modoEdicion && this.nuevoCurso.idCurso) {
            this.cursoService.editarCurso(this.nuevoCurso.idCurso, cursoData).subscribe({
                next: () => {
                    this.cargarCursos();
                    this.cerrarModal();
                },
                error: (err) => console.error('Error al editar curso', err)
            });
        } else {
            this.cursoService.crearCurso(cursoData).subscribe({
                next: () => {
                    this.cargarCursos();
                    this.cerrarModal();
                },
                error: (err) => console.error('Error al crear curso', err)
            });
        }
    }

    publicarCurso(id: number): void {
        if (confirm('¿Estás seguro de publicar este curso? Una vez publicado, estará visible para inscripciones.')) {
            this.cursoService.publicarCurso(id).subscribe({
                next: () => this.cargarCursos(),
                error: (err) => {
                    console.error('Error al publicar curso', err);
                    alert('No se pudo publicar el curso. Verifica que tenga fechas de inicio y fin.');
                }
            });
        }
    }

    cancelarCurso(id: number): void {
        if (confirm('¿Estás seguro de cancelar este curso? Esta acción no se puede deshacer.')) {
            this.cursoService.cancelarCurso(id).subscribe({
                next: () => this.cargarCursos(),
                error: (err) => {
                    console.error('Error al cancelar curso', err);
                    alert('No se pudo cancelar el curso.');
                }
            });
        }
    }

    eliminarCurso(id: number): void {
        if (confirm('¿Estás seguro de eliminar este curso? Esto no eliminará los eventos ya creados automáticamente.')) {
            this.cursoService.eliminarCurso(id).subscribe({
                next: () => this.cargarCursos(),
                error: (err) => console.error('Error al eliminar curso', err)
            });
        }
    }

    // Pagination Logic
    paginaActual = 1;
    cursosPorPagina = 5;

    // Filtered courses based on active tab and filters
    get cursosFiltrados(): Curso[] {
        let resultado = this.cursos;

        // 1. Filter by tab (estado)
        if (this.tabActiva !== 'TODOS') {
            resultado = resultado.filter(c => c.estado === this.tabActiva);
        }

        // 2. Filter by search text
        if (this.busqueda.trim()) {
            const busq = this.busqueda.toLowerCase();
            resultado = resultado.filter(c =>
                c.nombre?.toLowerCase().includes(busq) ||
                c.descripcion?.toLowerCase().includes(busq)
            );
        }

        // 3. Filter by category
        if (this.categoriaFiltro) {
            resultado = resultado.filter(c =>
                c.categoria?.id === this.categoriaFiltro
            );
        }

        // 4. Filter by date range
        if (this.fechaInicioFiltro) {
            resultado = resultado.filter(c =>
                c.fechaInicio && c.fechaInicio >= this.fechaInicioFiltro
            );
        }
        if (this.fechaFinFiltro) {
            resultado = resultado.filter(c =>
                c.fechaInicio && c.fechaInicio <= this.fechaFinFiltro
            );
        }

        // 5. Filter by session count
        if (this.sesionesMin !== null) {
            resultado = resultado.filter(c =>
                (c.cantidadSesiones || 0) >= this.sesionesMin!
            );
        }
        if (this.sesionesMax !== null) {
            resultado = resultado.filter(c =>
                (c.cantidadSesiones || 0) <= this.sesionesMax!
            );
        }

        return resultado;
    }

    get cursosPaginados(): Curso[] {
        const inicio = (this.paginaActual - 1) * this.cursosPorPagina;
        const fin = inicio + this.cursosPorPagina;
        return this.cursosFiltrados.slice(inicio, fin);
    }

    get totalPaginas(): number {
        return Math.ceil(this.cursosFiltrados.length / this.cursosPorPagina);
    }

    // Tab methods
    cambiarTab(tab: string): void {
        this.tabActiva = tab;
        this.paginaActual = 1; // Reset pagination when changing tabs
    }

    getCursosCount(estado: string): number {
        if (estado === 'TODOS') {
            return this.cursos.length;
        }
        return this.cursos.filter(c => c.estado === estado).length;
    }

    // Filter methods
    limpiarFiltros(): void {
        this.busqueda = '';
        this.categoriaFiltro = null;
        this.fechaInicioFiltro = '';
        this.fechaFinFiltro = '';
        this.sesionesMin = null;
        this.sesionesMax = null;
        this.paginaActual = 1;
    }

    tieneFiltrosActivos(): boolean {
        return this.busqueda.trim() !== '' ||
            this.categoriaFiltro !== null ||
            this.fechaInicioFiltro !== '' ||
            this.fechaFinFiltro !== '' ||
            this.sesionesMin !== null ||
            this.sesionesMax !== null;
    }

    contarFiltrosActivos(): number {
        let count = 0;
        if (this.busqueda.trim()) count++;
        if (this.categoriaFiltro) count++;
        if (this.fechaInicioFiltro) count++;
        if (this.fechaFinFiltro) count++;
        if (this.sesionesMin !== null) count++;
        if (this.sesionesMax !== null) count++;
        return count;
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

    irAPagina(pagina: any): void {
        if (typeof pagina === 'number' && pagina >= 1 && pagina <= this.totalPaginas) {
            this.paginaActual = pagina;
        }
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
}
