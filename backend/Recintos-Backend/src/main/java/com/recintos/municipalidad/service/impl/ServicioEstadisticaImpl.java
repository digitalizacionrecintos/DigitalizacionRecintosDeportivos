package com.recintos.municipalidad.service.impl;

import com.recintos.municipalidad.controller.dto.EstadisticasResponseDTO;
import com.recintos.municipalidad.controller.dto.EstadisticasCursosDTO;
import com.recintos.municipalidad.model.Curso;
import com.recintos.municipalidad.repository.RepositorioEvento;
import com.recintos.municipalidad.repository.RepositorioInscripcion;
import com.recintos.municipalidad.repository.RepositorioCurso;
import com.recintos.municipalidad.service.ServicioEstadistica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServicioEstadisticaImpl implements ServicioEstadistica {

        @Autowired
        private RepositorioInscripcion repositorioInscripcion;

        @Autowired
        private RepositorioEvento repositorioEvento;

        @Autowired
        private RepositorioCurso repositorioCurso;

        @Override
        public EstadisticasResponseDTO obtenerEstadisticas(Integer anio) {

                long totalEventos = repositorioEvento.countTotalEventos(anio);
                long totalInscripciones = repositorioInscripcion.countTotalInscripciones(anio);
                long asistencias = repositorioInscripcion.countInscripcionesPorAsistencia("ASISTIO", anio);
                long mesesConEventos = repositorioEvento.countMesesConEventos(anio);

                double promedioEventosMensual = mesesConEventos > 0 ? (double) totalEventos / mesesConEventos : 0;
                double porcentajeAsistencia = (totalInscripciones > 0) ? (double) asistencias / totalInscripciones * 100
                                : 0;
                double tasaAusentismo = 100 - porcentajeAsistencia;
                
                if (totalInscripciones == 0)
                        tasaAusentismo = 0;

                EstadisticasResponseDTO.ResumenStatsDTO resumen = EstadisticasResponseDTO.ResumenStatsDTO.builder()
                                .totalEventos(totalEventos)
                                .porcentajeAsistencia(Math.round(porcentajeAsistencia * 10.0) / 10.0)
                                .tasaAusentismo(Math.round(tasaAusentismo * 10.0) / 10.0)
                                .promedioEventosMensual(Math.round(promedioEventosMensual * 10.0) / 10.0)
                                .build();

                List<Object[]> categoriasRaw = repositorioEvento.countEventosByCategoria(anio);
                List<EstadisticasResponseDTO.CategoriaStatsDTO> categorias = categoriasRaw.stream()
                                .map(obj -> new EstadisticasResponseDTO.CategoriaStatsDTO((String) obj[0],
                                                (Long) obj[1]))
                                .sorted((a, b) -> Long.compare(b.getCantidad(), a.getCantidad()))
                                .collect(Collectors.toList());

                List<Object[]> recintosRaw = repositorioEvento.countEventosByRecinto(anio);
                List<EstadisticasResponseDTO.RecintoStatsDTO> recintos = recintosRaw.stream()
                                .map(obj -> new EstadisticasResponseDTO.RecintoStatsDTO((String) obj[0], (Long) obj[1]))
                                .sorted((a, b) -> Long.compare(b.getCantidad(), a.getCantidad()))
                                .collect(Collectors.toList());

                List<Object[]> porMesRaw = repositorioEvento.countEventosByMes(anio);
                Map<String, Long> porMes = new LinkedHashMap<>();

                for (Object[] obj : porMesRaw) {
                        Integer mesNum = (Integer) obj[0];
                        Long count = (Long) obj[1];
                        if (mesNum != null) {
                                String nombreMes = java.time.Month.of(mesNum).getDisplayName(TextStyle.FULL,
                                                new Locale("es", "ES"));

                                nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
                                porMes.put(nombreMes, count);
                        }
                }

                List<Object[]> porAnioRaw = repositorioEvento.countEventosByAnio();
                Map<String, Long> porAnio = porAnioRaw.stream()
                                .collect(Collectors.toMap(
                                                obj -> String.valueOf(obj[0]),
                                                obj -> (Long) obj[1]));

                List<Object[]> porDiaRaw = repositorioEvento.countEventosByDia(anio);
                Map<String, Long> porDia = porDiaRaw.stream()
                                .collect(Collectors.toMap(
                                                obj -> obj[0].toString(),
                                                obj -> (Long) obj[1]));

                EstadisticasResponseDTO.DistribucionTemporalDTO distTemporal = EstadisticasResponseDTO.DistribucionTemporalDTO
                                .builder()
                                .porMes(porMes)
                                .porAnio(porAnio)
                                .porDia(porDia)
                                .build();

                return EstadisticasResponseDTO.builder()
                                .resumen(resumen)
                                .categorias(categorias)
                                .recintos(recintos)
                                .distribucionTemporal(distTemporal)
                                .build();
        }

        @Override
        public EstadisticasCursosDTO obtenerEstadisticasCursos(Integer anio) {
                // Get all courses, filter by year if specified
                List<Curso> cursos = repositorioCurso.findAll();
                
                if (anio != null) {
                        cursos = cursos.stream()
                                .filter(c -> c.getFechaInicio() != null && 
                                           c.getFechaInicio().getYear() == anio)
                                .collect(Collectors.toList());
                }

                // Calculate summary statistics
                int totalCursos = cursos.size();
                int totalInscritos = cursos.stream()
                        .mapToInt(c -> c.getSesiones() != null ? 
                                c.getSesiones().stream()
                                        .mapToInt(e -> e.getInscripciones() != null ? e.getInscripciones().size() : 0)
                                        .sum() : 0)
                        .sum();
                
                double promedioInscritosPorCurso = totalCursos > 0 ? (double) totalInscritos / totalCursos : 0;

                EstadisticasCursosDTO.ResumenCursos resumen = new EstadisticasCursosDTO.ResumenCursos(
                        totalCursos,
                        totalInscritos,
                        Math.round(promedioInscritosPorCurso * 10.0) / 10.0
                );

                // Get top popular courses
                List<EstadisticasCursosDTO.CursoPopular> cursosPopulares = cursos.stream()
                        .map(c -> {
                                int inscritos = c.getSesiones() != null ? 
                                        c.getSesiones().stream()
                                                .mapToInt(e -> e.getInscripciones() != null ? e.getInscripciones().size() : 0)
                                                .sum() : 0;
                                int cupoMaximo = c.getCupo() != null ? c.getCupo() : 0;
                                double porcentaje = cupoMaximo > 0 ? (double) inscritos / cupoMaximo * 100 : 0;
                                String categoria = c.getCategoria() != null ? c.getCategoria().getNombre() : "Sin categoría";
                                
                                return new EstadisticasCursosDTO.CursoPopular(
                                        c.getNombre(),
                                        inscritos,
                                        cupoMaximo,
                                        Math.round(porcentaje * 10.0) / 10.0,
                                        categoria
                                );
                        })
                        .sorted((a, b) -> Integer.compare(b.getInscritos(), a.getInscritos()))
                        .limit(10)
                        .collect(Collectors.toList());

                // Calculate occupation rates
                int llenos = 0;
                int altaOcupacion = 0;
                int bajaOcupacion = 0;

                for (Curso c : cursos) {
                        int inscritos = c.getSesiones() != null ? 
                                c.getSesiones().stream()
                                        .mapToInt(e -> e.getInscripciones() != null ? e.getInscripciones().size() : 0)
                                        .sum() : 0;
                        int cupoMaximo = c.getCupo() != null ? c.getCupo() : 0;
                        
                        if (cupoMaximo > 0) {
                                double porcentaje = (double) inscritos / cupoMaximo * 100;
                                if (porcentaje >= 100) llenos++;
                                else if (porcentaje >= 75) altaOcupacion++;
                                else if (porcentaje < 50) bajaOcupacion++;
                        }
                }

                EstadisticasCursosDTO.OcupacionCursos ocupacion = new EstadisticasCursosDTO.OcupacionCursos(
                        llenos, altaOcupacion, bajaOcupacion
                );

                // Group by category
                Map<String, EstadisticasCursosDTO.CategoriaCursos> categoriaMap = new HashMap<>();
                
                for (Curso c : cursos) {
                        String catNombre = c.getCategoria() != null ? c.getCategoria().getNombre() : "Sin categoría";
                        int inscritos = c.getSesiones() != null ? 
                                c.getSesiones().stream()
                                        .mapToInt(e -> e.getInscripciones() != null ? e.getInscripciones().size() : 0)
                                        .sum() : 0;
                        
                        categoriaMap.merge(catNombre, 
                                new EstadisticasCursosDTO.CategoriaCursos(catNombre, inscritos, 1),
                                (existing, newVal) -> new EstadisticasCursosDTO.CategoriaCursos(
                                        catNombre,
                                        existing.getInscritos() + newVal.getInscritos(),
                                        existing.getCursos() + 1
                                ));
                }

                List<EstadisticasCursosDTO.CategoriaCursos> porCategoria = new ArrayList<>(categoriaMap.values());
                porCategoria.sort((a, b) -> Integer.compare(b.getInscritos(), a.getInscritos()));

                // Monthly trend
                Map<String, Integer> tendenciaMap = new LinkedHashMap<>();
                
                for (Curso c : cursos) {
                        if (c.getFechaInicio() != null) {
                                String mes = java.time.Month.of(c.getFechaInicio().getMonthValue())
                                        .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                                mes = mes.substring(0, 1).toUpperCase() + mes.substring(1);
                                
                                int inscritos = c.getSesiones() != null ? 
                                        c.getSesiones().stream()
                                                .mapToInt(e -> e.getInscripciones() != null ? e.getInscripciones().size() : 0)
                                                .sum() : 0;
                                
                                tendenciaMap.merge(mes, inscritos, Integer::sum);
                        }
                }

                List<EstadisticasCursosDTO.TendenciaMensual> tendenciaMensual = tendenciaMap.entrySet().stream()
                        .map(e -> new EstadisticasCursosDTO.TendenciaMensual(e.getKey(), e.getValue()))
                        .collect(Collectors.toList());

                return new EstadisticasCursosDTO(
                        resumen,
                        cursosPopulares,
                        ocupacion,
                        porCategoria,
                        tendenciaMensual
                );
        }
}
