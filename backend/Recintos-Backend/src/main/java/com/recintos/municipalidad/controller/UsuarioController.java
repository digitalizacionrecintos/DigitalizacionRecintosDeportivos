package com.recintos.municipalidad.controller;

import com.recintos.municipalidad.controller.dto.LoginUsuarioDTO;
import com.recintos.municipalidad.controller.dto.RegistroUsuarioDTO;
import com.recintos.municipalidad.controller.dto.ResponseUsuarioDTO;
import com.recintos.municipalidad.service.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UsuarioController {

    @Autowired
    private ServicioUsuario servicioUsuario;

    @PostMapping("/register")
    public ResponseEntity<Object> registrarUsuario(@RequestBody RegistroUsuarioDTO dto_usuario_request) {
        ResponseUsuarioDTO usuarioCreado = servicioUsuario.registrarUsuario(dto_usuario_request);
        if (usuarioCreado != null) {
            return new ResponseEntity<>(usuarioCreado, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("El correo ya está registrado", HttpStatus.CONFLICT);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> iniciarSesionUsuario(@RequestBody LoginUsuarioDTO loginDTO) {
        ResponseUsuarioDTO usuarioLogueado = servicioUsuario.login(loginDTO);
        if (usuarioLogueado != null) {
            return new ResponseEntity<>(usuarioLogueado, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/profile")
    public ResponseEntity<Object> obtenerPerfilUsuario(@RequestBody int idUsuario) {
        ResponseUsuarioDTO perfil = servicioUsuario.obtenerPerfil(idUsuario);
        if (perfil != null) {
            return new ResponseEntity<>(perfil, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    public ResponseEntity<Object> actualizarPerfil(@org.springframework.web.bind.annotation.PathVariable Long id,
            @RequestBody com.recintos.municipalidad.controller.dto.UpdateUsuarioDTO dto) {
        ResponseUsuarioDTO actualizado = servicioUsuario.actualizarPerfil(id, dto);
        if (actualizado != null) {
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        }
        return new ResponseEntity<>("No se pudo actualizar el perfil", HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.GetMapping("/managers/available")
    public ResponseEntity<java.util.List<com.recintos.municipalidad.model.Usuario>> listarEncargadosDisponibles() {
        java.util.List<com.recintos.municipalidad.model.Usuario> encargados = servicioUsuario.listarEncargados();
        return new ResponseEntity<>(encargados, HttpStatus.OK);
    }

    @org.springframework.web.bind.annotation.GetMapping("/{id}/history")
    public ResponseEntity<java.util.List<com.recintos.municipalidad.controller.dto.HistorialInscripcionDTO>> obtenerHistorial(
            @org.springframework.web.bind.annotation.PathVariable Long id) {
        java.util.List<com.recintos.municipalidad.controller.dto.HistorialInscripcionDTO> historial = servicioUsuario
                .obtenerHistorialInscripciones(id);
        return new ResponseEntity<>(historial, HttpStatus.OK);
    }
}
