import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DistribucionMensualChartComponent } from './distribucion-mensual-chart.component';

describe('DistribucionMensualChartComponent', () => {
  let component: DistribucionMensualChartComponent;
  let fixture: ComponentFixture<DistribucionMensualChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DistribucionMensualChartComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DistribucionMensualChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
