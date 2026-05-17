package com.recintos.municipalidad.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class InscripcionCursoMasivaDTO {
    private Long idUsuario;
    private Long idCurso;
    private List<InscripcionDTO> listaInscripcion;
}
