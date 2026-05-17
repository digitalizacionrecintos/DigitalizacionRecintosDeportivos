package com.recintos.municipalidad.mappers;

import com.recintos.municipalidad.controller.dto.CursoHorarioDTO;
import com.recintos.municipalidad.controller.dto.ResponseCursoDTO;
import com.recintos.municipalidad.model.Curso;
import com.recintos.municipalidad.model.CursoHorario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { EventoMapper.class, UsuarioMapper.class, CategoriaMapper.class })
public interface CursoMapper {

    ResponseCursoDTO toDTO(Curso curso);

    @Mapping(target = "idCurso", ignore = true)
    @Mapping(target = "recinto.idRecinto", source = "idRecinto")
    @Mapping(target = "encargado.idUsuario", source = "idEncargado")
    @Mapping(target = "categoria.id", source = "idCategoria")
    @Mapping(target = "sesiones", ignore = true)
    Curso toEntity(com.recintos.municipalidad.controller.dto.CreateCursoDTO dto);

    @Mapping(target = "idCurso", ignore = true)
    @Mapping(target = "recinto.idRecinto", source = "idRecinto")
    @Mapping(target = "encargado.idUsuario", source = "idEncargado")
    @Mapping(target = "categoria.id", source = "idCategoria")
    @Mapping(target = "sesiones", ignore = true)
    Curso updateEntity(com.recintos.municipalidad.controller.dto.UpdateCursoDTO dto, @org.mapstruct.MappingTarget Curso curso);

    CursoHorarioDTO toHorarioDTO(CursoHorario horario);
}
