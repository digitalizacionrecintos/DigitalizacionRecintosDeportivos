package com.recintos.municipalidad.mappers;

import com.recintos.municipalidad.controller.dto.CrearEventoDTO;
import com.recintos.municipalidad.controller.dto.ResponseEventoDTO;
import com.recintos.municipalidad.model.Evento;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { CategoriaMapper.class })
public interface EventoMapper extends IGenericMapper<Evento, ResponseEventoDTO, CrearEventoDTO> {

}
