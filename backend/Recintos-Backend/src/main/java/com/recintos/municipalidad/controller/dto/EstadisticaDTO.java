package com.recintos.municipalidad.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticaDTO {
    private double porcentajeAsistencia;
    private double tasaAusentismo;
    private Map<String, Long> recintosMasUtilizados;
    private Map<String, Long> categoriasMasSolicitadas;
    private Map<String, Long> eventosPorMes;
}
