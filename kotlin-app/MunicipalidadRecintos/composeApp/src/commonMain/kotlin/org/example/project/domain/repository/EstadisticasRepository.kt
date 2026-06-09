package org.example.project.domain.repository

import org.example.project.core.error.Try
import org.example.project.domain.model.EstadisticasCursos
import org.example.project.domain.model.EstadisticasGenerales

interface EstadisticasRepository {
    suspend fun getGeneral(anio: Int?): Try<EstadisticasGenerales>
    suspend fun getCursos(anio: Int?): Try<EstadisticasCursos>
}
