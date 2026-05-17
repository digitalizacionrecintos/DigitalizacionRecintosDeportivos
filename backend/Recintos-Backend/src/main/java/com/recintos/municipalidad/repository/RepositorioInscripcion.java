package com.recintos.municipalidad.repository;

import com.recintos.municipalidad.model.Evento;
import com.recintos.municipalidad.model.Inscripcion;
import com.recintos.municipalidad.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositorioInscripcion extends JpaRepository<Inscripcion, Long> {
    long countByEvento(Evento evento);

    long countByEstadoAsistencia(String estadoAsistencia);

    @Query("SELECT COUNT(i) FROM Inscripcion i WHERE :anio IS NULL OR YEAR(i.evento.fechaInicio) = :anio")
    long countTotalInscripciones(@Param("anio") Integer anio);

    @Query("SELECT COUNT(i) FROM Inscripcion i WHERE i.estadoAsistencia = :estado AND (:anio IS NULL OR YEAR(i.evento.fechaInicio) = :anio)")
    long countInscripcionesPorAsistencia(@Param("estado") String estado,
                                         @Param("anio") Integer anio);

    List<Inscripcion> findByEvento(Evento evento);

    List<Inscripcion> findByTutor(Usuario tutor);

    long countByEventoAndTutor(Evento evento, Usuario tutor);

    List<Inscripcion> findByEventoAndTutor(Evento evento, Usuario tutor);

}
