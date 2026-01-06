package com.recintos.municipalidad.repository;

import com.recintos.municipalidad.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositorioEvento extends JpaRepository<Evento, Long> {

  java.util.List<Evento> findByEstado(String estado);

  java.util.List<Evento> findByEncargado(com.recintos.municipalidad.model.Usuario encargado);

  boolean existsByRecintoAndEstado(com.recintos.municipalidad.model.Recinto recinto, String estado);

  @org.springframework.data.jpa.repository.Query("SELECT COUNT(e) FROM Evento e WHERE :anio IS NULL OR YEAR(e.fechaInicio) = :anio")
  long countTotalEventos(@org.springframework.data.repository.query.Param("anio") Integer anio);

  @org.springframework.data.jpa.repository.Query("SELECT e.recinto.nombre, COUNT(e) FROM Evento e WHERE :anio IS NULL OR YEAR(e.fechaInicio) = :anio GROUP BY e.recinto.nombre")
  java.util.List<Object[]> countEventosByRecinto(@org.springframework.data.repository.query.Param("anio") Integer anio);

  @org.springframework.data.jpa.repository.Query("SELECT e.categoria.nombre, COUNT(e) FROM Evento e WHERE :anio IS NULL OR YEAR(e.fechaInicio) = :anio GROUP BY e.categoria.nombre")
  java.util.List<Object[]> countEventosByCategoria(
      @org.springframework.data.repository.query.Param("anio") Integer anio);

  @org.springframework.data.jpa.repository.Query("SELECT MONTH(e.fechaInicio), COUNT(e) FROM Evento e WHERE :anio IS NULL OR YEAR(e.fechaInicio) = :anio GROUP BY MONTH(e.fechaInicio)")
  java.util.List<Object[]> countEventosByMes(@org.springframework.data.repository.query.Param("anio") Integer anio);

  @org.springframework.data.jpa.repository.Query("SELECT YEAR(e.fechaInicio), COUNT(e) FROM Evento e GROUP BY YEAR(e.fechaInicio)")
  java.util.List<Object[]> countEventosByAnio();

  @org.springframework.data.jpa.repository.Query("SELECT e.fechaInicio, COUNT(e) FROM Evento e WHERE :anio IS NULL OR YEAR(e.fechaInicio) = :anio GROUP BY e.fechaInicio")
  java.util.List<Object[]> countEventosByDia(@org.springframework.data.repository.query.Param("anio") Integer anio);

  @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT MONTH(e.fechaInicio)) FROM Evento e WHERE :anio IS NULL OR YEAR(e.fechaInicio) = :anio")
  long countMesesConEventos(@org.springframework.data.repository.query.Param("anio") Integer anio);
}
