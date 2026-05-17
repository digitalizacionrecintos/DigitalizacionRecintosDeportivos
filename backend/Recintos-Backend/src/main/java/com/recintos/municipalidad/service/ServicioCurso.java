package com.recintos.municipalidad.service;

import com.recintos.municipalidad.model.Curso;
import java.util.List;
import java.util.Optional;

public interface ServicioCurso {
    List<Curso> listarCursos();
    List<Curso> listarCursosDisponibles();
    Optional<Curso> buscarCurso(Long id);
    Curso guardarCurso(Curso curso);
    Curso editarCurso(Long id, Curso curso);
    void eliminarCurso(Long id);
}
