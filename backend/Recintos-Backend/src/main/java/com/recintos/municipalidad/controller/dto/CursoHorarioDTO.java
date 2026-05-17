package com.recintos.municipalidad.controller.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Data;

@Data
public class CursoHorarioDTO {
    private DayOfWeek dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
