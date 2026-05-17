import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EstadisticasService } from '../../services/estadisticas.service';
import { EstadisticaDTO, EstadisticaCategoriaDTO, EstadisticaRecintoDTO, EstadisticasCursosDTO } from '../../models/api.models';
import { CategoriasPopularesChartComponent } from "./categorias-populares-chart/categorias-populares-chart.component";

interface CategoriaStats {
  nombre: string;
  cantidad: number;
  porcentaje: number;
  color: string;
}

interface MesStats {
  mes: string;
  cantidad: number;
}

export interface InformacionModal {
  titulo: string;
  descripcion: string;
}

@Component({
  selector: 'app-estadisticas',
  standalone: true,
  imports: [CommonModule, FormsModule, CategoriasPopularesChartComponent],
  templateUrl: './estadistica.component.html',
  styleUrl: './estadistica.component.css'
})
export class EstadisticasComponent implements OnInit {

  anioSeleccionado: number | null = null;
  aniosDisponibles: number[] = [];
  granularidadSeleccionada = 'Mes';
  loading = false;
  statsData: EstadisticaDTO | null = null;

  granularidades = [
    { label: 'Mes', valor: 'Mes' },
    { label: 'Año', valor: 'Año' },
    { label: 'Semana', valor: 'Semana' }
  ];

  categoriasStats: CategoriaStats[] = [];
  mesesStats: MesStats[] = [];
  mesesHeatmap: any[] = [];
  datosPorGranularidad: any[] = [];


  mostrarModalInfo : boolean = false;
  infoTitulo = '';
  infoDescripcion = '';

  loadingCursos = false;
  statsCursos: EstadisticasCursosDTO | null = null;

  constructor(private estadisticasService: EstadisticasService) { }

  ngOnInit(): void {
    this.cargarAniosDisponibles();
  }

  cargarAniosDisponibles(): void {
    this.loading = true;

    this.estadisticasService.getEstadisticasGenerales().subscribe({
      next: (data) => {
        if (data.distribucionTemporal && data.distribucionTemporal.porAnio) {
          const anios = Object.keys(data.distribucionTemporal.porAnio).map(Number);

          this.aniosDisponibles = anios.sort((a, b) => b - a);


          if (this.aniosDisponibles.length > 0) {
            this.anioSeleccionado = this.aniosDisponibles[0];
          } else {
            const currentYear = new Date().getFullYear();
            this.aniosDisponibles = [currentYear];
            this.anioSeleccionado = currentYear;
          }


          this.actualizarDatos();
        } else {

          console.warn('No se pudo obtener años disponibles, usando defaults');
          const currentYear = new Date().getFullYear();
          this.aniosDisponibles = [currentYear];
          this.anioSeleccionado = currentYear;
          this.actualizarDatos();
        }
      },
      error: (err) => {
        console.error('Error al cargar años disponibles', err);

        const currentYear = new Date().getFullYear();
        this.aniosDisponibles = [currentYear];
        this.anioSeleccionado = currentYear;
        this.actualizarDatos();
      }
    });
  }

  abrirModalInfo(titulo: string, descripcion: string): void {
    this.infoTitulo = titulo;
    this.infoDescripcion = descripcion;
    this.mostrarModalInfo = true;
  }

  cerrarModalInfo(): void {
    this.mostrarModalInfo = false;
  }

  actualizarDatos(): void {
    this.loading = true;

    const anioParam = this.anioSeleccionado ?? undefined;
    this.estadisticasService.getEstadisticasGenerales(anioParam).subscribe({
      next: (data) => {
        console.log('📊 Estadísticas recibidas:', data);
        console.log('📊 Categorías del backend:', data.categorias);
        this.statsData = data;
        this.procesarDatos(data);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar estadísticas', err);
        this.loading = false;
      }
    });

    this.cargarEstadisticasCursos();
  }

  cargarEstadisticasCursos(): void {
    this.loadingCursos = true;
    const anioParam = this.anioSeleccionado ?? undefined;

    this.estadisticasService.getEstadisticasCursos(anioParam).subscribe({
      next: (data) => {
        console.log('📚 Estadísticas de cursos recibidas:', data);
        this.statsCursos = data;
        this.loadingCursos = false;
      },
      error: (err) => {
        console.error('Error al cargar estadísticas de cursos', err);
        this.loadingCursos = false;
      }
    });
  }

  procesarDatos(data: EstadisticaDTO): void {

    const mesesNombres = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
    this.mesesStats = mesesNombres.map(mes => ({
      mes: mes,
      cantidad: data.distribucionTemporal.porMes[mes] || 0
    }));

    this.generarHeatmapData(data);
    this.generarDatosPorGranularidad(data);
  }

  get maxEventosMes(): number {
    return this.mesesStats.length > 0 ? Math.max(...this.mesesStats.map(m => m.cantidad)) : 0;
  }

  get mesMasEventos(): MesStats {
    if (this.mesesStats.length === 0) return { mes: '-', cantidad: 0 };
    return this.mesesStats.reduce((max, mes) =>
      mes.cantidad > max.cantidad ? mes : max
    );
  }

  generarHeatmapData(data: EstadisticaDTO): void {
    const meses = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];

    this.mesesHeatmap = meses.map((mes, mesIndex) => ({
      nombre: mes,
      semanas: Array.from({ length: 4 }, (_, semanaIndex) => {


        const mesNombreCompleto = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'][mesIndex];
        const totalMes = data.distribucionTemporal.porMes[mesNombreCompleto] || 0;
        const promedioSemanal = Math.ceil(totalMes / 4);


        let cantidad = 0;
        if (totalMes > 0) {

          cantidad = Math.max(0, promedioSemanal + Math.floor(Math.random() * 3) - 1);
        }

        return {
          cantidad,
          nivel: cantidad === 0 ? 0 : cantidad <= 1 ? 1 : cantidad <= 2 ? 2 : cantidad <= 4 ? 3 : 4
        };
      })
    }));
  }

  cambiarGranularidad(granularidad: string): void {
    this.granularidadSeleccionada = granularidad;
    if (this.statsData) {
      this.generarDatosPorGranularidad(this.statsData);
    }
  }

  generarDatosPorGranularidad(data: EstadisticaDTO): void {
    switch (this.granularidadSeleccionada) {
      case 'Mes':
        this.datosPorGranularidad = this.mesesStats.map(m => ({
          label: m.mes,
          cantidad: m.cantidad
        }));
        break;
      case 'Año':

        this.datosPorGranularidad = Object.keys(data.distribucionTemporal.porAnio).map(anio => ({
          label: anio,
          cantidad: data.distribucionTemporal.porAnio[anio]
        }));
        break;
      case 'Semana':

        this.datosPorGranularidad = Array.from({ length: 52 }, (_, i) => ({
          label: `Semana ${i + 1}`,
          cantidad: Math.floor(data.resumen.totalEventos / 52)
        }));
        break;
    }
  }

  getHeatmapColor(nivel: number): string {
    const colors = [
      'bg-gray-200',
      'bg-arica-cyan/30',
      'bg-arica-cyan/60',
      'bg-arica-cyan',
      'bg-arica-blue'
    ];
    return colors[nivel] || 'bg-gray-200';
  }

 

  getPointX(index: number): number {
    const padding = 15;
    const width = 360 - (padding * 2);
    const step = width / (this.mesesStats.length - 1 || 1);
    return padding + (index * step);
  }

  getPointY(cantidad: number): number {
    const padding = 10;
    const height = 120 - (padding * 2);
    const maxCantidad = this.maxEventosMes || 1;
    const y = height - ((cantidad / maxCantidad) * height) + padding;
    return y;
  }

  getLineChartPoints(): string {
    if (!this.mesesStats || this.mesesStats.length === 0) return '';

    return this.mesesStats.map((mes, i) => {
      const x = this.getPointX(i);
      const y = this.getPointY(mes.cantidad);
      return `${x},${y}`;
    }).join(' ');
  }

  getLineChartAreaPoints(): string {
    if (!this.mesesStats || this.mesesStats.length === 0) return '';

    const linePoints = this.mesesStats.map((mes, i) => {
      const x = this.getPointX(i);
      const y = this.getPointY(mes.cantidad);
      return `${x},${y}`;
    });


    const firstX = this.getPointX(0);
    const lastX = this.getPointX(this.mesesStats.length - 1);
    const bottomY = 110;

    return `${firstX},${bottomY} ${linePoints.join(' ')} ${lastX},${bottomY}`;
  }
}