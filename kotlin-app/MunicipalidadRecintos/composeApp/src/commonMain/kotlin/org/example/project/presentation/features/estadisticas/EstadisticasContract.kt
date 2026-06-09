package org.example.project.presentation.features.estadisticas

import org.example.project.domain.model.EstadisticasGenerales
import org.example.project.domain.model.EstadisticasCursos

data class EstadisticasState(
    val generales: EstadisticasGenerales? = null,
    val cursos: EstadisticasCursos? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedYear: Int? = null
)

sealed interface EstadisticasEvent {
    data class SelectYear(val year: Int?) : EstadisticasEvent
}
