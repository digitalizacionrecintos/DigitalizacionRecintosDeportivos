package com.recintos.municipalidad.controller;

import com.recintos.municipalidad.controller.dto.RegistroUsuarioDTO;
import com.recintos.municipalidad.controller.dto.ResponseUsuarioDTO;
import com.recintos.municipalidad.controller.dto.UpdateEncargadoDTO;
import com.recintos.municipalidad.service.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encargados")
public class EncargadoController {

    @Autowired
    private ServicioUsuario servicioUsuario;

    @PostMapping
    public ResponseEntity<Object> registrarEncargado(@RequestBody RegistroUsuarioDTO dto) {
        ResponseUsuarioDTO creado = servicioUsuario.registrarEncargado(dto);
        if (creado != null) {
            return new ResponseEntity<>(creado, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("El correo ya está registrado", HttpStatus.CONFLICT);
    }

    @GetMapping
    public ResponseEntity<List<com.recintos.municipalidad.model.Usuario>> listarEncargados() {
        return new ResponseEntity<>(servicioUsuario.listarEncargados(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenerEncargado(@PathVariable Long id) {
        ResponseUsuarioDTO encargado = servicioUsuario.obtenerEncargado(id);
        if (encargado != null) {
            return new ResponseEntity<>(encargado, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizarEncargado(@PathVariable Long id, @RequestBody UpdateEncargadoDTO dto) {
        ResponseUsuarioDTO actualizado = servicioUsuario.actualizarEncargado(id, dto);
        if (actualizado != null) {
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        }
        return new ResponseEntity<>("No se pudo actualizar. Verifique el ID o si el correo ya existe.",
                HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarEncargado(@PathVariable Long id) {
        try {
            boolean eliminado = servicioUsuario.eliminarEncargado(id);
            if (eliminado) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return new ResponseEntity<>("No se puede eliminar el encargado porque tiene eventos asignados.",
                    HttpStatus.CONFLICT);
        }
    }
}
