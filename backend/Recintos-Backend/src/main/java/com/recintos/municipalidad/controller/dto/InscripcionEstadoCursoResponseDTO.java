package com.recintos.municipalidad.controller.dto;


import com.google.type.Date;
import lombok.Data;

import java.util.List;

@Data
public class InscripcionEstadoCursoResponseDTO {

    private boolean inscrito;
    private List<SesionDTO> sesiones;

}
