package com.recintos.municipalidad.service;

import com.recintos.municipalidad.controller.dto.EstadisticasResponseDTO;
import com.recintos.municipalidad.controller.dto.EstadisticasCursosDTO;

public interface ServicioEstadistica {
    EstadisticasResponseDTO obtenerEstadisticas(Integer anio);
    EstadisticasCursosDTO obtenerEstadisticasCursos(Integer anio);
}
