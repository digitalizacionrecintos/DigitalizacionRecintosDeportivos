package com.recintos.municipalidad.controller;

import com.recintos.municipalidad.controller.dto.LoginUsuarioDTO;
import com.recintos.municipalidad.controller.dto.RegistroUsuarioDTO;
import com.recintos.municipalidad.controller.dto.ResponseUsuarioDTO;
import com.recintos.municipalidad.service.ServicioUsuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UsuarioController {

    @Autowired
    private ServicioUsuario servicioUsuario;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @PostMapping("/register")
    public ResponseEntity<Object> registrarUsuario(@RequestBody RegistroUsuarioDTO dto_usuario_request) {
        ResponseUsuarioDTO usuarioCreado = servicioUsuario.registrarUsuario(dto_usuario_request);
        if (usuarioCreado != null) {
            return new ResponseEntity<>(usuarioCreado, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("El correo ya está registrado", HttpStatus.CONFLICT);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> iniciarSesionUsuario(@RequestBody LoginUsuarioDTO loginDTO, HttpServletResponse response) {
        ResponseUsuarioDTO usuarioLogueado = servicioUsuario.login(loginDTO);
        if (usuarioLogueado != null) {
            String token = Jwts.builder()
                    .subject(usuarioLogueado.getId().toString())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L))
                    .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .compact();

            Cookie authCookie = new Cookie("auth_token", token);
            authCookie.setHttpOnly(true);
            authCookie.setSecure(false);
            authCookie.setPath("/");
            authCookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(authCookie);

            Cookie sessionCookie = new Cookie("session_active", "true");
            sessionCookie.setHttpOnly(true);
            sessionCookie.setSecure(false);
            sessionCookie.setPath("/");
            sessionCookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(sessionCookie);

            return new ResponseEntity<>(usuarioLogueado, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> cerrarSesion(HttpServletResponse response) {
        Cookie authCookie = new Cookie("auth_token", "");
        authCookie.setHttpOnly(true);
        authCookie.setSecure(false);
        authCookie.setPath("/");
        authCookie.setMaxAge(0);
        response.addCookie(authCookie);

        Cookie sessionCookie = new Cookie("session_active", "");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(false);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(0);
        response.addCookie(sessionCookie);

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/profile")
    public ResponseEntity<Object> obtenerPerfilUsuario(@RequestBody int idUsuario) {
        ResponseUsuarioDTO perfil = servicioUsuario.obtenerPerfil(idUsuario);
        if (perfil != null) {
            return new ResponseEntity<>(perfil, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizarPerfil(@PathVariable Long id, @RequestBody com.recintos.municipalidad.controller.dto.UpdateUsuarioDTO dto) {
        ResponseUsuarioDTO actualizado = servicioUsuario.actualizarPerfil(id, dto);
        if (actualizado != null) {
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        }
        return new ResponseEntity<>("No se pudo actualizar el perfil", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/managers/available")
    public ResponseEntity<java.util.List<com.recintos.municipalidad.model.Usuario>> listarEncargadosDisponibles() {
        java.util.List<com.recintos.municipalidad.model.Usuario> encargados = servicioUsuario.listarEncargados();
        return new ResponseEntity<>(encargados, HttpStatus.OK);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<com.recintos.municipalidad.controller.dto.HistorialUsuarioDTO> obtenerHistorial(@PathVariable Long id) {
        com.recintos.municipalidad.controller.dto.HistorialUsuarioDTO historial = servicioUsuario.obtenerHistorialInscripciones(id);
        return new ResponseEntity<>(historial, HttpStatus.OK);
    }
}
