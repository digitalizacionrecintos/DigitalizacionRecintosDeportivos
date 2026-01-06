package com.recintos.municipalidad.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "evento")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvento;

    private String titulo;

    private String descripcion;

    private String imagenUrl;

    private Integer cupoMaximo;

    private LocalDateTime horaInicio;

    private LocalDateTime horaFin;

    private LocalDate fechaInicio;

    private String estado;

    @ManyToOne
    @JoinColumn(name = "idEncargado", nullable = true)
    private Usuario encargado;

    @OneToMany(mappedBy = "evento")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Inscripcion> inscripciones;

    @ManyToOne
    @JoinColumn(name = "idRecinto")
    private Recinto recinto;

    @ManyToOne
    @JoinColumn(name = "idCategoria")
    private Categoria categoria;

    @Transient
    private Long inscritos;
}
