package com.recintos.municipalidad.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

public interface IGenericMapper<E,D,I> {

    D toDTO(E entity);

    E toEntity(I requestDTO);

    List<D> toDtoList(List<E> entityList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(I requestDTO, @MappingTarget E entity);

}
