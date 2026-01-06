package com.recintos.municipalidad.service;

import com.recintos.municipalidad.controller.dto.CrearRecintoDTO;
import com.recintos.municipalidad.controller.dto.EditarRecintoDTO;
import com.recintos.municipalidad.controller.dto.ResponseRecintoDTO;
import com.recintos.municipalidad.model.Recinto;

import java.util.List;
import java.util.Optional;

public interface ServicioRecinto {
    List<Recinto> listarRecintos();

    Optional<Recinto> buscarRecinto(Long id);

    ResponseRecintoDTO guardarRecinto(CrearRecintoDTO recintoDTO);

    Recinto editarRecinto(Long id, EditarRecintoDTO editarRecintoDTO);

    Recinto eliminarRecinto(Long id);

    List<Recinto> listarRecintosDisponibles();

    Recinto cambiarEstado(Long id, String estado);
}
