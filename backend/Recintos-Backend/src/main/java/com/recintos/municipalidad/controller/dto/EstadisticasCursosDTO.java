package com.recintos.municipalidad.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasCursosDTO {
    private ResumenCursos resumen;
    private List<CursoPopular> cursosPopulares;
    private OcupacionCursos ocupacion;
    private List<CategoriaCursos> porCategoria;
    private List<TendenciaMensual> tendenciaMensual;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenCursos {
        private int totalCursos;
        private int totalInscritos;
        private double promedioInscritosPorCurso;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursoPopular {
        private String nombre;
        private int inscritos;
        private int cupoMaximo;
        private double porcentajeOcupacion;
        private String categoria;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OcupacionCursos {
        private int llenos;           // 100% ocupación
        private int altaOcupacion;    // >75%
        private int bajaOcupacion;    // <50%
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoriaCursos {
        private String categoria;
        private int inscritos;
        private int cursos;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TendenciaMensual {
        private String mes;
        private int inscritos;
    }
}
