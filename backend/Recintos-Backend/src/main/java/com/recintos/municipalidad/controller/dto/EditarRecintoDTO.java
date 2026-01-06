package com.recintos.municipalidad.controller.dto;

import lombok.Data;

@Data
public class EditarRecintoDTO {
    private String nombre;
    private String ubicacion;
    private String descripcion;
    private String imagenUrl;
    private Integer capacidad;
    private String coordenadasGPS;
    private String estado;
}
