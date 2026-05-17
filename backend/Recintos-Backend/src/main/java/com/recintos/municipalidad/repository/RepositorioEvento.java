package com.recintos.municipalidad.repository;

import java.util.List;
import com.recintos.municipalidad.model.Evento;
import com.recintos.municipalidad.model.Usuario;
import com.recintos.municipalidad.model.Recinto;
import com.recintos.municipalidad.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RepositorioEvento extends JpaRepository<Evento, Long> {

  List<Evento> findByCurso(Curso curso);

  List<Evento> findByCursoIsNull();

  List<Evento> findByCursoIsNullAndEstado(String estado);

  List<Evento> findByEstado(String estado);

  List<Evento> findByEncargado(Usuario encargado);

  boolean existsByRecintoAndEstado(Recinto recinto, String estado);

  @Query("SELECT COUNT(e) FROM Evento e Where estado != 'TERMINADO' and recinto.id = :recintoId")
  long countEventosActivosPorRecinto(@Param("recintoId") Long recintoId);

  @Query("SELECT COUNT(e) FROM Evento e WHERE :anio IS NULL OR YEAR(e.fechaInicio) = :anio")
  long countTotalEventos(@Param("anio") Integer anio);

  @Query("SELECT e.recinto.nombre, COUNT(e) FROM Evento e WHERE :anio IS NULL OR YEAR(e.fechaInicio) = :anio GROUP BY e.recinto.nombre")
  List<Object[]> countEventosByRecinto(@Param("anio") Integer anio);

  @Query("SELECT e.categoria.nombre, COUNT(e) FROM Evento e WHERE :anio IS NULL OR YEAR(e.fechaInicio) = :anio GROUP BY e.categoria.nombre")
  List<Object[]> countEventosByCategoria(
      @Param("anio") Integer anio);

  @Query("SELECT MONTH(e.fechaInicio), COUNT(e) FROM Evento e WHERE :anio IS NULL OR YEAR(e.fechaInicio) = :anio GROUP BY MONTH(e.fechaInicio)")
  List<Object[]> countEventosByMes(@Param("anio") Integer anio);

  @Query("SELECT YEAR(e.fechaInicio), COUNT(e) FROM Evento e GROUP BY YEAR(e.fechaInicio)")
  List<Object[]> countEventosByAnio();

  @Query("SELECT e.fechaInicio, COUNT(e) FROM Evento e WHERE :anio IS NULL OR YEAR(e.fechaInicio) = :anio GROUP BY e.fechaInicio")
  List<Object[]> countEventosByDia(@Param("anio") Integer anio);

  @Query("SELECT COUNT(DISTINCT MONTH(e.fechaInicio)) FROM Evento e WHERE :anio IS NULL OR YEAR(e.fechaInicio) = :anio")
  long countMesesConEventos(@Param("anio") Integer anio);
}
