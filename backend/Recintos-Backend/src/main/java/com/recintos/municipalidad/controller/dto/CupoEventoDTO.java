package com.recintos.municipalidad.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CupoEventoDTO {
    private Long idEvento;
    private Integer cupoMaximo;
    private Long inscritos;
    private Long disponibles;
}
