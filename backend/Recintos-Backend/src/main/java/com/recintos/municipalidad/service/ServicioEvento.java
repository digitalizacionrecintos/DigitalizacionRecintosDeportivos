package com.recintos.municipalidad.service;

import com.recintos.municipalidad.controller.dto.CrearEventoDTO;
import com.recintos.municipalidad.controller.dto.EditarEventoDTO;
import com.recintos.municipalidad.controller.dto.ResponseEventoDTO;
import com.recintos.municipalidad.model.Evento;
import com.recintos.municipalidad.controller.dto.EventoConAsistentesDTO;
import java.util.List;
import java.util.Optional;

public interface ServicioEvento {

    public List<Evento> listarEventos();

    public Optional<Evento> buscarEvento(Long idEvento);

    public ResponseEventoDTO guardarEvento(CrearEventoDTO evento);

    public Evento editarEvento(Long id, EditarEventoDTO editarEventoDTO);

    public Evento eliminarEvento(int IdEvento);

    public Evento cambiarEstado(Long id, String estado);

    public List<Evento> listarEventosEnTranscurso();

    public com.recintos.municipalidad.controller.dto.CupoEventoDTO obtenerCupo(Long idEvento);

    public List<EventoConAsistentesDTO> listarEventosPorEncargado(Long idEncargado);

    public Evento asignarEncargado(Long idEvento, Long idEncargado);

    public List<Evento> listarEventosDisponibles();

    public List<Evento> listarEventosSinCurso();
}
