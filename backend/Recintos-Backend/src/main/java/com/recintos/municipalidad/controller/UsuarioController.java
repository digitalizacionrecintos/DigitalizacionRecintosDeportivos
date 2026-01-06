package com.recintos.municipalidad.controller;

import com.recintos.municipalidad.controller.dto.RegistroUsuarioDTO;
import com.recintos.municipalidad.controller.dto.UsuarioDTO;
import com.recintos.municipalidad.service.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {

    @Autowired
    private ServicioUsuario servicioUsuario;

    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> registrarUsuario(@RequestBody RegistroUsuarioDTO dto_usuario_request){

        UsuarioDTO usuarioCreado = servicioUsuario.registrarUsuario(dto_usuario_request);

        return new ResponseEntity<>(usuarioCreado, HttpStatus.CREATED);
    }
}
