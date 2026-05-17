package com.recintos.municipalidad.controller.dto;

import lombok.Data;

@Data
public class InscripcionDTO {
    private String nombre;
    private String apellidos;
    private Integer edad;
    private Long idTutor;
    private Long idEvento;
}
