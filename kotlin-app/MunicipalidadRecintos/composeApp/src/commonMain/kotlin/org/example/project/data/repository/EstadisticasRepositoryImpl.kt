package org.example.project.data.repository

import org.example.project.core.error.AppError
import org.example.project.core.error.Try
import org.example.project.data.mapper.toDomain
import org.example.project.data.remote.EstadisticasRemoteDataSource
import org.example.project.domain.model.EstadisticasCursos
import org.example.project.domain.model.EstadisticasGenerales
import org.example.project.domain.repository.EstadisticasRepository

class EstadisticasRepositoryImpl(
    private val remoteDataSource: EstadisticasRemoteDataSource
) : EstadisticasRepository {

    override suspend fun getGeneral(anio: Int?): Try<EstadisticasGenerales> {
        return try {
            Try.Success(remoteDataSource.getGeneral(anio).toDomain())
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar estadisticas", e))
        }
    }

    override suspend fun getCursos(anio: Int?): Try<EstadisticasCursos> {
        return try {
            Try.Success(remoteDataSource.getCursos(anio).toDomain())
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar estadisticas de cursos", e))
        }
    }
}
