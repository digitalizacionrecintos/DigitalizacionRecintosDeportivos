package org.example.project.domain.usecase.estadisticas

import org.example.project.core.error.Try
import org.example.project.domain.model.EstadisticasGenerales
import org.example.project.domain.repository.EstadisticasRepository

class GetEstadisticasUseCase(
    private val repository: EstadisticasRepository
) {
    suspend operator fun invoke(anio: Int? = null): Try<EstadisticasGenerales> =
        repository.getGeneral(anio)
}
