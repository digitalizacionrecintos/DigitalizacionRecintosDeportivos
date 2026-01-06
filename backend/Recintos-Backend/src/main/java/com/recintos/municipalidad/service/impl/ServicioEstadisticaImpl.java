package com.recintos.municipalidad.service.impl;

import com.recintos.municipalidad.controller.dto.EstadisticasResponseDTO;
import com.recintos.municipalidad.model.Evento;
import com.recintos.municipalidad.repository.RepositorioEvento;
import com.recintos.municipalidad.repository.RepositorioInscripcion;
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
}
