package com.recintos.municipalidad.controller.dto;

import lombok.Data;

@Data
public class UpdateEncargadoDTO {
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
}
