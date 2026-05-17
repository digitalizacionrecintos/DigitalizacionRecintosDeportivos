package com.recintos.municipalidad.service.impl;

import com.recintos.municipalidad.controller.dto.CrearRecintoDTO;
import com.recintos.municipalidad.controller.dto.EditarRecintoDTO;
import com.recintos.municipalidad.controller.dto.ResponseRecintoDTO;
import com.recintos.municipalidad.mappers.RecintoMapper;
import com.recintos.municipalidad.model.Recinto;
import com.recintos.municipalidad.repository.RepositorioRecinto;
import com.recintos.municipalidad.service.ServicioRecinto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioRecintoImpl implements ServicioRecinto {

    @Autowired
    private RepositorioRecinto repositorioRecinto;

    @Autowired
    private com.recintos.municipalidad.repository.RepositorioEvento repositorioEvento;

    @Autowired
    private RecintoMapper mapper;

    @Override
    public List<Recinto> listarRecintos() {
        return repositorioRecinto.findAll();
    }

    @Override
    public Optional<Recinto> buscarRecinto(Long id) {
        return repositorioRecinto.findById(id);
    }

    @Override
    public ResponseRecintoDTO guardarRecinto(CrearRecintoDTO recintoDTO) {
        Recinto recinto = mapper.toEntity(recintoDTO);
        if (recintoDTO.getImagenUrl() != null) {
            recinto.setImagenUrl(recintoDTO.getImagenUrl());
        }
        recinto.setEstado("DISPONIBLE");
        Recinto recintoGuardado = repositorioRecinto.save(recinto);
        return mapper.toDTO(recintoGuardado);
    }

    @Override
    public Recinto editarRecinto(Long id, EditarRecintoDTO editarRecintoDTO) {
        Optional<Recinto> recintoOpt = repositorioRecinto.findById(id);
        if (recintoOpt.isPresent()) {
            Recinto recinto = recintoOpt.get();
            if (editarRecintoDTO.getNombre() != null)
                recinto.setNombre(editarRecintoDTO.getNombre());
            if (editarRecintoDTO.getUbicacion() != null)
                recinto.setUbicacion(editarRecintoDTO.getUbicacion());
            if (editarRecintoDTO.getDescripcion() != null)
                recinto.setDescripcion(editarRecintoDTO.getDescripcion());
            if (editarRecintoDTO.getImagenUrl() != null)
                recinto.setImagenUrl(editarRecintoDTO.getImagenUrl());
            if (editarRecintoDTO.getCapacidad() != null)
                recinto.setCapacidad(editarRecintoDTO.getCapacidad());
            if (editarRecintoDTO.getCoordenadasGPS() != null)
                recinto.setCoordenadasGPS(editarRecintoDTO.getCoordenadasGPS());
            if (editarRecintoDTO.getEstado() != null) {
                String nuevoEstado = editarRecintoDTO.getEstado();
                if (!"ACTIVO".equals(nuevoEstado) && !"INACTIVO".equals(nuevoEstado)) {
                    throw new RuntimeException("El estado solo puede ser ACTIVO o INACTIVO");
                }
                if ("INACTIVO".equals(nuevoEstado)) {

                    Long cantidad_eventos_activos = repositorioEvento.countEventosActivosPorRecinto(id);
                    
                    if (cantidad_eventos_activos > 0)
                        throw new RuntimeException(
                                "No se puede desactivar el recinto porque tiene eventos activos");
                        }
                    
                recinto.setEstado(nuevoEstado);
            }

            return repositorioRecinto.save(recinto);
        }
        return null;
    }

    @Override
    public Recinto eliminarRecinto(Long id) {
        Optional<Recinto> recintoOpt = repositorioRecinto.findById(id);
        if (recintoOpt.isPresent()) {
            repositorioRecinto.deleteById(id);
            return recintoOpt.get();
        }
        return null;
    }

    @Override
    public List<Recinto> listarRecintosDisponibles() {
        return repositorioRecinto.findByEstado("ACTIVO");
    }

    @Override
    public Recinto cambiarEstado(Long id, String estado) {
        Optional<Recinto> recintoOpt = repositorioRecinto.findById(id);
        if (recintoOpt.isPresent()) {
            Recinto recinto = recintoOpt.get();
            recinto.setEstado(estado);
            return repositorioRecinto.save(recinto);
        }
        return null;
    }
}
