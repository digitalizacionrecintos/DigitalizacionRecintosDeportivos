package com.recintos.municipalidad.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SesionId implements Serializable{
    
    private Long cursoId;
    private Long numeroSesion;
}
