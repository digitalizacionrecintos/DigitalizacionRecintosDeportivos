package com.recintos.municipalidad.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String password;

    private String nombre;
    private String apellido;
    private String telefono;

    @Column(nullable = false)
    private String rol;

    private String fcmToken;

    @Column(columnDefinition = "TEXT")
    private String informacion;

    @OneToMany(mappedBy = "encargado")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Evento> eventosGestionados;

    @OneToMany(mappedBy = "usuario")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Inscripcion> inscripciones;

    @OneToMany(mappedBy = "encargadoConfirmacion")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Inscripcion> inscripcionesConfirmadas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Notificacion> notificaciones;
}
