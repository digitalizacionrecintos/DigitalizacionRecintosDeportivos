import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-distribucion-mensual-chart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './distribucion-mensual-chart.component.html',
  styleUrl: './distribucion-mensual-chart.component.css'
})
export class DistribucionMensualChartComponent implements OnInit {
  
  mesesStats: { mes: string, cantidad: number }[] = [];
  
  maxEventosMes: number = 0;

  mesMasEventos! : { mes: string, cantidad: number };

  ngOnInit():void {

    
    
    
  } 
  
  abrirModalInfo(titulo : string, descripcion: string){}
    
  
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
