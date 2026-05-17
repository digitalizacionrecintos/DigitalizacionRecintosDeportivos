package com.recintos.municipalidad.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "inscripcion")
public class Inscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInscripcion;

    private String nombre;

    private String apellido;
    
    private Integer edad;

    private LocalDateTime fechaHoraRegistro;
    
    private String estadoAsistencia;

    @ManyToOne
    @JoinColumn(name = "idEvento", nullable = false)
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "idTutor")
    private Usuario tutor;

    @ManyToOne
    @JoinColumn(name = "idEncargado")
    private Usuario encargadoConfirmacion;
}
