package com.recintos.municipalidad.service;

import com.recintos.municipalidad.model.Curso;
import com.recintos.municipalidad.model.CursoHorario;
import com.recintos.municipalidad.model.Evento;
import com.recintos.municipalidad.model.enums.EstadoCurso;
import com.recintos.municipalidad.repository.RepositorioCurso;
import com.recintos.municipalidad.repository.RepositorioEvento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ServicioCursoImpl implements ServicioCurso {

    @Autowired
    private RepositorioCurso repositorioCurso;

    @Autowired
    private RepositorioEvento repositorioEvento;

    @Override
    public List<Curso> listarCursos() {
        return repositorioCurso.findAll();
    }

    @Override
    public Optional<Curso> buscarCurso(Long id) {
        return repositorioCurso.findById(id);
    }

    @Override
    public Curso guardarCurso(Curso curso) {
        if (curso.getHorarios() != null) {
            curso.getHorarios().forEach(h -> h.setCurso(curso));
        }

        
        if (curso.getEstado() == null) {
            curso.setEstado(com.recintos.municipalidad.model.enums.EstadoCurso.BORRADOR);
        }

        actualizarDiasString(curso); 
        Curso cursoGuardado = repositorioCurso.save(curso);
        generarSesiones(cursoGuardado);
        return cursoGuardado;
    }

    private void generarSesiones(Curso curso) {
        if (curso.getFechaInicio() == null || curso.getFechaFin() == null) {
            return;
        }

        List<Evento> sesiones = new ArrayList<>();
        
        List<Evento> existingSessions = repositorioEvento.findByCurso(curso);

        LocalDate fechaActual = curso.getFechaInicio();

        
        int numeroSesion = existingSessions.size() + 1;
        
        
        

        

        
        if (curso.getHorarios() != null && !curso.getHorarios().isEmpty()) {
            while (!fechaActual.isAfter(curso.getFechaFin())) {
                final LocalDate currentKeyDate = fechaActual; 
                DayOfWeek currentDay = fechaActual.getDayOfWeek();

                for (CursoHorario horario : curso.getHorarios()) {
                    if (horario.getDia() == currentDay) {
                        
                        Optional<Evento> existing = existingSessions.stream()
                                .filter(e -> e.getFechaInicio().equals(currentKeyDate) &&
                                        e.getHoraInicio().toLocalTime().equals(horario.getHoraInicio()))
                                .findFirst();

                        if (existing.isPresent()) {
                            Evento e = existing.get();
                            
                            e.setCupoMaximo(curso.getCupo());
                            e.setRecinto(curso.getRecinto());
                            e.setEncargado(curso.getEncargado());
                            e.setCategoria(curso.getCategoria());
                            e.setMaximoPorInscripcion(curso.getMaximoPorInscripcion());
                            sesiones.add(e);
                        } else {
                            Evento evento = crearEventoBase(curso, currentKeyDate, numeroSesion++);
                            evento.setHoraInicio(LocalDateTime.of(currentKeyDate, horario.getHoraInicio()));
                            evento.setHoraFin(LocalDateTime.of(currentKeyDate, horario.getHoraFin()));
                            sesiones.add(evento);
                        }
                    }
                }

                fechaActual = fechaActual.plusDays(1);
            }
        }
        
        else if (curso.getDias() != null) {
            Set<DayOfWeek> diasPermitidos = Arrays.stream(curso.getDias().split(","))
                    .map(String::trim)
                    .map(this::parseDia)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toSet());

            while (!fechaActual.isAfter(curso.getFechaFin())) {
                if (diasPermitidos.contains(fechaActual.getDayOfWeek())) {
                    final LocalDate constFecha = fechaActual; 
                    Optional<Evento> existing = existingSessions.stream()
                            .filter(e -> e.getFechaInicio().equals(constFecha))
                            .findFirst();

                    if (existing.isPresent()) {
                        Evento e = existing.get();
                        e.setCupoMaximo(curso.getCupo());
                        e.setRecinto(curso.getRecinto());
                        e.setEncargado(curso.getEncargado());
                        e.setCategoria(curso.getCategoria());
                        e.setMaximoPorInscripcion(curso.getMaximoPorInscripcion());
                        sesiones.add(e);
                    } else {
                        Evento evento = crearEventoBase(curso, fechaActual, numeroSesion++);
                        if (curso.getHoraInicio() != null) {
                            evento.setHoraInicio(LocalDateTime.of(fechaActual, curso.getHoraInicio()));
                        }
                        if (curso.getHoraFin() != null) {
                            evento.setHoraFin(LocalDateTime.of(fechaActual, curso.getHoraFin()));
                        }
                        sesiones.add(evento);
                    }
                }
                fechaActual = fechaActual.plusDays(1);
            }
        }

        if (!sesiones.isEmpty()) {
            repositorioEvento.saveAll(sesiones);
        }
    }

    private Evento crearEventoBase(Curso curso, LocalDate fecha, int numeroSesion) {
        Evento evento = new Evento();
        evento.setTitulo(curso.getNombre() + " - SesiÃ³n " + numeroSesion);
        evento.setDescripcion(curso.getDescripcion());
        evento.setFechaInicio(fecha);
        evento.setCupoMaximo(curso.getCupo());
        evento.setMaximoPorInscripcion(curso.getMaximoPorInscripcion());
        evento.setRecinto(curso.getRecinto());
        evento.setEncargado(curso.getEncargado());
        evento.setCategoria(curso.getCategoria());
        evento.setEstado("DISPONIBLE");
        evento.setCurso(curso);
        evento.setInscritos(0L);
        return evento;
    }

    @Override
    public Curso editarCurso(Long id, Curso curso) {
        return repositorioCurso.findById(id).map(c -> {
            c.setNombre(curso.getNombre());
            c.setDescripcion(curso.getDescripcion());
            c.setFechaInicio(curso.getFechaInicio());
            c.setFechaFin(curso.getFechaFin());
            c.setCupo(curso.getCupo());
            c.setMaximoPorInscripcion(curso.getMaximoPorInscripcion());
            c.setRecinto(curso.getRecinto());
            c.setEncargado(curso.getEncargado());
            c.setCategoria(curso.getCategoria());

            
            if (c.getHorarios() == null) {
                c.setHorarios(new ArrayList<>());
            }
            c.getHorarios().clear();
            if (curso.getHorarios() != null) {
                curso.getHorarios().forEach(h -> {
                    h.setCurso(c);
                    c.getHorarios().add(h);
                });
            }

            
            actualizarDiasString(c);

            Curso cursoGuardado = repositorioCurso.save(c);
            generarSesiones(cursoGuardado);
            return cursoGuardado;
        }).orElse(null);
    }

    private void actualizarDiasString(Curso curso) {
        if (curso.getHorarios() != null && !curso.getHorarios().isEmpty()) {
            String diasStr = curso.getHorarios().stream()
                    .map(CursoHorario::getDia)
                    .distinct()
                    .sorted()
                    .map(this::traducirDia)
                    .collect(Collectors.joining(", "));
            curso.setDias(diasStr);
        }
    }

    private String traducirDia(DayOfWeek dia) {
        switch (dia) {
            case MONDAY:
                return "Lunes";
            case TUESDAY:
                return "Martes";
            case WEDNESDAY:
                return "MiÃ©rcoles";
            case THURSDAY:
                return "Jueves";
            case FRIDAY:
                return "Viernes";
            case SATURDAY:
                return "SÃ¡bado";
            case SUNDAY:
                return "Domingo";
            default:
                return dia.name();
        }
    }

    private DayOfWeek parseDia(String dia) {
        if (dia == null)
            return null;
        String normalized = dia.trim().toUpperCase();
        try {
            return DayOfWeek.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            
            switch (normalized) {
                case "LUNES":
                    return DayOfWeek.MONDAY;
                case "MARTES":
                    return DayOfWeek.TUESDAY;
                case "MIERCOLES":
                    return DayOfWeek.WEDNESDAY;
                case "JUEVES":
                    return DayOfWeek.THURSDAY;
                case "VIERNES":
                    return DayOfWeek.FRIDAY;
                case "SABADO":
                case "SÃBADO":
                    return DayOfWeek.SATURDAY;
                case "DOMINGO":
                    return DayOfWeek.SUNDAY;
                default:
                    return null;
            }
        }
    }

    @Override
    public void eliminarCurso(Long id) {
        repositorioCurso.deleteById(id);
    }

    @Override
    public List<Curso> listarCursosDisponibles(){
        List<Curso> cursosDisponibles = this.repositorioCurso.findByEstado(EstadoCurso.PUBLICADO);

        if (cursosDisponibles.isEmpty()){
            System.out.println("No hay cursos disponibles");
        }

        return cursosDisponibles;
    }
}
