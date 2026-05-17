package com.recintos.municipalidad.service;

import com.recintos.municipalidad.controller.dto.*;
import com.recintos.municipalidad.model.Inscripcion;

import java.util.List;

public interface ServicioInscripcion {
    Inscripcion inscribirUsuario(String nombre, String apellido, Integer edad, Long idTutor, Long idEvento);

    List<InscripcionBatchResponseDTO> inscribirUsuariosMasivo(List<InscripcionDTO> inscripciones);

    SesionDTO verificarEstadoInscripcion(Long idEvento, Long idUsuario);

    void actualizarAsistenciaMasiva(Long idEvento, List<Long> idsPresentes);

    void inscribirUsuariosACurso(InscripcionCursoMasivaDTO inscripciones);

    InscripcionEstadoCursoResponseDTO verificarEstadoInscripcionACurso(Long idCurso, Long idUsuario);
}
