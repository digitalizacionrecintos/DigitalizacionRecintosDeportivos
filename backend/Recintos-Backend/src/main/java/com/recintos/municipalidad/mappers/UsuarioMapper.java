package com.recintos.municipalidad.mappers;

import com.recintos.municipalidad.controller.dto.ResponseUsuarioDTO;
import com.recintos.municipalidad.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "idUsuario", target = "id")
    ResponseUsuarioDTO toDTO(Usuario usuario);
}
