package com.recintos.municipalidad.repository;

import com.recintos.municipalidad.model.Evento;
import com.recintos.municipalidad.model.Inscripcion;
import com.recintos.municipalidad.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioInscripcion extends JpaRepository<Inscripcion, Long> {
    long countByEvento(Evento evento);

    boolean existsByUsuarioAndEvento(Usuario usuario, Evento evento);

    long countByEstadoAsistencia(String estadoAsistencia);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM Inscripcion i WHERE :anio IS NULL OR YEAR(i.evento.fechaInicio) = :anio")
    long countTotalInscripciones(@org.springframework.data.repository.query.Param("anio") Integer anio);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM Inscripcion i WHERE i.estadoAsistencia = :estado AND (:anio IS NULL OR YEAR(i.evento.fechaInicio) = :anio)")
    long countInscripcionesPorAsistencia(@org.springframework.data.repository.query.Param("estado") String estado,
            @org.springframework.data.repository.query.Param("anio") Integer anio);

    java.util.List<Inscripcion> findByEvento(Evento evento);
}
