import { Component, input, Input, InputSignal, OnInit, output, OutputEmitterRef } from '@angular/core';
import { EstadisticaCategoriaDTO, EstadisticaDTO } from '../../../models/api.models';
import { CommonModule } from '@angular/common';
import { InformacionModal } from '../estadistica.component';

interface CategoriaStats {
  nombre: string;
  cantidad: number;
  porcentaje: number;
  color: string;
}



@Component({
  selector: 'app-categorias-populares-chart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './categorias-populares-chart.component.html',
  styleUrl: './categorias-populares-chart.component.css'
})


export class CategoriasPopularesChartComponent implements OnInit {

  estadisticasHeredadas : InputSignal<EstadisticaDTO> = input.required<EstadisticaDTO>();

  estadisticasCategorias! : CategoriaStats[];

  estadisticasGenerales! : EstadisticaDTO;

  mostrarModalInfo : OutputEmitterRef<InformacionModal> = output();


  ngOnInit(): void {
    this.estadisticasGenerales = this.estadisticasHeredadas()
    this.procesarEstadisticas();
  }

  abrirModalInfo(title : string, description : string){
    this.mostrarModalInfo.emit({
      titulo: title,
      descripcion: description
    });
  }

  procesarEstadisticas(){

    const total = this.estadisticasGenerales.resumen.totalEventos;  
    
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

    this.estadisticasCategorias = this.estadisticasGenerales.categorias.map((cat, index) => ({
      nombre: cat.nombre,
      cantidad: cat.cantidad,
      porcentaje: total > 0 ? Math.round((cat.cantidad / total) * 100) : 0,
      color: colors[index % colors.length]
    }));
  }


  getPieChartGradient(): string {
    if (!this.estadisticasCategorias || this.estadisticasCategorias.length === 0) {
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

    this.estadisticasCategorias.forEach(cat => {
      const degrees = (cat.porcentaje / 100) * 360;
      const color = colorMap[cat.color] || '#6B7280';
      const startAngle = currentAngle;
      const endAngle = currentAngle + degrees;

      gradientParts.push(`${color} ${startAngle}deg ${endAngle}deg`);
      currentAngle = endAngle;
    });

    return `conic-gradient(${gradientParts.join(', ')})`;
  }
  
}
