package com.recintos.municipalidad.controller;

import com.recintos.municipalidad.controller.dto.InscripcionDTO;
import com.recintos.municipalidad.exception.SinCupoException;
import com.recintos.municipalidad.exception.UsuarioYaInscritoException;
import com.recintos.municipalidad.model.Inscripcion;
import com.recintos.municipalidad.service.ServicioInscripcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inscripcion")
public class InscripcionController {

    @Autowired
    private ServicioInscripcion servicioInscripcion;

    @PostMapping("/register")
    public ResponseEntity<Object> inscribirUsuario(@RequestBody InscripcionDTO inscripcionDTO) {
        try {
            Inscripcion inscripcion = servicioInscripcion.inscribirUsuario(
                    inscripcionDTO.getIdUsuario(),
                    inscripcionDTO.getIdEvento());
            return new ResponseEntity<>(inscripcion, HttpStatus.CREATED);
        } catch (com.recintos.municipalidad.exception.UsuarioYaInscritoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (com.recintos.municipalidad.exception.SinCupoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @org.springframework.web.bind.annotation.GetMapping("/check")
    public ResponseEntity<com.recintos.municipalidad.controller.dto.EstadoInscripcionDTO> verificarInscripcion(
            @org.springframework.web.bind.annotation.RequestParam Long idUsuario,
            @org.springframework.web.bind.annotation.RequestParam Long idEvento) {
        com.recintos.municipalidad.controller.dto.EstadoInscripcionDTO estado = servicioInscripcion
                .verificarEstadoInscripcion(idUsuario, idEvento);
        return new ResponseEntity<>(estado, HttpStatus.OK);
    }

    @org.springframework.web.bind.annotation.PutMapping("/attendance-batch")
    public ResponseEntity<String> actualizarAsistenciaMasiva(
            @RequestBody com.recintos.municipalidad.controller.dto.UpdateAsistenciaDTO updateDTO) {
        try {
            servicioInscripcion.actualizarAsistenciaMasiva(updateDTO.getIdEvento(), updateDTO.getIds());
            return new ResponseEntity<>("Asistencia actualizada correctamente", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
