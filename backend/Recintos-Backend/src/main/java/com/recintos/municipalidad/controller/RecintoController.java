package com.recintos.municipalidad.controller;

import com.recintos.municipalidad.controller.dto.CrearRecintoDTO;
import com.recintos.municipalidad.controller.dto.EditarRecintoDTO;
import com.recintos.municipalidad.controller.dto.ResponseRecintoDTO;
import com.recintos.municipalidad.model.Recinto;
import com.recintos.municipalidad.service.ServicioRecinto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recinto")
public class RecintoController {

    @Autowired
    private ServicioRecinto servicioRecinto;

    @PostMapping("/create")
    public ResponseEntity<ResponseRecintoDTO> crearRecinto(@RequestBody CrearRecintoDTO dto) {
        ResponseRecintoDTO recintoCreado = servicioRecinto.guardarRecinto(dto);
        return new ResponseEntity<>(recintoCreado, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Recinto>> obtenerTodosLosRecintos() {
        List<Recinto> recintos = servicioRecinto.listarRecintos();
        return new ResponseEntity<>(recintos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenerUnRecinto(@PathVariable Long id) {
        return servicioRecinto.buscarRecinto(id)
                .map(recinto -> new ResponseEntity<>((Object) recinto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Object> editarRecinto(@PathVariable Long id, @RequestBody EditarRecintoDTO dto) {
        try {
            Recinto recintoEditado = servicioRecinto.editarRecinto(id, dto);
            if (recintoEditado != null) {
                return new ResponseEntity<>(recintoEditado, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarRecinto(@PathVariable Long id) {
        Recinto recintoEliminado = servicioRecinto.eliminarRecinto(id);
        if (recintoEliminado != null) {
            return new ResponseEntity<>(recintoEliminado, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Recinto>> listarRecintosDisponibles() {
        List<Recinto> recintos = servicioRecinto.listarRecintosDisponibles();
        return new ResponseEntity<>(recintos, HttpStatus.OK);
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<Object> cambiarEstadoRecinto(@PathVariable Long id, @RequestBody String estado) {
        Recinto recinto = servicioRecinto.cambiarEstado(id, estado);
        if (recinto != null) {
            return new ResponseEntity<>(recinto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
