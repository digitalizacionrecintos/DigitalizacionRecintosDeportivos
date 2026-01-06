package com.recintos.municipalidad.controller;

import com.recintos.municipalidad.controller.dto.CategoriaDTO;
import com.recintos.municipalidad.model.Categoria;
import com.recintos.municipalidad.service.ServicioCategoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categoria")
public class CategoriaController {

    @Autowired
    private ServicioCategoria servicioCategoria;

    @PostMapping("/create")
    public ResponseEntity<Categoria> crearCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        Categoria categoria = servicioCategoria.crearCategoria(categoriaDTO);
        return new ResponseEntity<>(categoria, HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Categoria> editarCategoria(@PathVariable Long id, @RequestBody CategoriaDTO categoriaDTO) {
        Categoria categoria = servicioCategoria.editarCategoria(id, categoriaDTO);
        if (categoria != null) {
            return new ResponseEntity<>(categoria, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        servicioCategoria.eliminarCategoria(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Categoria>> listarCategorias() {
        List<Categoria> categorias = servicioCategoria.listarCategorias();
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarCategoria(@PathVariable Long id) {
        return servicioCategoria.buscarCategoria(id)
                .map(categoria -> new ResponseEntity<>(categoria, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
