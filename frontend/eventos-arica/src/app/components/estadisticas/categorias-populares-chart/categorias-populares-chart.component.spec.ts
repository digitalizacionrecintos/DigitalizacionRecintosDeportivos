import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoriasPopularesChartComponent } from './categorias-populares-chart.component';

describe('CategoriasPopularesChartComponent', () => {
  let component: CategoriasPopularesChartComponent;
  let fixture: ComponentFixture<CategoriasPopularesChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoriasPopularesChartComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CategoriasPopularesChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
