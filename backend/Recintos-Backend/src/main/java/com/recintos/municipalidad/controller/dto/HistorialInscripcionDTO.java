package com.recintos.municipalidad.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialInscripcionDTO {
    private Long idInscripcion;
    private Long idEvento;
    private String tituloEvento;
    private LocalDateTime fechaEvento;
    private String ubicacionRecinto;
    private String estadoEvento;
    private String estadoAsistencia;
    private LocalDateTime fechaInscripcion;
}
