package com.recintos.municipalidad.controller.dto;

import lombok.Data;

@Data
public class ResponseRecintoDTO {
    private Long idRecinto;
    private String nombre;
    private String ubicacion;
    private String descripcion;
    private Integer capacidad;
    private String coordenadasGPS;
    private String estado;
}
