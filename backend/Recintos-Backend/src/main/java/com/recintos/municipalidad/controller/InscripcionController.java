package com.recintos.municipalidad.controller;

import com.recintos.municipalidad.controller.dto.*;
import com.recintos.municipalidad.exception.SinCupoException;
import com.recintos.municipalidad.model.Inscripcion;
import com.recintos.municipalidad.service.ServicioInscripcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripcion")
public class InscripcionController {

    @Autowired
    private ServicioInscripcion servicioInscripcion;

    @PostMapping("/register")
    public ResponseEntity<Object> inscribirUsuario(@RequestBody InscripcionDTO inscripcionDTO) {
        try {
            Inscripcion inscripcion = servicioInscripcion.inscribirUsuario(
                    inscripcionDTO.getNombre(),
                    inscripcionDTO.getApellidos(),
                    inscripcionDTO.getEdad(),
                    inscripcionDTO.getIdTutor(),
                    inscripcionDTO.getIdEvento());
            return new ResponseEntity<>(inscripcion, HttpStatus.CREATED);
        } catch (SinCupoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register-batch")
    public ResponseEntity<Object> inscribirUsuariosMasivo(@RequestBody java.util.List<InscripcionDTO> inscripciones) {
        System.out.println("Received batch registration request with " + inscripciones.size() + " entries.");
        inscripciones.forEach(dto -> System.out.println("Processing: " + dto));
        try {
            var respuestas = servicioInscripcion.inscribirUsuariosMasivo(inscripciones);

            System.out.println("Registro masivo correcto" + respuestas);
            return new ResponseEntity<>(respuestas, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("Error al registrar masivamente: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<SesionDTO> verificarInscripcion(
            @RequestParam Long idEvento,
            @RequestParam(required = false) Long idUsuario) {
        SesionDTO estado = servicioInscripcion
                .verificarEstadoInscripcion(idEvento, idUsuario);
        return new ResponseEntity<>(estado, HttpStatus.OK);
    }


    ///  hacer un dto especial para este endpoint
    @GetMapping("/check-course")
    public ResponseEntity<InscripcionEstadoCursoResponseDTO> verificarInscripcionACurso(
        @RequestParam Long idCurso,
        @RequestParam Long idUsuario){
        InscripcionEstadoCursoResponseDTO estadoInscripcionDeCurso  = servicioInscripcion.verificarEstadoInscripcionACurso(idCurso,idUsuario);

        return new ResponseEntity<>(estadoInscripcionDeCurso, HttpStatus.OK);

    }

    @PutMapping("/attendance-batch")
    public ResponseEntity<String> actualizarAsistenciaMasiva(
            @RequestBody UpdateAsistenciaDTO updateDTO) {
        try {
            servicioInscripcion.actualizarAsistenciaMasiva(updateDTO.getIdEvento(), updateDTO.getIds());
            return new ResponseEntity<>("Asistencia actualizada correctamente", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/register-course")
    public ResponseEntity<String> inscribirACurso(@RequestBody InscripcionCursoMasivaDTO registerCourseBatchDTO) {
        try {
            servicioInscripcion.inscribirUsuariosACurso(registerCourseBatchDTO);
            return new ResponseEntity<>("Inscripciones a curso masivo exitosa", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    ;

}
