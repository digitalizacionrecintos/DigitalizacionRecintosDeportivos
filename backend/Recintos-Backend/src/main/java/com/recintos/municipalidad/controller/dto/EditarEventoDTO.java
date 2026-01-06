package com.recintos.municipalidad.controller.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

import java.util.List;

@Data
public class EditarEventoDTO {

    private String descripcion;

    private String imagenUrl;

    private LocalDateTime horaInicio;

    private LocalDateTime horaFin;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private int cupoMaximo;

    private Long recintoId;

    private Long encargadoId;

    private String publicoObjetivo;

    private Long categoriaId;
}
