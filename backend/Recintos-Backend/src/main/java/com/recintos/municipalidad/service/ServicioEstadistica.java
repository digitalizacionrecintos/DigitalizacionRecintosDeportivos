package com.recintos.municipalidad.service;

import com.recintos.municipalidad.controller.dto.EstadisticasResponseDTO;

public interface ServicioEstadistica {
    EstadisticasResponseDTO obtenerEstadisticas(Integer anio);
}
