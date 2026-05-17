package com.recintos.municipalidad.controller.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ResponseEventoDTO {

    private Long idEvento;

    private String estado;

    private String titulo;

    private String descripcion;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime horaInicio;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime horaFin;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    private CategoriaDTO categoria;

    private int cupoMaximo;

    private String publicoObjetivo;

    private Long cursoId;
    
    private String cursoNombre;

    private Long inscritosActuales;

    private ResponseRecintoDTO recinto;

    private ResponseUsuarioDTO encargado;

    private Long encargadoId;

    private Long categoriaId;

    private Integer maximoPorInscripcion;
}
