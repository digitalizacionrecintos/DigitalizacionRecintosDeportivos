import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EstadisticasService } from '../../services/estadisticas.service';
import { EstadisticaDTO, EstadisticaCategoriaDTO, EstadisticaRecintoDTO } from '../../models/api.models';

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

@Component({
  selector: 'app-estadisticas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="ml-64 min-h-screen bg-gradient-to-br from-arica-blue via-blue-700 to-cyan-500 p-8">
      <div class="max-w-7xl mx-auto">


        <div class="bg-white rounded-lg shadow-2xl overflow-hidden mb-8">
          <div class="h-2 overflow-hidden">
            <img src="images/cuatro_colores.png" alt="" class="w-full h-full object-cover">
          </div>
          <div class="p-8">
            <h1 class="text-4xl font-bold text-arica-blue mb-2">Estadísticas de Eventos</h1>
            <p class="text-gray-600">Análisis detallado de la actividad de eventos municipales</p>
          </div>
        </div>


        <div class="bg-white rounded-lg shadow-lg p-4 mb-8">
          <div class="flex items-center justify-end gap-4">
            <label class="text-arica-blue font-bold">Año:</label>
            <select
              [(ngModel)]="anioSeleccionado"
              (change)="actualizarDatos()"
              class="px-4 py-2 bg-arica-blue text-white rounded-lg outline-none focus:ring-2 focus:ring-blue-400 font-semibold cursor-pointer shadow-md hover:bg-blue-800 transition-colors"
            >
              <option [ngValue]="null">Todos</option>
              <option *ngFor="let anio of aniosDisponibles" [ngValue]="anio">{{ anio }}</option>
            </select>
          </div>
        </div>

        <div *ngIf="loading" class="text-center text-white text-xl py-10">
            Cargando estadísticas...
        </div>

        <div *ngIf="!loading && statsData" class="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">


          <div class="bg-white rounded-xl shadow-sm p-4">
            <div class="flex items-center gap-2 mb-3">
              <svg class="w-4 h-4" style="color: #051C9C;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
              </svg>
              <h2 class="text-sm font-bold" style="color: #051C9C;">Categorías Populares</h2>
              <button
                (click)="abrirModalInfo('Categorías Populares', 'Muestra las categorías de eventos más frecuentes y su porcentaje respecto al total de eventos realizados.')"
                class="ml-auto text-gray-400 hover:text-arica-blue transition-colors focus:outline-none"
                title="Más información"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
              </button>
            </div>


            <div class="mb-6 p-6 bg-gradient-to-br from-arica-blue to-blue-600 rounded-lg text-center">
              <p class="text-white/80 text-sm mb-2">Total de eventos registrados</p>
              <p class="text-5xl font-bold text-white">{{ statsData.resumen.totalEventos }}</p>
            </div>


            <div class="flex flex-col lg:flex-row gap-6 items-center">

              <div class="relative">
                <div
                  class="w-48 h-48 rounded-full shadow-lg"
                  [style.background]="getPieChartGradient()"
                ></div>

                <div class="absolute inset-0 flex items-center justify-center">
                  <div class="w-20 h-20 bg-white rounded-full shadow-inner"></div>
                </div>
              </div>


              <div class="flex-1 space-y-3">
                <div *ngFor="let cat of categoriasStats" class="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-50 transition-colors">
                  <div [class]="cat.color + ' w-4 h-4 rounded-full flex-shrink-0'"></div>
                  <div class="flex-1">
                    <div class="flex justify-between items-center">
                      <span class="font-semibold text-gray-700">{{ cat.nombre }}</span>
                      <span class="text-sm font-bold" [class]="cat.color.replace('bg-', 'text-')">{{ cat.porcentaje }}%</span>
                    </div>
                    <p class="text-xs text-gray-500">{{ cat.cantidad }} eventos</p>
                  </div>
                </div>
              </div>
            </div>
          </div>


          <div class="bg-white rounded-xl shadow-sm p-4">
            <div class="flex items-center gap-2 mb-3">
              <svg class="w-4 h-4" style="color: #051C9C;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 12l3-3 3 3 4-4M8 21l4-4 4 4M3 4h18M4 4h16v12a1 1 0 01-1 1H5a1 1 0 01-1-1V4z"/>
              </svg>
              <h2 class="text-sm font-bold" style="color: #051C9C;">Distribución Mensual</h2>
              <button
                (click)="abrirModalInfo('Distribución Mensual', 'Visualiza la cantidad total de eventos realizados en cada mes del año seleccionado. Permite identificar los meses con mayor actividad.')"
                class="ml-auto text-gray-400 hover:text-arica-blue transition-colors focus:outline-none"
                title="Más información"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
              </button>
            </div>


            <div class="relative h-48 mb-6 bg-gray-50 rounded-lg p-4">
              <svg class="w-full h-full" viewBox="0 0 360 120" preserveAspectRatio="none">

                <line x1="0" y1="30" x2="360" y2="30" stroke="#e5e7eb" stroke-width="1"/>
                <line x1="0" y1="60" x2="360" y2="60" stroke="#e5e7eb" stroke-width="1"/>
                <line x1="0" y1="90" x2="360" y2="90" stroke="#e5e7eb" stroke-width="1"/>


                <polyline
                  [attr.points]="getLineChartPoints()"
                  fill="none"
                  stroke="#00AAC8"
                  stroke-width="3"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />


                <polygon
                  [attr.points]="getLineChartAreaPoints()"
                  fill="url(#areaGradient)"
                  opacity="0.3"
                />


                <defs>
                  <linearGradient id="areaGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                    <stop offset="0%" style="stop-color:#00AAC8;stop-opacity:0.5" />
                    <stop offset="100%" style="stop-color:#00AAC8;stop-opacity:0" />
                  </linearGradient>
                </defs>


                <g *ngFor="let mes of mesesStats; let i = index">
                  <circle
                    [attr.cx]="getPointX(i)"
                    [attr.cy]="getPointY(mes.cantidad)"
                    r="4"
                    fill="#0045AB"
                    stroke="white"
                    stroke-width="2"
                  />
                </g>
              </svg>


              <div class="flex justify-between mt-2 text-xs text-gray-500">
                <span *ngFor="let mes of mesesStats">{{ mes.mes.substring(0, 3) }}</span>
              </div>
            </div>


            <div class="space-y-2 max-h-64 overflow-y-auto pr-2">
              <div *ngFor="let mes of mesesStats; let i = index" class="flex items-center justify-between p-2 rounded-lg hover:bg-gray-50 transition-colors">
                <div class="flex items-center gap-3">
                  <span class="font-semibold text-gray-700 w-24">{{ mes.mes }}</span>
                  <span class="text-lg font-bold text-arica-blue">{{ mes.cantidad }}</span>
                </div>
                <div class="flex items-center gap-2">

                  <div *ngIf="i > 0" class="flex items-center gap-1">
                    <span
                      class="text-sm font-bold"
                      [class.text-green-500]="mes.cantidad > mesesStats[i-1].cantidad"
                      [class.text-red-500]="mes.cantidad < mesesStats[i-1].cantidad"
                      [class.text-gray-400]="mes.cantidad === mesesStats[i-1].cantidad"
                    >
                      {{ mes.cantidad > mesesStats[i-1].cantidad ? '+' : '' }}{{ mes.cantidad - mesesStats[i-1].cantidad }}
                    </span>
                    <svg *ngIf="mes.cantidad > mesesStats[i-1].cantidad" class="w-4 h-4 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"/>
                    </svg>
                    <svg *ngIf="mes.cantidad < mesesStats[i-1].cantidad" class="w-4 h-4 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                    </svg>
                    <svg *ngIf="mes.cantidad === mesesStats[i-1].cantidad" class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4"/>
                    </svg>
                  </div>
                  <span *ngIf="i === 0" class="text-xs text-gray-400">--</span>
                </div>
              </div>
            </div>


            <div class="mt-6 p-4 bg-gradient-to-r from-arica-cyan to-arica-blue rounded-lg">
              <div class="text-center">
                <p class="text-white/80 text-sm mb-1">Mes con más eventos</p>
                <p class="text-2xl font-bold text-white">{{ mesMasEventos.mes }}</p>
                <p class="text-white/90 text-lg">{{ mesMasEventos.cantidad }} eventos</p>
              </div>
            </div>
          </div>
        </div>


        <div *ngIf="!loading && statsData" class="bg-white rounded-lg shadow-2xl p-8 mb-8">

          <div class="flex items-center gap-4 mb-6">
            <label class="text-arica-blue font-bold">Granularidad:</label>
            <div class="flex gap-2">
              <button
                *ngFor="let g of granularidades"
                (click)="cambiarGranularidad(g.valor)"
                [class.bg-arica-blue]="granularidadSeleccionada === g.valor"
                [class.text-white]="granularidadSeleccionada === g.valor"
                [class.bg-gray-200]="granularidadSeleccionada !== g.valor"
                [class.text-gray-700]="granularidadSeleccionada !== g.valor"
                class="px-4 py-2 rounded-lg font-semibold transition-colors hover:shadow-md"
              >
                {{ g.label }}
              </button>
            </div>
          </div>


          <div class="bg-white rounded-xl shadow-sm p-4 mt-3">
            <div class="flex items-center justify-between mb-3">
              <div class="flex items-center gap-2">
                <svg class="w-4 h-4" style="color: #051C9C;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                </svg>
                <h2 class="text-sm font-bold" style="color: #051C9C;">Mapa de Intensidad {{ anioSeleccionado || 'Todos los años' }}</h2>
                <button
                  (click)="abrirModalInfo('Mapa de Intensidad', 'Gráfico de calor que muestra la concentración de eventos a lo largo de las semanas del año. Los cuadros más oscuros indican semanas con mayor cantidad de eventos.')"
                  class="ml-2 text-gray-400 hover:text-arica-blue transition-colors focus:outline-none"
                  title="Más información"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                  </svg>
                </button>
              </div>
              <div class="flex items-center gap-1 text-[10px]">
                <span class="text-gray-500">Menos</span>
                <div class="w-4 h-4 bg-gray-100 rounded"></div>
                <div class="w-4 h-4 bg-arica-cyan/30 rounded"></div>
                <div class="w-4 h-4 bg-arica-cyan/60 rounded"></div>
                <div class="w-4 h-4 bg-arica-cyan rounded"></div>
                <div class="w-4 h-4 bg-arica-blue rounded"></div>
                <span class="text-gray-500">Más</span>
              </div>
            </div>


          <div class="overflow-x-auto">
            <div class="inline-block min-w-full">

              <div class="grid grid-cols-12 gap-2">
                <div *ngFor="let mes of mesesHeatmap; let i = index" class="space-y-1">
                  <div class="text-[10px] font-semibold text-gray-600 text-center mb-1">
                    {{ mes.nombre }}
                  </div>
                  <div class="grid grid-cols-4 gap-0.5">
                    <div
                      *ngFor="let semana of mes.semanas"
                      [title]="semana.cantidad + ' eventos'"
                      [class]="getHeatmapColor(semana.nivel)"
                      class="w-full h-8 rounded cursor-pointer hover:ring-1 hover:ring-arica-blue transition-all flex items-center justify-center"
                    >
                      <span *ngIf="semana.cantidad > 0" class="text-xs font-bold text-white">
                        {{ semana.cantidad }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>


              <div class="mt-8 grid grid-cols-1 md:grid-cols-3 gap-4">
                <div class="bg-gradient-to-br from-arica-cyan to-cyan-400 rounded-lg p-6 text-white text-center">
                  <p class="text-sm opacity-90 mb-2">Total de eventos</p>
                  <p class="text-4xl font-bold">{{ statsData.resumen.totalEventos }}</p>
                </div>
                <div class="bg-gradient-to-br from-arica-yellow to-yellow-400 rounded-lg p-6 text-white text-center">
                  <p class="text-sm opacity-90 mb-2">Asistencia Promedio</p>
                  <p class="text-4xl font-bold">{{ statsData.resumen.porcentajeAsistencia }}%</p>
                </div>
                <div class="bg-gradient-to-br from-arica-coral to-red-400 rounded-lg p-6 text-white text-center">
                  <p class="text-sm opacity-90 mb-2">Promedio mensual</p>
                  <p class="text-4xl font-bold">{{ statsData.resumen.promedioEventosMensual }}</p>
                  <p class="text-xs opacity-90 mt-1">eventos</p>
                </div>


        <div *ngIf="mostrarModalInfo" class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div class="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden transform transition-all scale-100 opacity-100">

            <div class="bg-gradient-to-r from-arica-blue to-blue-600 p-6 flex justify-between items-center text-white">
              <h3 class="text-xl font-bold flex items-center gap-2">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                Información
              </h3>
              <button (click)="cerrarModalInfo()" class="hover:bg-white/20 rounded-full p-1 transition-colors">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                </svg>
              </button>
            </div>


            <div class="p-6">
              <h4 class="text-lg font-bold text-arica-blue mb-3 border-b pb-2">{{ infoTitulo }}</h4>
              <p class="text-gray-600 leading-relaxed text-base">
                {{ infoDescripcion }}
              </p>

              <div class="mt-6 flex justify-end">
                <button
                  (click)="cerrarModalInfo()"
                  class="px-5 py-2.5 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold rounded-lg transition-colors border border-gray-300"
                >
                  Entendido
                </button>
              </div>
            </div>
          </div>
        </div>
              </div>
            </div>
          </div>
        </div>


        <div *ngIf="!loading && statsData" class="bg-white rounded-xl shadow-sm p-4 mt-3">
          <div class="flex items-center gap-2 mb-3">
            <svg class="w-4 h-4" style="color: #051C9C;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 3.055A9.001 9.001 0 1020.945 13H11V3.055z"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.488 9H15V3.512A9.025 9.025 0 0120.488 9z"/>
            </svg>
            <h2 class="text-sm font-bold" style="color: #051C9C;">Por {{ granularidadSeleccionada }}</h2>
            <button
              (click)="abrirModalInfo('Desglose por ' + granularidadSeleccionada, 'Muestra el detalle numérico de la cantidad de eventos agrupados según la granularidad seleccionada (Mes, Año o Semana).')"
              class="ml-auto text-gray-400 hover:text-arica-blue transition-colors focus:outline-none"
              title="Más información"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </button>
          </div>

          <div class="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-6 lg:grid-cols-8 gap-2">
            <div
              *ngFor="let periodo of datosPorGranularidad"
              class="bg-gradient-to-br from-arica-cyan/20 to-arica-blue/20 rounded-lg p-3 hover:shadow-md transition-all cursor-pointer hover:scale-105"
            >
              <p class="text-[10px] text-gray-500 mb-1 truncate">{{ periodo.label }}</p>
              <p class="text-xl font-bold text-arica-blue">{{ periodo.cantidad }}</p>
              <p class="text-[10px] text-gray-600">eventos</p>
            </div>
          </div>
        </div>

      </div>
    </div>
  `,
  styles: []
})
export class EstadisticasComponent implements OnInit {

  anioSeleccionado: number | null = null;
  aniosDisponibles: number[] = [];
  granularidadSeleccionada = 'Mes';
  loading = false;
  statsData: EstadisticaDTO | null = null;

  granularidades = [
    { label: 'Año', valor: 'Año' },
    { label: 'Mes', valor: 'Mes' },
    { label: 'Semana', valor: 'Semana' }
  ];

  categoriasStats: CategoriaStats[] = [];
  mesesStats: MesStats[] = [];
  mesesHeatmap: any[] = [];
  datosPorGranularidad: any[] = [];


  mostrarModalInfo = false;
  infoTitulo = '';
  infoDescripcion = '';

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
        console.log(' Estadísticas recibidas:', data);
        console.log(' Categorías del backend:', data.categorias);
        this.statsData = data;
        this.procesarDatos(data);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar estadísticas', err);
        this.loading = false;
      }
    });
  }

  procesarDatos(data: EstadisticaDTO): void {

    const total = data.resumen.totalEventos;
    const colors = [
      'bg-arica-blue',
      'bg-arica-cyan',
      'bg-arica-yellow',
      'bg-arica-coral',
      'bg-purple-500',
      'bg-pink-500',
      'bg-indigo-500',
      'bg-teal-500',
      'bg-orange-500',
      'bg-green-500',
      'bg-red-500',
      'bg-blue-400'
    ];

    this.categoriasStats = data.categorias.map((cat, index) => ({
      nombre: cat.nombre,
      cantidad: cat.cantidad,
      porcentaje: total > 0 ? Math.round((cat.cantidad / total) * 100) : 0,
      color: colors[index % colors.length]
    }));

    console.log(' Categorías procesadas:', this.categoriasStats);
    console.log(' Pie gradient:', this.getPieChartGradient());


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

  getPieChartGradient(): string {
    if (!this.categoriasStats || this.categoriasStats.length === 0) {
      return 'conic-gradient(#e5e7eb 0deg 360deg)';
    }


    const colorMap: { [key: string]: string } = {
      'bg-arica-blue': '#0045AB',
      'bg-arica-cyan': '#00AAC8',
      'bg-arica-yellow': '#FFB81C',
      'bg-arica-coral': '#E33E2B',
      'bg-purple-500': '#8B5CF6',
      'bg-pink-500': '#EC4899',
      'bg-indigo-500': '#6366F1',
      'bg-teal-500': '#14B8A6',
      'bg-orange-500': '#F97316',
      'bg-green-500': '#22C55E',
      'bg-red-500': '#EF4444',
      'bg-blue-400': '#60A5FA'
    };

    let gradientParts: string[] = [];
    let currentAngle = 0;

    this.categoriasStats.forEach(cat => {
      const degrees = (cat.porcentaje / 100) * 360;
      const color = colorMap[cat.color] || '#6B7280';
      const startAngle = currentAngle;
      const endAngle = currentAngle + degrees;

      gradientParts.push(`${color} ${startAngle}deg ${endAngle}deg`);
      currentAngle = endAngle;
    });

    return `conic-gradient(${gradientParts.join(', ')})`;
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