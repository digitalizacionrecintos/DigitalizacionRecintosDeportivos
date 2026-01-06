package com.recintos.municipalidad.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasResponseDTO {

    private ResumenStatsDTO resumen;
    private List<CategoriaStatsDTO> categorias;
    private List<RecintoStatsDTO> recintos;
    private DistribucionTemporalDTO distribucionTemporal;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenStatsDTO {
        private long totalEventos;
        private double porcentajeAsistencia;
        private double tasaAusentismo;
        private double promedioEventosMensual;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoriaStatsDTO {
        private String nombre;
        private long cantidad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecintoStatsDTO {
        private String nombre;
        private long cantidad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistribucionTemporalDTO {
        private Map<String, Long> porMes;
        private Map<String, Long> porAnio;
        private Map<String, Long> porDia;
    }
}
