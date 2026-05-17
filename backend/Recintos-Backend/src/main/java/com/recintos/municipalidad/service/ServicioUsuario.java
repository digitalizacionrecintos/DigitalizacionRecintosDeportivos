package com.recintos.municipalidad.service;

import com.recintos.municipalidad.controller.dto.HistorialUsuarioDTO;
import com.recintos.municipalidad.controller.dto.RegistroUsuarioDTO;
import com.recintos.municipalidad.controller.dto.ResponseUsuarioDTO;

public interface ServicioUsuario {
        public ResponseUsuarioDTO registrarUsuario(RegistroUsuarioDTO registroDTO);

        public ResponseUsuarioDTO login(com.recintos.municipalidad.controller.dto.LoginUsuarioDTO loginDTO);

        public ResponseUsuarioDTO obtenerPerfil(int idUsuario);

        public java.util.List<com.recintos.municipalidad.model.Usuario> listarEncargados();

        public HistorialUsuarioDTO obtenerHistorialInscripciones(Long idUsuario);

        public void guardarTokenFCM(Long idUsuario, String token);

        public ResponseUsuarioDTO registrarEncargado(RegistroUsuarioDTO registroDTO);

        public ResponseUsuarioDTO obtenerEncargado(Long id);

        public ResponseUsuarioDTO actualizarEncargado(Long id,
                        com.recintos.municipalidad.controller.dto.UpdateEncargadoDTO updateDTO);

        public boolean eliminarEncargado(Long id);

        public ResponseUsuarioDTO actualizarPerfil(Long id,
                        com.recintos.municipalidad.controller.dto.UpdateUsuarioDTO dto);
}
