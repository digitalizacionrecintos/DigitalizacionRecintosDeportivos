package com.recintos.municipalidad.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsistenteDTO {
    private Long idInscripcion;
    private Long idUsuario;
    private String nombreCompleto;
    private String correo;
    private String estadoAsistencia;
}
