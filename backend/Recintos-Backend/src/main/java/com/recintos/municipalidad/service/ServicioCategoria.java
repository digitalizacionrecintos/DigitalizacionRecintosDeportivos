package com.recintos.municipalidad.service;

import com.recintos.municipalidad.controller.dto.CategoriaDTO;
import com.recintos.municipalidad.model.Categoria;

import java.util.List;
import java.util.Optional;

public interface ServicioCategoria {
    Categoria crearCategoria(CategoriaDTO categoriaDTO);

    Categoria editarCategoria(Long id, CategoriaDTO categoriaDTO);

    void eliminarCategoria(Long id);

    List<Categoria> listarCategorias();

    Optional<Categoria> buscarCategoria(Long id);
}
