package com.recintos.municipalidad.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class UpdateAsistenciaDTO {
    private Long idEvento;
    private List<Long> ids;

}
