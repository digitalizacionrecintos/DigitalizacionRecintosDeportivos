package com.recintos.municipalidad.controller.dto;

import com.google.type.Date;
import com.recintos.municipalidad.model.Inscripcion;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data

public class SesionDTO {

    String tituloEvento;
    LocalDate fechaInicio;
    LocalDateTime horaInicio;
    LocalDateTime horaFin;
    List<Inscripcion> inscripciones;
    boolean inscrito;

}
