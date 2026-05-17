package com.recintos.municipalidad.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialUsuarioDTO {
    private List<CursoHistorialDTO> cursos;
    private List<HistorialInscripcionDTO> eventos;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CursoHistorialDTO {
        private String nombre;
        private List<ParticipanteDTO> participantes;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParticipanteDTO {
        private Long idInscripcion;
        private String nombre;
        private String apellido;
        private String estadoAsistencia;
    }
}
