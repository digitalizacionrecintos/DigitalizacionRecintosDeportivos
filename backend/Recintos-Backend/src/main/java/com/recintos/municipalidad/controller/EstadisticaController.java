package com.recintos.municipalidad.controller;

import com.recintos.municipalidad.controller.dto.EstadisticasResponseDTO;
import com.recintos.municipalidad.service.ServicioEstadistica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaController {

    @Autowired
    private ServicioEstadistica servicioEstadistica;

    @GetMapping("/general")
    public ResponseEntity<EstadisticasResponseDTO> obtenerEstadisticasGenerales(
            @RequestParam(required = false) Integer anio) {
        EstadisticasResponseDTO estadisticas = servicioEstadistica.obtenerEstadisticas(anio);
        return new ResponseEntity<>(estadisticas, HttpStatus.OK);
    }
}
