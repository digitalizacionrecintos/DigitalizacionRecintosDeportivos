package com.recintos.municipalidad.repository;

import com.recintos.municipalidad.model.Recinto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioRecinto extends JpaRepository<Recinto, Long> {
    java.util.List<Recinto> findByEstado(String estado);
}
