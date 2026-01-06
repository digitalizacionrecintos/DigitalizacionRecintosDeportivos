package com.recintos.municipalidad.service.impl;

import com.recintos.municipalidad.controller.dto.EstadisticasResponseDTO;
import com.recintos.municipalidad.model.Categoria;
import com.recintos.municipalidad.model.Evento;
import com.recintos.municipalidad.model.Inscripcion;
import com.recintos.municipalidad.model.Recinto;
import com.recintos.municipalidad.repository.RepositorioEvento;
import com.recintos.municipalidad.repository.RepositorioInscripcion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ServicioEstadisticaImplTest {

    @Mock
    private RepositorioEvento repositorioEvento;

    @Mock
    private RepositorioInscripcion repositorioInscripcion;

    @InjectMocks
    private ServicioEstadisticaImpl servicioEstadistica;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void obtenerEstadisticas_ShouldReturnCorrectData() {

        Categoria catDeportes = new Categoria();
        catDeportes.setNombre("Deportes");
        Categoria catCultura = new Categoria();
        catCultura.setNombre("Cultura");

        Recinto recEstadio = new Recinto();
        recEstadio.setNombre("Estadio");
        Recinto recTeatro = new Recinto();
        recTeatro.setNombre("Teatro");

        Evento e1 = new Evento();
        e1.setCategoria(catDeportes);
        e1.setRecinto(recEstadio);
        e1.setFechaInicio(LocalDate.of(2024, 1, 15));

        Inscripcion i1 = new Inscripcion();
        i1.setEstadoAsistencia("ASISTIO");
        Inscripcion i2 = new Inscripcion();
        i2.setEstadoAsistencia("AUSENTE");
        e1.setInscripciones(Arrays.asList(i1, i2));

        Evento e2 = new Evento();
        e2.setCategoria(catCultura);
        e2.setRecinto(recTeatro);
        e2.setFechaInicio(LocalDate.of(2024, 2, 20));
        e2.setInscripciones(Collections.emptyList());

        Evento e3 = new Evento();
        e3.setCategoria(catDeportes);
        e3.setRecinto(recEstadio);
        e3.setFechaInicio(LocalDate.of(2023, 12, 10));

        when(repositorioEvento.findAll()).thenReturn(Arrays.asList(e1, e2, e3));

        EstadisticasResponseDTO result = servicioEstadistica.obtenerEstadisticas(null);

        assertNotNull(result);
        assertEquals(3, result.getResumen().getTotalEventos());

        assertEquals(2, result.getCategorias().size());
        assertEquals("Deportes", result.getCategorias().get(0).getNombre());
        assertEquals(2, result.getCategorias().get(0).getCantidad());

        assertEquals(2, result.getRecintos().size());
        assertEquals("Estadio", result.getRecintos().get(0).getNombre());
        assertEquals(2, result.getRecintos().get(0).getCantidad());

        assertTrue(result.getDistribucionTemporal().getPorAnio().containsKey("2024"));
        assertEquals(2, result.getDistribucionTemporal().getPorAnio().get("2024"));
        assertEquals(1, result.getDistribucionTemporal().getPorAnio().get("2023"));
    }

    @Test
    public void obtenerEstadisticas_WithYearFilter_ShouldFilterCorrectly() {

        Evento e1 = new Evento();
        e1.setFechaInicio(LocalDate.of(2024, 1, 1));
        Evento e2 = new Evento();
        e2.setFechaInicio(LocalDate.of(2023, 1, 1));

        when(repositorioEvento.findAll()).thenReturn(Arrays.asList(e1, e2));

        EstadisticasResponseDTO result = servicioEstadistica.obtenerEstadisticas(2024);

        assertEquals(1, result.getResumen().getTotalEventos());
        assertEquals(1, result.getDistribucionTemporal().getPorAnio().get("2024"));
        assertNull(result.getDistribucionTemporal().getPorAnio().get("2023"));
    }
}
