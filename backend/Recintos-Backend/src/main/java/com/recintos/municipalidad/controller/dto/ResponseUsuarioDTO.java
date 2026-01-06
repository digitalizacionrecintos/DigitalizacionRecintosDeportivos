package com.recintos.municipalidad.controller.dto;

import lombok.Data;

@Data
public class ResponseUsuarioDTO {

    private Long id;

    private String nombre;

    private String apellido;

    private String correo;

    private String rol;

    private String telefono;

    private String informacion;

}
