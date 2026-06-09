package com.recintos.municipalidad.service.impl;

import com.recintos.municipalidad.controller.dto.CrearEventoDTO;
import com.recintos.municipalidad.controller.dto.EditarEventoDTO;
import com.recintos.municipalidad.controller.dto.ResponseEventoDTO;
import com.recintos.municipalidad.mappers.EventoMapper;
import com.recintos.municipalidad.model.Evento;
import com.recintos.municipalidad.model.Recinto;
import com.recintos.municipalidad.model.Usuario;
import com.recintos.municipalidad.repository.RepositorioEvento;
import com.recintos.municipalidad.repository.RepositorioRecinto;
import com.recintos.municipalidad.repository.RepositorioUsuario;
import com.recintos.municipalidad.service.ServicioEvento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioEventoImpl implements ServicioEvento {

  @Autowired
  private RepositorioEvento repositorioEvento;

  @Autowired
  private RepositorioRecinto repositorioRecinto;

  @Autowired
  private RepositorioUsuario repositorioUsuario;

  @Autowired
  private com.recintos.municipalidad.repository.RepositorioInscripcion repositorioInscripcion;

  @Autowired
  private com.recintos.municipalidad.repository.RepositorioCategoria repositorioCategoria;

  @Autowired
  private com.recintos.municipalidad.repository.RepositorioCurso repositorioCurso;

  @Autowired
  private EventoMapper mapper;

  @Autowired
  private com.recintos.municipalidad.service.ServicioNotificacion servicioNotificacion;

  @Override
  public List<Evento> listarEventos() {
    List<Evento> eventos = repositorioEvento.findAll();
    eventos.forEach(evento -> {
      long count = repositorioInscripcion.countByEvento(evento);
      evento.setInscritos(count);
    });
    return eventos;
  }

  @Override
  public List<Evento> listarEventosSinCurso() {
    List<Evento> eventos = repositorioEvento.findByCursoIsNullAndEstado("DISPONIBLE");
    eventos.forEach(evento -> {
      long count = repositorioInscripcion.countByEvento(evento);
      evento.setInscritos(count);
    });
    return eventos;
  }

  @Override
  public List<Evento> listarEventosParaCliente() {
    List<Evento> eventos = repositorioEvento.findByCursoIsNull();
    eventos.forEach(evento -> {
      long count = repositorioInscripcion.countByEvento(evento);
      evento.setInscritos(count);
    });
    return eventos;
  }

  @Override
  public Optional<Evento> buscarEvento(Long idEvento) {
    Optional<Evento> eventoOpt = repositorioEvento.findById(idEvento);
    eventoOpt.ifPresent(evento -> {
      long count = repositorioInscripcion.countByEvento(evento);
      evento.setInscritos(count);
    });
    return eventoOpt;
  }

  @Override
  public ResponseEventoDTO guardarEvento(CrearEventoDTO eventoDTO) {
    Evento nuevoEvento = mapper.toEntity(eventoDTO);
    nuevoEvento.setEstado("EN_ESPERA");

    if (eventoDTO.getImagenUrl() != null) {
      nuevoEvento.setImagenUrl(eventoDTO.getImagenUrl());
    }

    if (eventoDTO.getEncargadoId() != null) {
      Optional<Usuario> encargado = repositorioUsuario.findById(eventoDTO.getEncargadoId());
      encargado.ifPresent(nuevoEvento::setEncargado);
    }

    if (eventoDTO.getRecintoId() != null) {
      Optional<Recinto> recintoOpt = repositorioRecinto.findById(eventoDTO.getRecintoId());
      recintoOpt.ifPresent(nuevoEvento::setRecinto);
    }

    if (eventoDTO.getCategoriaId() != null) {
      Optional<com.recintos.municipalidad.model.Categoria> categoriaOpt = repositorioCategoria
          .findById(eventoDTO.getCategoriaId());
      categoriaOpt.ifPresent(nuevoEvento::setCategoria);
    }

    if (eventoDTO.getCursoId() != null) {
        Optional<com.recintos.municipalidad.model.Curso> cursoOpt = repositorioCurso.findById(eventoDTO.getCursoId());
        cursoOpt.ifPresent(nuevoEvento::setCurso);
    }

    Evento eventoGuardado = repositorioEvento.save(nuevoEvento);
    return mapper.toDTO(eventoGuardado);
  }

  @Override
  public Evento editarEvento(Long id, EditarEventoDTO editarEventoDTO) {
    Optional<Evento> eventoOpt = repositorioEvento.findById(id);
    if (eventoOpt.isPresent()) {

      Evento evento = eventoOpt.get();
      if (editarEventoDTO.getDescripcion() != null)
        evento.setDescripcion(editarEventoDTO.getDescripcion());
      if (editarEventoDTO.getImagenUrl() != null)
        evento.setImagenUrl(editarEventoDTO.getImagenUrl());
      if (editarEventoDTO.getHoraInicio() != null)
        evento.setHoraInicio(editarEventoDTO.getHoraInicio());
      if (editarEventoDTO.getHoraFin() != null)
        evento.setHoraFin(editarEventoDTO.getHoraFin());
      if (editarEventoDTO.getFechaInicio() != null)
        evento.setFechaInicio(editarEventoDTO.getFechaInicio());
      if (editarEventoDTO.getCupoMaximo() > 0)
        evento.setCupoMaximo(editarEventoDTO.getCupoMaximo());

      if (editarEventoDTO.getEncargadoId() != null) {
        Optional<Usuario> encargado = repositorioUsuario.findById(editarEventoDTO.getEncargadoId());
        encargado.ifPresent(evento::setEncargado);
      }

      if (editarEventoDTO.getRecintoId() != null) {
        Optional<Recinto> recintoOpt = repositorioRecinto.findById(editarEventoDTO.getRecintoId());
        recintoOpt.ifPresent(evento::setRecinto);
      }

      if (editarEventoDTO.getCategoriaId() != null) {
        Optional<com.recintos.municipalidad.model.Categoria> categoriaOpt = repositorioCategoria
            .findById(editarEventoDTO.getCategoriaId());
        categoriaOpt.ifPresent(evento::setCategoria);
      }

      if (editarEventoDTO.getCursoId() != null) {
          Optional<com.recintos.municipalidad.model.Curso> cursoOpt = repositorioCurso.findById(editarEventoDTO.getCursoId());
          cursoOpt.ifPresent(evento::setCurso);
      }

      Evento eventoGuardado = repositorioEvento.save(evento);

      if (eventoGuardado.getInscripciones() != null) {
        for (com.recintos.municipalidad.model.Inscripcion inscripcion : eventoGuardado.getInscripciones()) {
          if (inscripcion.getTutor() != null) {
            servicioNotificacion.enviarNotificacion(
                inscripcion.getTutor(),
                "El evento '" + eventoGuardado.getTitulo() + "' ha sido modificado.",
                eventoGuardado.getIdEvento());
          }
        }
      }

      return eventoGuardado;
    }
    return null;
  }

  @Override
  public Evento eliminarEvento(int IdEvento) {
    Long id = (long) IdEvento;
    Optional<Evento> eventoOpt = repositorioEvento.findById(id);
    if (eventoOpt.isPresent()) {
      repositorioEvento.deleteById(id);
      return eventoOpt.get();
    }
    return null;
  }

  @Override
  public Evento cambiarEstado(Long id, String estado) {
    Optional<Evento> eventoOpt = repositorioEvento.findById(id);
    if (eventoOpt.isPresent()) {
      Evento evento = eventoOpt.get();
      evento.setEstado(estado);
      Evento eventoGuardado = repositorioEvento.save(evento);

      if (eventoGuardado.getInscripciones() != null) {
        for (com.recintos.municipalidad.model.Inscripcion inscripcion : eventoGuardado.getInscripciones()) {
          if (inscripcion.getTutor() != null) {
            servicioNotificacion.enviarNotificacion(
                inscripcion.getTutor(),
                "El estado del evento '" + eventoGuardado.getTitulo() + "' ha cambiado a " + estado + ".",
                eventoGuardado.getIdEvento());
          }
        }
      }

      return eventoGuardado;
    }
    return null;
  }

  @Override
  public List<Evento> listarEventosEnTranscurso() {
    List<Evento> eventos = repositorioEvento.findByEstado("DISPONIBLE");
    eventos.forEach(evento -> {
      long count = repositorioInscripcion.countByEvento(evento);
      evento.setInscritos(count);
    });
    return eventos;
  }

  @Override
  public com.recintos.municipalidad.controller.dto.CupoEventoDTO obtenerCupo(Long idEvento) {
    Evento evento = repositorioEvento.findById(idEvento)
        .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

    long inscritos = repositorioInscripcion.countByEvento(evento);
    long disponibles = evento.getCupoMaximo() - inscritos;
    if (disponibles < 0) {
      disponibles = 0;
    }

    return new com.recintos.municipalidad.controller.dto.CupoEventoDTO(
        evento.getIdEvento(),
        evento.getCupoMaximo(),
        inscritos,
        disponibles);
  }

  @Override
  public List<com.recintos.municipalidad.controller.dto.EventoConAsistentesDTO> listarEventosPorEncargado(
      Long idEncargado) {
    Usuario encargado = repositorioUsuario.findById(idEncargado)
        .orElseThrow(() -> new RuntimeException("Encargado no encontrado"));

    List<Evento> eventos = repositorioEvento.findByEncargado(encargado);

    return eventos.stream().map(evento -> {
      List<com.recintos.municipalidad.controller.dto.AsistenteDTO> asistentes = evento.getInscripciones().stream()
          .map(inscripcion -> new com.recintos.municipalidad.controller.dto.AsistenteDTO(
              inscripcion.getIdInscripcion(),
              inscripcion.getTutor() != null ? inscripcion.getTutor().getIdUsuario() : null,
              inscripcion.getNombre() + " " + inscripcion.getApellido(),
              inscripcion.getTutor() != null ? inscripcion.getTutor().getCorreo() : "Sin correo",
              inscripcion.getEstadoAsistencia()))
          .collect(java.util.stream.Collectors.toList());

      return new com.recintos.municipalidad.controller.dto.EventoConAsistentesDTO(
          evento.getIdEvento(),
          evento.getTitulo(),
          evento.getFechaInicio().atStartOfDay(),
          evento.getHoraInicio(),
          evento.getHoraFin(),
          evento.getEstado(),
          evento.getRecinto() != null ? evento.getRecinto().getUbicacion() : "Sin ubicación",
          evento.getImagenUrl(),
          asistentes,
          evento.getCategoria() != null ? mapCategoriaToDTO(evento.getCategoria()) : null,
          evento.getRecinto() != null
              ? new com.recintos.municipalidad.controller.dto.EventoConAsistentesDTO.RecintoInfoDTO(
                  evento.getRecinto().getNombre(), evento.getRecinto().getImagenUrl())
              : null);
    }).collect(java.util.stream.Collectors.toList());

  }

  @Override
  public Evento asignarEncargado(Long idEvento, Long idEncargado) {
    Evento evento = repositorioEvento.findById(idEvento)
        .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

    Usuario encargado = repositorioUsuario.findById(idEncargado)
        .orElseThrow(() -> new RuntimeException("Encargado no encontrado"));

    if (!"ROLE_ENCARGADO".equals(encargado.getRol())) {
      throw new RuntimeException("El usuario no tiene rol de encargado");
    }

    evento.setEncargado(encargado);
    return repositorioEvento.save(evento);
  }

  private com.recintos.municipalidad.controller.dto.CategoriaDTO mapCategoriaToDTO(
      com.recintos.municipalidad.model.Categoria categoria) {
    com.recintos.municipalidad.controller.dto.CategoriaDTO dto = new com.recintos.municipalidad.controller.dto.CategoriaDTO();
    dto.setId(categoria.getId());
    dto.setNombre(categoria.getNombre());
    dto.setDescripcion(categoria.getDescripcion());
    return dto;
  }

  @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000)
  public void verificarEventosTerminados() {
    java.time.LocalDateTime now = java.time.LocalDateTime.now();

    List<Evento> eventos = repositorioEvento.findAll();

    for (Evento evento : eventos) {
      if ("TERMINADO".equals(evento.getEstado())) {
        continue;
      }

      if (evento.getFechaInicio() != null && evento.getHoraFin() != null) {
        java.time.LocalDateTime finEvento = java.time.LocalDateTime.of(evento.getFechaInicio(),
            evento.getHoraFin().toLocalTime());

        if (now.isAfter(finEvento)) {
          evento.setEstado("TERMINADO");
          repositorioEvento.save(evento);
          System.out.println("Evento ID " + evento.getIdEvento() + " finalizado automáticamente.");
        }
      }
    }
  }

  public List<Evento> listarEventosDisponibles() {
    List<Evento> eventos = repositorioEvento.findByEstado("DISPONIBLE");
    eventos.forEach(evento -> {
      long count = repositorioInscripcion.countByEvento(evento);
      evento.setInscritos(count);
    });
    return eventos;
  }
}
