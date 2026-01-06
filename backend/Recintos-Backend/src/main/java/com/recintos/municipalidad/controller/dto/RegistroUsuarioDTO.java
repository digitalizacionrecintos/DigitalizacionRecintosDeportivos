package com.recintos.municipalidad.controller.dto;

import lombok.Data;

@Data
public class RegistroUsuarioDTO {

    private String correo;

    private String contrasena;

    private String nombre;

    private String apellido;

    private String telefono;
}
