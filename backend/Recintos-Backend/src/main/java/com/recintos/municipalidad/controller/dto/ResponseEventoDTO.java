package com.recintos.municipalidad.controller.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ResponseEventoDTO {

    private int IdEvento;

    private String estado;

    private String titulo;

    private String descripcion;

    private LocalDateTime hora;

    private LocalDate fechaIniceditio;

    private LocalDate fechaFin;

    private CategoriaDTO categoria;

    private int cupoMaximo;

    private String publicoObjetivo;

}
