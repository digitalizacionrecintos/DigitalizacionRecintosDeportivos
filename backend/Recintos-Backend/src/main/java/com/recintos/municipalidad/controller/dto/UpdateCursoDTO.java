package com.recintos.municipalidad.controller.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class UpdateCursoDTO {
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String dias;
    private Integer cupo;
    private Integer maximoPorInscripcion;
    private Long idRecinto;
    private Long idEncargado;
    private Long idCategoria;
    private List<CursoHorarioDTO> horarios;
    private com.recintos.municipalidad.model.enums.EstadoCurso estado;
}
