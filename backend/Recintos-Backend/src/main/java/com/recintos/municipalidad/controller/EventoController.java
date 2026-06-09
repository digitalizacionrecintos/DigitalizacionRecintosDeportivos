package com.recintos.municipalidad.controller;

import com.recintos.municipalidad.controller.dto.CrearEventoDTO;
import com.recintos.municipalidad.controller.dto.EditarEventoDTO;
import com.recintos.municipalidad.controller.dto.ResponseEventoDTO;
import com.recintos.municipalidad.model.Evento;
import com.recintos.municipalidad.service.ServicioEvento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event")
public class EventoController {

    @Autowired
    private ServicioEvento servicioEvento;

    @PostMapping("/create")
    public ResponseEntity<ResponseEventoDTO> crearEvento(@RequestBody CrearEventoDTO dto_request_event) {
        ResponseEventoDTO eventoCreado = servicioEvento.guardarEvento(dto_request_event);
        return new ResponseEntity<>(eventoCreado, HttpStatus.CREATED);
    }

    @GetMapping("/avaible")
    public ResponseEntity<List<Evento>> obtenerEventosDisponibles() {
        List<Evento> eventos = servicioEvento.listarEventosSinCurso();
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    @GetMapping("/client")
    public ResponseEntity<List<Evento>> obtenerEventosParaCliente() {
        List<Evento> eventos = servicioEvento.listarEventosParaCliente();
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Object> editarEvento(
            @PathVariable Long id,
            @RequestBody EditarEventoDTO editarEventoDTO) {
        Evento eventoEditado = servicioEvento.editarEvento(id, editarEventoDTO);
        if (eventoEditado != null) {
            return new ResponseEntity<>(eventoEditado, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarEvento(
            @PathVariable Long id) {
        Evento eventoEliminado = servicioEvento.eliminarEvento(id.intValue());
        if (eventoEliminado != null) {
            return new ResponseEntity<>(eventoEliminado, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Evento>> obtenerTodosLosEvento() {
        List<Evento> eventos = servicioEvento.listarEventos();
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenerUnEvento(
            @PathVariable Long id) {
        return servicioEvento.buscarEvento(id)
                .map(evento -> new ResponseEntity<>((Object) evento, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<Object> cambiarEstadoEvento(@PathVariable Long id, @RequestBody String estado) {
        Evento evento = servicioEvento.cambiarEstado(id, estado);
        if (evento != null) {
            return new ResponseEntity<>(evento, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/in-progress")
    public ResponseEntity<List<Evento>> obtenerEventosEnTranscurso() {
        List<Evento> eventos = servicioEvento.listarEventosEnTranscurso();
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    @GetMapping("/{id}/quota")
    public ResponseEntity<com.recintos.municipalidad.controller.dto.CupoEventoDTO> obtenerCupoEvento(
            @PathVariable Long id) {
        com.recintos.municipalidad.controller.dto.CupoEventoDTO cupoDTO = servicioEvento.obtenerCupo(id);
        return new ResponseEntity<>(cupoDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}/inscritos/count")
    public ResponseEntity<Long> obtenerCantidadInscritos(@PathVariable Long id) {
        com.recintos.municipalidad.controller.dto.CupoEventoDTO cupoDTO = servicioEvento.obtenerCupo(id);
        return new ResponseEntity<>(cupoDTO.getInscritos(), HttpStatus.OK);
    }

    @GetMapping("/manager/{id}")
    public ResponseEntity<List<com.recintos.municipalidad.controller.dto.EventoConAsistentesDTO>> listarEventosEncargado(
            @PathVariable Long id) {
        List<com.recintos.municipalidad.controller.dto.EventoConAsistentesDTO> eventos = servicioEvento
                .listarEventosPorEncargado(id);
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    @PutMapping("/{id}/assign-manager/{encargadoId}")
    public ResponseEntity<Object> asignarEncargado(@PathVariable Long id, @PathVariable Long encargadoId) {
        try {
            Evento evento = servicioEvento.asignarEncargado(id, encargadoId);
            return new ResponseEntity<>(evento, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
