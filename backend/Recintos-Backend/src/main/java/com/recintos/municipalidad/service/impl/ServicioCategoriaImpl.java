package com.recintos.municipalidad.service.impl;

import com.recintos.municipalidad.controller.dto.CategoriaDTO;
import com.recintos.municipalidad.model.Categoria;
import com.recintos.municipalidad.repository.RepositorioCategoria;
import com.recintos.municipalidad.service.ServicioCategoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioCategoriaImpl implements ServicioCategoria {

    @Autowired
    private RepositorioCategoria repositorioCategoria;

    @Override
    public Categoria crearCategoria(CategoriaDTO categoriaDTO) {
        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());
        return repositorioCategoria.save(categoria);
    }

    @Override
    public Categoria editarCategoria(Long id, CategoriaDTO categoriaDTO) {
        Optional<Categoria> categoriaOpt = repositorioCategoria.findById(id);
        if (categoriaOpt.isPresent()) {
            Categoria categoria = categoriaOpt.get();
            categoria.setNombre(categoriaDTO.getNombre());
            categoria.setDescripcion(categoriaDTO.getDescripcion());
            return repositorioCategoria.save(categoria);
        }
        return null;
    }

    @Override
    public void eliminarCategoria(Long id) {
        repositorioCategoria.deleteById(id);
    }

    @Override
    public List<Categoria> listarCategorias() {
        return repositorioCategoria.findAll();
    }

    @Override
    public Optional<Categoria> buscarCategoria(Long id) {
        return repositorioCategoria.findById(id);
    }
}
