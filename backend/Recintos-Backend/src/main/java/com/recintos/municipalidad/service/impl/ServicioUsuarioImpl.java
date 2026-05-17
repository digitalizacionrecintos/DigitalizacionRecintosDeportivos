package com.recintos.municipalidad.service.impl;

import com.recintos.municipalidad.controller.dto.HistorialInscripcionDTO;
import com.recintos.municipalidad.controller.dto.HistorialUsuarioDTO;
import com.recintos.municipalidad.controller.dto.LoginUsuarioDTO;
import com.recintos.municipalidad.controller.dto.RegistroUsuarioDTO;
import com.recintos.municipalidad.controller.dto.ResponseUsuarioDTO;
import com.recintos.municipalidad.controller.dto.UpdateEncargadoDTO;
import com.recintos.municipalidad.controller.dto.UpdateUsuarioDTO;
import com.recintos.municipalidad.model.Curso;
import com.recintos.municipalidad.model.Evento;
import com.recintos.municipalidad.model.Inscripcion;
import com.recintos.municipalidad.model.Usuario;
import com.recintos.municipalidad.repository.RepositorioInscripcion;
import com.recintos.municipalidad.repository.RepositorioUsuario;
import com.recintos.municipalidad.service.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicioUsuarioImpl implements ServicioUsuario {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private RepositorioInscripcion repositorioInscripcion;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseUsuarioDTO registrarUsuario(RegistroUsuarioDTO registroDTO) {
        if (repositorioUsuario.findByCorreo(registroDTO.getCorreo()).isPresent()) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.getNombre());
        usuario.setApellido(registroDTO.getApellido());
        usuario.setCorreo(registroDTO.getCorreo());
        usuario.setTelefono(registroDTO.getTelefono());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getContrasena()));
        usuario.setRol("PARTICIPANTE");

        Usuario usuarioGuardado = repositorioUsuario.save(usuario);
        return toDTO(usuarioGuardado);
    }

    @Override
    public ResponseUsuarioDTO login(LoginUsuarioDTO loginDTO) {
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByCorreo(loginDTO.getCorreo());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(loginDTO.getContrasena(), usuario.getPassword())) {
                return toDTO(usuario);
            }
        }
        return null;
    }

    @Override
    public ResponseUsuarioDTO obtenerPerfil(int idUsuario) {
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById((long) idUsuario);
        return usuarioOpt.map(this::toDTO).orElse(null);
    }

    @Override
    public List<Usuario> listarEncargados() {
        return repositorioUsuario.findByRol("ROLE_ENCARGADO");
    }

    @Override
    public HistorialUsuarioDTO obtenerHistorialInscripciones(Long idUsuario) {
        Usuario usuario = repositorioUsuario.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Inscripcion> inscripciones = repositorioInscripcion.findAll();

        List<HistorialInscripcionDTO> eventos = mapearEventos(inscripciones);
        List<HistorialUsuarioDTO.CursoHistorialDTO> cursos = mapearCursos(inscripciones);

        return new HistorialUsuarioDTO(cursos, eventos);
    }

    private List<HistorialInscripcionDTO> mapearEventos(List<Inscripcion> inscripciones) {
        return inscripciones.stream()
                .map(this::mapearEvento)
                .collect(Collectors.toList());
    }

    private HistorialInscripcionDTO mapearEvento(Inscripcion inscripcion) {
        Evento evento = inscripcion.getEvento();
        String ubicacion = evento.getRecinto() != null ? evento.getRecinto().getUbicacion() : "Sin ubicación";

        List<HistorialInscripcionDTO.ParticipanteSimpleDTO> participantes = evento.getInscripciones().stream()
                .map(this::mapearParticipanteSimple)
                .collect(Collectors.toList());

        return new HistorialInscripcionDTO(
                inscripcion.getIdInscripcion(),
                evento.getIdEvento(),
                evento.getTitulo(),
                evento.getHoraInicio(),
                ubicacion,
                evento.getEstado(),
                inscripcion.getEstadoAsistencia(),
                inscripcion.getFechaHoraRegistro(),
                participantes);
    }

    private HistorialInscripcionDTO.ParticipanteSimpleDTO mapearParticipanteSimple(Inscripcion inscripcion) {
        return new HistorialInscripcionDTO.ParticipanteSimpleDTO(
                inscripcion.getIdInscripcion(),
                inscripcion.getNombre(),
                inscripcion.getApellido(),
                inscripcion.getEstadoAsistencia());
    }

    private List<HistorialUsuarioDTO.CursoHistorialDTO> mapearCursos(List<Inscripcion> inscripciones) {
        Map<String, List<HistorialUsuarioDTO.ParticipanteDTO>> cursosMap = new HashMap<>();

        for (Inscripcion inscripcion : inscripciones) {
            Curso curso = inscripcion.getEvento().getCurso();
            if (curso != null) {
                String nombreCurso = curso.getNombre();
                HistorialUsuarioDTO.ParticipanteDTO participante = mapearParticipante(inscripcion);
                cursosMap.computeIfAbsent(nombreCurso, k -> new ArrayList<>()).add(participante);
            }
        }

        return cursosMap.entrySet().stream()
                .map(entry -> new HistorialUsuarioDTO.CursoHistorialDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private HistorialUsuarioDTO.ParticipanteDTO mapearParticipante(Inscripcion inscripcion) {
        return new HistorialUsuarioDTO.ParticipanteDTO(
                inscripcion.getIdInscripcion(),
                inscripcion.getNombre(),
                inscripcion.getApellido(),
                inscripcion.getEstadoAsistencia());
    }

    @Override
    public void guardarTokenFCM(Long idUsuario, String token) {
        Usuario usuario = repositorioUsuario.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setFcmToken(token);
        repositorioUsuario.save(usuario);
    }

    @Override
    public ResponseUsuarioDTO registrarEncargado(RegistroUsuarioDTO registroDTO) {
        if (repositorioUsuario.findByCorreo(registroDTO.getCorreo()).isPresent()) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.getNombre());
        usuario.setApellido(registroDTO.getApellido());
        usuario.setCorreo(registroDTO.getCorreo());
        usuario.setTelefono(registroDTO.getTelefono());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getContrasena()));
        usuario.setRol("ROLE_ENCARGADO");

        Usuario usuarioGuardado = repositorioUsuario.save(usuario);
        return toDTO(usuarioGuardado);
    }

    @Override
    public ResponseUsuarioDTO obtenerEncargado(Long id) {
        return repositorioUsuario.findById(id)
                .filter(u -> "ROLE_ENCARGADO".equals(u.getRol()))
                .map(this::toDTO)
                .orElse(null);
    }

    @Override
    public ResponseUsuarioDTO actualizarEncargado(Long id, UpdateEncargadoDTO updateDTO) {
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
        if (usuarioOpt.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioOpt.get();
        if (!"ROLE_ENCARGADO".equals(usuario.getRol())) {
            return null;
        }

        if (updateDTO.getNombre() != null) {
            usuario.setNombre(updateDTO.getNombre());
        }
        if (updateDTO.getApellido() != null) {
            usuario.setApellido(updateDTO.getApellido());
        }
        if (updateDTO.getTelefono() != null) {
            usuario.setTelefono(updateDTO.getTelefono());
        }
        if (updateDTO.getCorreo() != null) {
            Optional<Usuario> existing = repositorioUsuario.findByCorreo(updateDTO.getCorreo());
            if (existing.isPresent() && !existing.get().getIdUsuario().equals(id)) {
                return null;
            }
            usuario.setCorreo(updateDTO.getCorreo());
        }

        return toDTO(repositorioUsuario.save(usuario));
    }

    @Override
    public boolean eliminarEncargado(Long id) {
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if ("ROLE_ENCARGADO".equals(usuario.getRol())) {
                repositorioUsuario.delete(usuario);
                return true;
            }
        }
        return false;
    }

    @Override
    public ResponseUsuarioDTO actualizarPerfil(Long id, UpdateUsuarioDTO dto) {
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
        if (usuarioOpt.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioOpt.get();

        if (dto.getNombre() != null) {
            usuario.setNombre(dto.getNombre());
        }
        if (dto.getApellido() != null) {
            usuario.setApellido(dto.getApellido());
        }
        if (dto.getTelefono() != null) {
            usuario.setTelefono(dto.getTelefono());
        }
        if (dto.getInformacion() != null) {
            usuario.setInformacion(dto.getInformacion());
        }
        if (dto.getCorreo() != null) {
            Optional<Usuario> existing = repositorioUsuario.findByCorreo(dto.getCorreo());
            if (existing.isPresent() && !existing.get().getIdUsuario().equals(id)) {
                return null;
            }
            usuario.setCorreo(dto.getCorreo());
        }

        return toDTO(repositorioUsuario.save(usuario));
    }

    private ResponseUsuarioDTO toDTO(Usuario usuario) {
        ResponseUsuarioDTO dto = new ResponseUsuarioDTO();
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getCorreo());
        dto.setId(usuario.getIdUsuario());
        dto.setRol(usuario.getRol());
        dto.setTelefono(usuario.getTelefono());
        dto.setInformacion(usuario.getInformacion());
        return dto;
    }
}
