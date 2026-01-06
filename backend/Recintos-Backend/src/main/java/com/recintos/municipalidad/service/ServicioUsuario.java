package com.recintos.municipalidad.service;

import com.recintos.municipalidad.controller.dto.RegistroUsuarioDTO;
import com.recintos.municipalidad.controller.dto.UsuarioDTO;

public interface ServicioUsuario {
    public UsuarioDTO registrarUsuario(RegistroUsuarioDTO registroDTO);
}
