package com.recintos.municipalidad.controller.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import com.recintos.municipalidad.model.Recinto;

@Data
public class ResponseCursoDTO {
    private Long idCurso;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer cupo;
    private Integer maximoPorInscripcion;
    private Recinto recinto;
    private ResponseUsuarioDTO encargado;
    private CategoriaDTO categoria;
    
    private List<CursoHorarioDTO> horarios;
    private List<ResponseEventoDTO> sesiones;
    private Integer cantidadSesiones;
    private com.recintos.municipalidad.model.enums.EstadoCurso estado;
}
