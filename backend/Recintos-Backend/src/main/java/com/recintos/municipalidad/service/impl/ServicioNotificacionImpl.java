package com.recintos.municipalidad.service.impl;

import com.recintos.municipalidad.model.Notificacion;
import com.recintos.municipalidad.model.Usuario;
import com.recintos.municipalidad.repository.RepositorioNotificacion;
import com.recintos.municipalidad.repository.RepositorioUsuario;
import com.recintos.municipalidad.service.ServicioNotificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServicioNotificacionImpl implements ServicioNotificacion {

    @Autowired
    private RepositorioNotificacion repositorioNotificacion;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private com.recintos.municipalidad.service.ServicioFirebase servicioFirebase;

    @Override
    public void enviarNotificacion(Usuario usuario, String mensaje, Long idEvento) {

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setMensaje(mensaje);
        notificacion.setFecha(LocalDateTime.now());
        notificacion.setLeido(false);
        notificacion.setIdEvento(idEvento);
        repositorioNotificacion.save(notificacion);

        messagingTemplate.convertAndSend("/topic/user/" + usuario.getIdUsuario(), notificacion);

        if (usuario.getFcmToken() != null && !usuario.getFcmToken().isEmpty()) {
            servicioFirebase.enviarNotificacionPush(usuario.getFcmToken(), "Actualización de Evento", mensaje);
        }
    }

    @Override
    public List<Notificacion> obtenerNotificacionesPorUsuario(Long idUsuario) {
        Usuario usuario = repositorioUsuario.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return repositorioNotificacion.findByUsuarioOrderByFechaDesc(usuario);
    }

    @Override
    public void marcarComoLeida(Long idNotificacion) {
        Notificacion notificacion = repositorioNotificacion.findById(idNotificacion)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notificacion.setLeido(true);
        repositorioNotificacion.save(notificacion);
    }
}
