package org.example.project.data.repository

import org.example.project.core.error.AppError
import org.example.project.core.error.Try
import org.example.project.data.mapper.toDomain
import org.example.project.data.remote.RecintoRemoteDataSource
import org.example.project.data.remote.dto.CreateRecintoRequest
import org.example.project.data.remote.dto.UpdateRecintoRequest
import org.example.project.domain.model.Recinto
import org.example.project.domain.repository.RecintoRepository

class RecintoRepositoryImpl(
    private val remoteDataSource: RecintoRemoteDataSource
) : RecintoRepository {

    override suspend fun getAll(): Try<List<Recinto>> {
        return try {
            Try.Success(remoteDataSource.getAll().map { it.toDomain() })
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar recintos", e))
        }
    }

    override suspend fun getAvailable(): Try<List<Recinto>> {
        return try {
            Try.Success(remoteDataSource.getAvailable().map { it.toDomain() })
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar recintos disponibles", e))
        }
    }

    override suspend fun getById(id: Int): Try<Recinto> {
        return try {
            Try.Success(remoteDataSource.getById(id).toDomain())
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar recinto", e))
        }
    }

    override suspend fun create(
        nombre: String, ubicacion: String, descripcion: String, capacidad: Int, coordenadasGPS: String?
    ): Try<Recinto> {
        return try {
            val dto = remoteDataSource.create(CreateRecintoRequest(nombre, ubicacion, descripcion, capacidad, coordenadasGPS))
            Try.Success(dto.toDomain())
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al crear recinto", e))
        }
    }

    override suspend fun update(
        id: Int, nombre: String, ubicacion: String, descripcion: String, capacidad: Int, coordenadasGPS: String?, estado: String
    ): Try<Recinto> {
        return try {
            val dto = remoteDataSource.update(id, UpdateRecintoRequest(nombre, ubicacion, descripcion, capacidad, coordenadasGPS, estado))
            Try.Success(dto.toDomain())
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al actualizar recinto", e))
        }
    }

    override suspend fun delete(id: Int): Try<Unit> {
        return try {
            remoteDataSource.delete(id)
            Try.Success(Unit)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al eliminar recinto", e))
        }
    }

    override suspend fun changeStatus(id: Int, status: String): Try<Recinto> {
        return try {
            Try.Success(remoteDataSource.changeStatus(id, status).toDomain())
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cambiar estado", e))
        }
    }
}
