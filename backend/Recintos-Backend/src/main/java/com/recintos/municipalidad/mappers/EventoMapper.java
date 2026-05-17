package com.recintos.municipalidad.mappers;

import com.recintos.municipalidad.controller.dto.CrearEventoDTO;
import com.recintos.municipalidad.controller.dto.ResponseEventoDTO;
import com.recintos.municipalidad.model.Evento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { CategoriaMapper.class, RecintoMapper.class, UsuarioMapper.class })
public interface EventoMapper extends IGenericMapper<Evento, ResponseEventoDTO, CrearEventoDTO> {

    @Mapping(source = "curso.idCurso", target = "cursoId")
    @Mapping(source = "curso.nombre", target = "cursoNombre")
    @Mapping(source = "recinto", target = "recinto")
    @Mapping(source = "fechaInicio", target = "fechaInicio")
    @Mapping(source = "horaInicio", target = "horaInicio")
    @Mapping(source = "horaFin", target = "horaFin")
    @Mapping(source = "inscritos", target = "inscritosActuales")
    @Mapping(source = "encargado", target = "encargado")
    @Mapping(source = "encargado.idUsuario", target = "encargadoId")
    @Mapping(source = "categoria", target = "categoria")
    @Mapping(source = "categoria.id", target = "categoriaId")
    ResponseEventoDTO toDTO(Evento evento);
    
    @org.mapstruct.AfterMapping
    default void mapCursoNombre(Evento evento, @org.mapstruct.MappingTarget ResponseEventoDTO dto) {
        if (evento.getCurso() != null) {
            dto.setCursoNombre(evento.getCurso().getNombre());
            dto.setCursoId(evento.getCurso().getIdCurso());
        }
    }
}
