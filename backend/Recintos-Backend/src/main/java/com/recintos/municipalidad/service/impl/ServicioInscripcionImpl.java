package com.recintos.municipalidad.service.impl;

import com.recintos.municipalidad.controller.dto.*;
import com.recintos.municipalidad.model.Curso;
import com.recintos.municipalidad.model.Evento;
import com.recintos.municipalidad.model.Inscripcion;
import com.recintos.municipalidad.model.Usuario;
import com.recintos.municipalidad.model.enums.EstadoCurso;
import com.recintos.municipalidad.repository.RepositorioCurso;
import com.recintos.municipalidad.repository.RepositorioEvento;
import com.recintos.municipalidad.repository.RepositorioInscripcion;
import com.recintos.municipalidad.repository.RepositorioUsuario;
import com.recintos.municipalidad.service.ServicioEvento;
import com.recintos.municipalidad.service.ServicioInscripcion;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioInscripcionImpl implements ServicioInscripcion {

    @Autowired
    private RepositorioInscripcion repositorioInscripcion;

    @Autowired
    private RepositorioEvento repositorioEvento;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RepositorioCurso repositorioCurso;

    @Autowired
    private ServicioEvento servicioEvento;

    @Override
    public Inscripcion inscribirUsuario(String nombre, String apellido, Integer edad, Long idTutor, Long idEvento) {
        Evento evento = repositorioEvento.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        Usuario tutor = null;
        if (idTutor != null) {
            tutor = repositorioUsuario.findById(idTutor)
                    .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
        }

        if (evento.getEstado() == null || !"DISPONIBLE".equalsIgnoreCase(evento.getEstado().trim())) {
            throw new RuntimeException(
                    "El evento no está disponible para inscripción (Estado: " + evento.getEstado()
                            + ")");
        }

        long inscritos = repositorioInscripcion.countByEvento(evento);
        if (inscritos >= evento.getCupoMaximo()) {
            throw new com.recintos.municipalidad.exception.SinCupoException(
                    "No hay cupos disponibles para este evento");
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setNombre(nombre);
        inscripcion.setApellido(apellido);
        inscripcion.setEdad(edad);
        inscripcion.setTutor(tutor);
        inscripcion.setEvento(evento);
        inscripcion.setFechaHoraRegistro(LocalDateTime.now());
        inscripcion.setEstadoAsistencia("PENDIENTE");

        Inscripcion nuevaInscripcion = repositorioInscripcion.save(inscripcion);

        CupoEventoDTO cupoActualizado = servicioEvento.obtenerCupo(idEvento);

        messagingTemplate.convertAndSend("/topic/event/" + idEvento + "/quota", cupoActualizado);

        return nuevaInscripcion;
    }

    @Override
    public List<InscripcionBatchResponseDTO> inscribirUsuariosMasivo(
            List<InscripcionDTO> inscripciones) {
        List<InscripcionBatchResponseDTO> respuestas = new ArrayList<>();

        for (InscripcionDTO inscripcionDTO : inscripciones) {
            try {
                Inscripcion inscripcion = inscribirUsuario(
                        inscripcionDTO.getNombre(),
                        inscripcionDTO.getApellidos(),
                        inscripcionDTO.getEdad(),
                        inscripcionDTO.getIdTutor(),
                        inscripcionDTO.getIdEvento());

                respuestas.add(new InscripcionBatchResponseDTO(
                        inscripcion.getIdInscripcion(),
                        inscripcionDTO.getNombre(),
                        inscripcionDTO.getApellidos(),
                        true,
                        "Inscripción exitosa"));
            } catch (com.recintos.municipalidad.exception.SinCupoException e) {
                respuestas.add(new InscripcionBatchResponseDTO(
                        null,
                        inscripcionDTO.getNombre(),
                        inscripcionDTO.getApellidos(),
                        false,
                        "Sin cupos disponibles"));
            } catch (RuntimeException e) {
                respuestas.add(new InscripcionBatchResponseDTO(
                        null,
                        inscripcionDTO.getNombre(),
                        inscripcionDTO.getApellidos(),
                        false,
                        e.getMessage()));
            }
        }
        return respuestas;
    }

    @Override
    public void actualizarAsistenciaMasiva(Long idEvento, List<Long> idsPresentes) {
        Evento evento = repositorioEvento.findById(idEvento).orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        List<Inscripcion> todasInscripciones = repositorioInscripcion.findByEvento(evento);

        for (Inscripcion inscripcion : todasInscripciones) {
            if (idsPresentes != null && idsPresentes.contains(inscripcion.getIdInscripcion())) {
                inscripcion.setEstadoAsistencia("PRESENTE");
            } else {
                inscripcion.setEstadoAsistencia("AUSENTE");
            }
        }
        repositorioInscripcion.saveAll(todasInscripciones);
    }

    @Transactional
    @Override
    public void inscribirUsuariosACurso(InscripcionCursoMasivaDTO inscripciones) {

        int cantidadAInscribir = inscripciones.getListaInscripcion().size();

        Curso curso = repositorioCurso.findByIdParaInscribir(inscripciones.getIdCurso())
                .orElseThrow(() -> new RuntimeException("Curso no encontrado") );

        EstadoCurso estado_curso = curso.getEstado();

        if (cantidadAInscribir > curso.getMaximoPorInscripcion()){
            throw new RuntimeException(
                    "No puedes inscribir mas de " + curso.getMaximoPorInscripcion() + "personas"
            );
        }


        if (estado_curso != EstadoCurso.PUBLICADO){
            throw new RuntimeException(
                    "No se puede inscribir porque el estado no esta publicado, esta en estado" + estado_curso
            );
        }

        List<Evento> lista_eventos = curso.getSesiones();
        /// creo que hay un problema con los apellidos (revisar si se puede hacer mas eficiente esta funcion)
        List<InscripcionDTO> lista_inscripcion_curso = inscripciones.getListaInscripcion();
        for (Evento evento : lista_eventos) {
            for (InscripcionDTO inscripcion : lista_inscripcion_curso ){
                inscribirUsuario(
                        inscripcion.getNombre(),
                        inscripcion.getApellidos(),
                        inscripcion.getEdad(),
                        inscripcion.getIdTutor(),
                        evento.getIdEvento());
            }
        }
        System.out.println("Inscripcion a curso exitosa");
    }

    @Override
    public SesionDTO verificarEstadoInscripcion(Long idEvento, Long idUsuario) {

        /// ahora los estados inscripcion deben traer informoracion de los inscritos (nombre edad, fechaInscripcion)
        Usuario usuario = repositorioUsuario.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Evento evento = repositorioEvento.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        SesionDTO sesionEvento = new SesionDTO();
        sesionEvento.setTituloEvento( evento.getTitulo() );
        sesionEvento.setFechaInicio( evento.getFechaInicio() );
        sesionEvento.setHoraInicio( evento.getHoraInicio()) ;
        sesionEvento.setHoraFin( evento.getHoraFin() );

        List<Inscripcion> inscripcionesASesion = repositorioInscripcion.findByEventoAndTutor(evento,usuario);

        sesionEvento.setInscripciones( inscripcionesASesion );

        return sesionEvento;

    }



    public InscripcionEstadoCursoResponseDTO verificarEstadoInscripcionACurso(Long idCurso, Long idUsuario){
        Curso curso = repositorioCurso.findById(idCurso)
                .orElseThrow(() ->  new RuntimeException("Este curso no existe"));
        Usuario usuario = repositorioUsuario.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Evento> eventos = curso.getSesiones();
        List<SesionDTO> sesionesDTO = new ArrayList<>();

        InscripcionEstadoCursoResponseDTO estadoCursoInscripcion = new InscripcionEstadoCursoResponseDTO();
        boolean usuarioInscrito = false;

        for (Evento evento : eventos){
            SesionDTO sesionDTO = verificarEstadoInscripcion(evento.getIdEvento(), idUsuario);
            sesionesDTO.add(sesionDTO);

            if (!sesionDTO.getInscripciones().isEmpty()) {
                usuarioInscrito = true;
            }
        }

        estadoCursoInscripcion.setInscrito(usuarioInscrito);
        estadoCursoInscripcion.setSesiones(sesionesDTO);
        return estadoCursoInscripcion;
    };

}
