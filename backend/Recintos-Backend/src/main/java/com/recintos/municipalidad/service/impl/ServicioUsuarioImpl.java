package com.recintos.municipalidad.service.impl;

import com.recintos.municipalidad.controller.dto.RegistroUsuarioDTO;
import com.recintos.municipalidad.controller.dto.ResponseUsuarioDTO;
import com.recintos.municipalidad.model.Usuario;
import com.recintos.municipalidad.repository.RepositorioUsuario;
import com.recintos.municipalidad.service.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ServicioUsuarioImpl implements ServicioUsuario {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseUsuarioDTO registrarUsuario(RegistroUsuarioDTO registroDTO) {

        if (repositorioUsuario.findByCorreo(registroDTO.getCorreo()).isPresent())
            return null;

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
    public ResponseUsuarioDTO login(com.recintos.municipalidad.controller.dto.LoginUsuarioDTO loginDTO) {
        java.util.Optional<Usuario> usuarioOpt = repositorioUsuario.findByCorreo(loginDTO.getCorreo());
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
        java.util.Optional<Usuario> usuarioOpt = repositorioUsuario.findById((long) idUsuario);
        return usuarioOpt.map(this::toDTO).orElse(null);
    }

    private ResponseUsuarioDTO toDTO(Usuario usuario) {
        ResponseUsuarioDTO responseUsuarioDto = new ResponseUsuarioDTO();
        responseUsuarioDto.setNombre(usuario.getNombre());
        responseUsuarioDto.setApellido(usuario.getApellido());
        responseUsuarioDto.setCorreo(usuario.getCorreo());
        responseUsuarioDto.setId(usuario.getIdUsuario());
        responseUsuarioDto.setRol(usuario.getRol());
        responseUsuarioDto.setTelefono(usuario.getTelefono());
        responseUsuarioDto.setInformacion(usuario.getInformacion());

        return responseUsuarioDto;
    }

    @Override
    public java.util.List<Usuario> listarEncargados() {
        return repositorioUsuario.findByRol("ROLE_ENCARGADO");
    }

    @Override
    public java.util.List<com.recintos.municipalidad.controller.dto.HistorialInscripcionDTO> obtenerHistorialInscripciones(
            Long idUsuario) {
        Usuario usuario = repositorioUsuario.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getInscripciones() == null) {
            return new java.util.ArrayList<>();
        }

        return usuario.getInscripciones().stream().map(inscripcion -> {
            com.recintos.municipalidad.model.Evento evento = inscripcion.getEvento();
            return new com.recintos.municipalidad.controller.dto.HistorialInscripcionDTO(
                    inscripcion.getIdInscripcion(),
                    evento.getIdEvento(),
                    evento.getTitulo(),
                    evento.getHoraInicio(),
                    evento.getRecinto() != null ? evento.getRecinto().getUbicacion() : "Sin ubicación",
                    evento.getEstado(),
                    inscripcion.getEstadoAsistencia(),
                    inscripcion.getFechaHoraRegistro());
        }).collect(java.util.stream.Collectors.toList());
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
    public ResponseUsuarioDTO actualizarEncargado(Long id,
            com.recintos.municipalidad.controller.dto.UpdateEncargadoDTO updateDTO) {
        java.util.Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (!"ROLE_ENCARGADO".equals(usuario.getRol())) {
                return null;
            }

            if (updateDTO.getNombre() != null)
                usuario.setNombre(updateDTO.getNombre());
            if (updateDTO.getApellido() != null)
                usuario.setApellido(updateDTO.getApellido());
            if (updateDTO.getTelefono() != null)
                usuario.setTelefono(updateDTO.getTelefono());
            if (updateDTO.getCorreo() != null) {

                java.util.Optional<Usuario> existing = repositorioUsuario.findByCorreo(updateDTO.getCorreo());
                if (existing.isPresent() && !existing.get().getIdUsuario().equals(id)) {
                    return null;
                }
                usuario.setCorreo(updateDTO.getCorreo());
            }

            Usuario updated = repositorioUsuario.save(usuario);
            return toDTO(updated);
        }
        return null;
    }

    @Override
    public boolean eliminarEncargado(Long id) {
        java.util.Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
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
    public ResponseUsuarioDTO actualizarPerfil(Long id,
            com.recintos.municipalidad.controller.dto.UpdateUsuarioDTO dto) {
        java.util.Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (dto.getNombre() != null)
                usuario.setNombre(dto.getNombre());
            if (dto.getApellido() != null)
                usuario.setApellido(dto.getApellido());
            if (dto.getTelefono() != null)
                usuario.setTelefono(dto.getTelefono());
            if (dto.getInformacion() != null)
                usuario.setInformacion(dto.getInformacion());
            if (dto.getCorreo() != null) {
                java.util.Optional<Usuario> existing = repositorioUsuario.findByCorreo(dto.getCorreo());
                if (existing.isPresent() && !existing.get().getIdUsuario().equals(id)) {
                    return null;
                }
                usuario.setCorreo(dto.getCorreo());
            }
            Usuario updated = repositorioUsuario.save(usuario);
            return toDTO(updated);
        }
        return null;
    }
}
