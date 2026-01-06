package com.recintos.municipalidad.service;

import com.recintos.municipalidad.model.Inscripcion;

public interface ServicioInscripcion {
    Inscripcion inscribirUsuario(Long idUsuario, Long idEvento);

    com.recintos.municipalidad.controller.dto.EstadoInscripcionDTO verificarEstadoInscripcion(Long idUsuario,
            Long idEvento);

    void actualizarAsistenciaMasiva(Long idEvento, java.util.List<Long> idsPresentes);
}
