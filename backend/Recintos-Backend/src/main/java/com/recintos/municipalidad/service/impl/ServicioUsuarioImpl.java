package com.recintos.municipalidad.service.impl;

import com.recintos.municipalidad.controller.dto.RegistroUsuarioDTO;
import com.recintos.municipalidad.controller.dto.UsuarioDTO;
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
    public UsuarioDTO registrarUsuario(RegistroUsuarioDTO registroDTO) {

        if (repositorioUsuario.findByEmail( registroDTO.getCorreo()).isPresent()){

            throw new RuntimeException("Correo ya existente!!!");

        }
        Usuario usuario = new Usuario();

        usuario.setNombre( registroDTO.getNombre() );
        usuario.setApellido( registroDTO.getApellido() );
        usuario.setEmail( registroDTO.getCorreo() );

        usuario.setPassword( passwordEncoder.encode(registroDTO.getContrasena()));

        usuario.setRol("PARTICIPANTE");

        Usuario usuarioGuardado = repositorioUsuario.save(usuario);

        return toDTO(usuarioGuardado);
    }

    // funcoin mapper
    private UsuarioDTO toDTO(Usuario usuario){
        UsuarioDTO usuarioDto = new UsuarioDTO();
        usuarioDto.setNombre( usuario.getNombre() );
        usuarioDto.setApellido( usuario.getApellido() );
        usuarioDto.setCorreo( usuario.getEmail() );
        usuarioDto.setId( usuario.getId() );
        usuarioDto.setRol( usuario.getRol() );

        return usuarioDto;
    }
}
