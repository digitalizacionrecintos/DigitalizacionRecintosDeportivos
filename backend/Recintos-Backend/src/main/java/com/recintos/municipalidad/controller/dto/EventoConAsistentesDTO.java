package com.recintos.municipalidad.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoConAsistentesDTO {
    private Long idEvento;
    private String titulo;
    private LocalDateTime fechaInicio;

    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private String estado;
    private String ubicacionRecinto;
    private String imagenUrl;
    private List<AsistenteDTO> asistentes;
    private CategoriaDTO categoria;
    private RecintoInfoDTO recinto;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecintoInfoDTO {
        private String titulo;
        private String imagenUrl;
    }
}
