package com.recintos.municipalidad.controller;

import com.google.api.Http;
import com.recintos.municipalidad.model.Curso;
import com.recintos.municipalidad.service.ServicioCurso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.recintos.municipalidad.controller.dto.CreateCursoDTO;
import com.recintos.municipalidad.controller.dto.UpdateCursoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.recintos.municipalidad.controller.dto.ResponseCursoDTO;
import com.recintos.municipalidad.mappers.CursoMapper;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/curso")
public class CursoController {

    private final ServicioCurso servicioCurso;
    private final CursoMapper cursoMapper;
    private final com.recintos.municipalidad.service.CursoStatusService cursoStatusService;
    private final com.recintos.municipalidad.repository.RepositorioRecinto repositorioRecinto;
    private final com.recintos.municipalidad.repository.RepositorioUsuario repositorioUsuario;
    private final com.recintos.municipalidad.repository.RepositorioCategoria repositorioCategoria;

    @Autowired
    public CursoController(ServicioCurso servicioCurso, CursoMapper cursoMapper, 
                          com.recintos.municipalidad.service.CursoStatusService cursoStatusService,
                          com.recintos.municipalidad.repository.RepositorioRecinto repositorioRecinto,
                          com.recintos.municipalidad.repository.RepositorioUsuario repositorioUsuario,
                          com.recintos.municipalidad.repository.RepositorioCategoria repositorioCategoria) {
        this.servicioCurso = servicioCurso;
        this.cursoMapper = cursoMapper;
        this.cursoStatusService = cursoStatusService;
        this.repositorioRecinto = repositorioRecinto;
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioCategoria = repositorioCategoria;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResponseCursoDTO>> obtenerTodos() {
        List<Curso> cursos = servicioCurso.listarCursos();
        List<ResponseCursoDTO> dtos = cursos.stream()
                .map(cursoMapper::toDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseCursoDTO> crear(@RequestBody CreateCursoDTO cursoDTO) {
        Curso curso = new Curso();
        curso.setNombre(cursoDTO.getNombre());
        curso.setDescripcion(cursoDTO.getDescripcion());
        curso.setFechaInicio(cursoDTO.getFechaInicio());
        curso.setFechaFin(cursoDTO.getFechaFin());
        curso.setHoraInicio(cursoDTO.getHoraInicio());
        curso.setHoraFin(cursoDTO.getHoraFin());
        curso.setDias(cursoDTO.getDias());
        curso.setCupo(cursoDTO.getCupo());
        curso.setMaximoPorInscripcion(cursoDTO.getMaximoPorInscripcion());
        
        
        if (cursoDTO.getIdRecinto() != null) {
            curso.setRecinto(repositorioRecinto.findById(cursoDTO.getIdRecinto())
                .orElseThrow(() -> new RuntimeException("Recinto no encontrado")));
        }
        if (cursoDTO.getIdEncargado() != null) {
            curso.setEncargado(repositorioUsuario.findById(cursoDTO.getIdEncargado())
                .orElseThrow(() -> new RuntimeException("Encargado no encontrado")));
        }
        if (cursoDTO.getIdCategoria() != null) {
            curso.setCategoria(repositorioCategoria.findById(cursoDTO.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("CategorÃ­a no encontrada")));
        }
        
        
        if (cursoDTO.getHorarios() != null) {
            curso.setHorarios(new java.util.ArrayList<>());
            for (var horarioDTO : cursoDTO.getHorarios()) {
                com.recintos.municipalidad.model.CursoHorario horario = new com.recintos.municipalidad.model.CursoHorario();
                horario.setDia(horarioDTO.getDia());
                horario.setHoraInicio(horarioDTO.getHoraInicio());
                horario.setHoraFin(horarioDTO.getHoraFin());
                horario.setCurso(curso);
                curso.getHorarios().add(horario);
            }
        }
        
        Curso guardado = servicioCurso.guardarCurso(curso);
        return new ResponseEntity<>(cursoMapper.toDTO(guardado), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseCursoDTO> obtenerUno(@PathVariable Long id) {
        return servicioCurso.buscarCurso(id)
                .map(cursoMapper::toDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<ResponseCursoDTO> editar(@PathVariable Long id, @RequestBody UpdateCursoDTO cursoDTO) {
        return servicioCurso.buscarCurso(id)
                .map(cursoExistente -> {
                    cursoMapper.updateEntity(cursoDTO, cursoExistente);
                    Curso actualizado = servicioCurso.editarCurso(id, cursoExistente);
                    return new ResponseEntity<>(cursoMapper.toDTO(actualizado), HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicioCurso.eliminarCurso(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/publicar")
    public ResponseEntity<ResponseCursoDTO> publicar(@PathVariable Long id) {
        try {
            Curso curso = cursoStatusService.publicarCurso(id);
            return new ResponseEntity<>(cursoMapper.toDTO(curso), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<ResponseCursoDTO> cancelar(@PathVariable Long id) {
        try {
            Curso curso = cursoStatusService.cancelarCurso(id);
            return new ResponseEntity<>(cursoMapper.toDTO(curso), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/avaible")
    public ResponseEntity<List<Curso>> obtenerCursosDisponibles(){
        try {
            List<Curso> cursosDisponibles = this.servicioCurso.listarCursosDisponibles();
            return new ResponseEntity<>(cursosDisponibles, HttpStatus.OK) ;
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
}
