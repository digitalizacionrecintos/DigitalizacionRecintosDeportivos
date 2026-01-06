package com.recintos.municipalidad.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "recinto")
public class Recinto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecinto;

    private String nombre;
    private String ubicacion;
    private String descripcion;
    private String imagenUrl;
    private Integer capacidad;
    private String coordenadasGPS;
    private String estado;

    @OneToMany(mappedBy = "recinto")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Evento> eventos;
}
