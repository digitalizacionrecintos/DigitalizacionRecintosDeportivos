package com.recintos.municipalidad.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadoInscripcionDTO {
    private boolean yaInscrito;
    private boolean cupoDisponible;
    private boolean puedeInscribirse;
    private String mensaje;
    private String estadoEvento;
    private Long inscritos;
    private Integer cupoMaximo;
    private Long inscripcionesUsuario;
}
