package com.recintos.municipalidad.mappers;

import com.recintos.municipalidad.controller.dto.CrearRecintoDTO;
import com.recintos.municipalidad.controller.dto.ResponseRecintoDTO;
import com.recintos.municipalidad.model.Recinto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecintoMapper extends IGenericMapper<Recinto, ResponseRecintoDTO, CrearRecintoDTO> {
}
