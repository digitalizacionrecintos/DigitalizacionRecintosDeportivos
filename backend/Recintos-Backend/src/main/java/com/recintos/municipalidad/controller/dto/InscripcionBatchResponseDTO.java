package com.recintos.municipalidad.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InscripcionBatchResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private Boolean exitosa;
    private String mensaje;
}
