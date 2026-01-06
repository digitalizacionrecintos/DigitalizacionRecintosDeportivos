package com.recintos.municipalidad.service.impl;

import com.recintos.municipalidad.model.Evento;
import com.recintos.municipalidad.model.Inscripcion;
import com.recintos.municipalidad.model.Usuario;
import com.recintos.municipalidad.repository.RepositorioEvento;
import com.recintos.municipalidad.repository.RepositorioInscripcion;
import com.recintos.municipalidad.repository.RepositorioUsuario;
import com.recintos.municipalidad.service.ServicioInscripcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ServicioInscripcionImpl implements ServicioInscripcion {

        @Autowired
        private RepositorioInscripcion repositorioInscripcion;

        @Autowired
        private RepositorioEvento repositorioEvento;

        @Autowired
        private RepositorioUsuario repositorioUsuario;

        @Autowired
        private org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

        @Autowired
        private com.recintos.municipalidad.service.ServicioEvento servicioEvento;

        @Override
        public Inscripcion inscribirUsuario(Long idUsuario, Long idEvento) {
                Evento evento = repositorioEvento.findById(idEvento)
                                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

                Usuario usuario = repositorioUsuario.findById(idUsuario)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                if (evento.getEstado() == null || !"DISPONIBLE".equalsIgnoreCase(evento.getEstado().trim())) {
                        throw new RuntimeException(
                                        "El evento no está disponible para inscripción (Estado: " + evento.getEstado()
                                                        + ")");
                }

                if (repositorioInscripcion.existsByUsuarioAndEvento(usuario, evento)) {
                        throw new com.recintos.municipalidad.exception.UsuarioYaInscritoException(
                                        "El usuario ya está inscrito en este evento");
                }

                long inscritos = repositorioInscripcion.countByEvento(evento);
                if (inscritos >= evento.getCupoMaximo()) {
                        throw new com.recintos.municipalidad.exception.SinCupoException(
                                        "No hay cupos disponibles para este evento");
                }

                Inscripcion inscripcion = new Inscripcion();
                inscripcion.setUsuario(usuario);
                inscripcion.setEvento(evento);
                inscripcion.setFechaHoraRegistro(LocalDateTime.now());
                inscripcion.setEstadoAsistencia("PENDIENTE");

                Inscripcion nuevaInscripcion = repositorioInscripcion.save(inscripcion);

                com.recintos.municipalidad.controller.dto.CupoEventoDTO cupoActualizado = servicioEvento
                                .obtenerCupo(idEvento);
                messagingTemplate.convertAndSend("/topic/event/" + idEvento + "/quota", cupoActualizado);

                return nuevaInscripcion;
        }

        @Override
        public com.recintos.municipalidad.controller.dto.EstadoInscripcionDTO verificarEstadoInscripcion(Long idUsuario,
                        Long idEvento) {
                Evento evento = repositorioEvento.findById(idEvento)
                                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

                Usuario usuario = repositorioUsuario.findById(idUsuario)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                boolean yaInscrito = repositorioInscripcion.existsByUsuarioAndEvento(usuario, evento);
                long inscritos = repositorioInscripcion.countByEvento(evento);
                boolean cupoDisponible = inscritos < evento.getCupoMaximo();
                boolean eventoDisponible = evento.getEstado() != null
                                && "DISPONIBLE".equalsIgnoreCase(evento.getEstado().trim());

                boolean puedeInscribirse = !yaInscrito && cupoDisponible && eventoDisponible;

                String mensaje = "Puede inscribirse";
                if (yaInscrito) {
                        mensaje = "Usuario ya inscrito";
                } else if (!eventoDisponible) {
                        mensaje = "Evento no disponible";
                } else if (!cupoDisponible) {
                        mensaje = "Sin cupos disponibles";
                }

                return new com.recintos.municipalidad.controller.dto.EstadoInscripcionDTO(
                                yaInscrito,
                                cupoDisponible,
                                puedeInscribirse,
                                mensaje,
                                evento.getEstado(),
                                inscritos,
                                evento.getCupoMaximo());
        }

        @Override
        public void actualizarAsistenciaMasiva(Long idEvento, java.util.List<Long> idsPresentes) {
                Evento evento = repositorioEvento.findById(idEvento)
                                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

                java.util.List<Inscripcion> todasInscripciones = repositorioInscripcion.findByEvento(evento);

                for (Inscripcion inscripcion : todasInscripciones) {
                        if (idsPresentes != null && idsPresentes.contains(inscripcion.getIdInscripcion())) {
                                inscripcion.setEstadoAsistencia("PRESENTE");
                        } else {
                                inscripcion.setEstadoAsistencia("AUSENTE");
                        }
                }
                repositorioInscripcion.saveAll(todasInscripciones);
        }
}
