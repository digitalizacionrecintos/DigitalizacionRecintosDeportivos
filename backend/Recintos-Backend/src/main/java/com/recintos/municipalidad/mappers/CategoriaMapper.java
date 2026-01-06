package com.recintos.municipalidad.mappers;

import com.recintos.municipalidad.controller.dto.CategoriaDTO;
import com.recintos.municipalidad.model.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    CategoriaDTO toDTO(Categoria categoria);

    Categoria toEntity(CategoriaDTO categoriaDTO);
}
