package com.recintos.municipalidad.service;

import com.recintos.municipalidad.model.Notificacion;
import com.recintos.municipalidad.model.Usuario;

import java.util.List;

public interface ServicioNotificacion {
    void enviarNotificacion(Usuario usuario, String mensaje, Long idEvento);

    List<Notificacion> obtenerNotificacionesPorUsuario(Long idUsuario);

    void marcarComoLeida(Long idNotificacion);
}
