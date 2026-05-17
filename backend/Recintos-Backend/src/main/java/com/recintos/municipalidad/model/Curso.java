package com.recintos.municipalidad.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.recintos.municipalidad.model.enums.EstadoCurso;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table (name = "curso")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCurso;

    private String nombre;

    private String descripcion;

    private java.time.LocalDate fechaInicio;
    
    private java.time.LocalDate fechaFin;
    
    private java.time.LocalTime horaInicio;
    
    private java.time.LocalTime horaFin;

    private String dias;

    private Integer cupo;

    private Integer maximoPorInscripcion;

    @ManyToOne
    @JoinColumn(name = "idRecinto")
    private Recinto recinto;

    @ManyToOne
    @JoinColumn(name = "idEncargado")
    private Usuario encargado;

    @ManyToOne
    @JoinColumn(name = "idCategoria")
    private Categoria categoria;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    @OrderBy("fechaInicio ASC, horaInicio ASC")
    @JsonIgnore
    private List<Evento> sesiones;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CursoHorario> horarios;

    @Enumerated(EnumType.STRING)
    private EstadoCurso estado;

    public int getCantidadSesiones() {
        return sesiones != null ? sesiones.size() : 0;
    }
}
