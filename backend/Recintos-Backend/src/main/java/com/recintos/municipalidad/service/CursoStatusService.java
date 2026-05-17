package com.recintos.municipalidad.service;

import com.recintos.municipalidad.model.Curso;
import com.recintos.municipalidad.model.enums.EstadoCurso;
import com.recintos.municipalidad.repository.RepositorioCurso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CursoStatusService {

    @Autowired
    private RepositorioCurso repositorioCurso;

    /**
     * Calculate the automatic status for a course based on its dates
     */
    public EstadoCurso calcularEstadoAutomatico(Curso curso) {
        // Safety check
        if (curso == null) {
            return EstadoCurso.BORRADOR;
        }
        
        // If already in a terminal state, don't change
        if (curso.getEstado() == EstadoCurso.CANCELADO || 
            curso.getEstado() == EstadoCurso.FINALIZADO) {
            return curso.getEstado();
        }

        // If no dates set, keep as BORRADOR or current state
        if (curso.getFechaInicio() == null || curso.getFechaFin() == null) {
            return curso.getEstado() != null ? curso.getEstado() : EstadoCurso.BORRADOR;
        }

        LocalDate today = LocalDate.now();

        // If course has ended, mark as FINALIZADO
        if (today.isAfter(curso.getFechaFin())) {
            return EstadoCurso.FINALIZADO;
        }

        // If course has started, mark as EN_PROGRESO
        if (!today.isBefore(curso.getFechaInicio())) {
            return EstadoCurso.EN_PROGRESO;
        }

        // If course is published and hasn't started yet, keep as PUBLICADO
        if (curso.getEstado() == EstadoCurso.PUBLICADO) {
            return EstadoCurso.PUBLICADO;
        }

        // Default to current state or BORRADOR
        return curso.getEstado() != null ? curso.getEstado() : EstadoCurso.BORRADOR;
    }

    /**
     * Validate if a status transition is allowed
     */
    public boolean validarTransicion(EstadoCurso from, EstadoCurso to) {
        if (from == null) {
            return to == EstadoCurso.BORRADOR;
        }

        switch (from) {
            case BORRADOR:
                return to == EstadoCurso.PUBLICADO || to == EstadoCurso.CANCELADO;
            
            case PUBLICADO:
                return to == EstadoCurso.EN_PROGRESO || to == EstadoCurso.CANCELADO;
            
            case EN_PROGRESO:
                return to == EstadoCurso.FINALIZADO || to == EstadoCurso.CANCELADO;
            
            case FINALIZADO:
            case CANCELADO:
                return false; // Terminal states cannot transition
            
            default:
                return false;
        }
    }

    /**
     * Manually publish a course
     */
    public Curso publicarCurso(Long cursoId) {
        Curso curso = repositorioCurso.findById(cursoId)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        if (curso.getEstado() != EstadoCurso.BORRADOR) {
            throw new RuntimeException("Solo se pueden publicar cursos en estado BORRADOR");
        }

        if (curso.getFechaInicio() == null || curso.getFechaFin() == null) {
            throw new RuntimeException("El curso debe tener fechas de inicio y fin para ser publicado");
        }

        // Calculate the correct state based on current date
        java.time.LocalDate today = java.time.LocalDate.now();
        
        if (today.isAfter(curso.getFechaFin())) {
            // Course has already ended
            curso.setEstado(EstadoCurso.FINALIZADO);
        } else if (!today.isBefore(curso.getFechaInicio())) {
            // Course has started or starts today
            curso.setEstado(EstadoCurso.EN_PROGRESO);
        } else {
            // Course hasn't started yet
            curso.setEstado(EstadoCurso.PUBLICADO);
        }
        
        return repositorioCurso.save(curso);
    }

    /**
     * Manually cancel a course and all its associated events
     */
    public Curso cancelarCurso(Long cursoId) {
        Curso curso = repositorioCurso.findById(cursoId)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        if (curso.getEstado() == EstadoCurso.FINALIZADO) {
            throw new RuntimeException("No se puede cancelar un curso finalizado");
        }

        if (curso.getEstado() == EstadoCurso.CANCELADO) {
            throw new RuntimeException("El curso ya está cancelado");
        }

        // Cancel the course
        curso.setEstado(EstadoCurso.CANCELADO);
        
        // Cancel all associated events (sesiones)
        if (curso.getSesiones() != null && !curso.getSesiones().isEmpty()) {
            curso.getSesiones().forEach(evento -> {
                if (!"Cancelado".equals(evento.getEstado())) {
                    evento.setEstado("Cancelado");
                }
            });
        }
        
        return repositorioCurso.save(curso);
    }

    /**
     * Scheduled task to update course statuses daily at midnight
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void actualizarEstadosAutomaticos() {
        List<Curso> cursos = repositorioCurso.findAll();
        
        for (Curso curso : cursos) {
            EstadoCurso estadoAnterior = curso.getEstado();
            EstadoCurso estadoNuevo = calcularEstadoAutomatico(curso);
            
            if (estadoAnterior != estadoNuevo) {
                curso.setEstado(estadoNuevo);
                repositorioCurso.save(curso);
            }
        }
    }
}
