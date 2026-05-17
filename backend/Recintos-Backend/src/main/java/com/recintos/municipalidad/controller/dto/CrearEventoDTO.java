package com.recintos.municipalidad.controller.dto;

import lombok.Data;

import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CrearEventoDTO {

    private String titulo;

    private String descripcion;

    private String imagenUrl;

    private LocalDateTime horaInicio;

    private LocalDateTime horaFin;

    private LocalDate fechaInicio;

    private Long categoriaId;

    private int cupoMaximo;

    private String publicoObjetivo;

    private Long recintoId;

    private Long encargadoId;

    private Integer maximoPorInscripcion;

    private Long cursoId;

}
