package org.example.project.data.repository

import org.example.project.core.error.AppError
import org.example.project.core.error.Try
import org.example.project.data.mapper.toDomain
import org.example.project.data.remote.EncargadoRemoteDataSource
import org.example.project.data.remote.dto.RegisterRequest
import org.example.project.data.remote.dto.UpdateUserRequest
import org.example.project.domain.model.Encargado
import org.example.project.domain.repository.EncargadoRepository

class EncargadoRepositoryImpl(
    private val remoteDataSource: EncargadoRemoteDataSource
) : EncargadoRepository {

    override suspend fun getAll(): Try<List<Encargado>> {
        return try {
            Try.Success(remoteDataSource.getAll().map { it.toDomain() })
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar encargados", e))
        }
    }

    override suspend fun getById(id: Int): Try<Encargado> {
        return try {
            Try.Success(remoteDataSource.getById(id).toDomain())
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar encargado", e))
        }
    }

    override suspend fun create(
        correo: String, contrasena: String, nombre: String, apellido: String, telefono: String
    ): Try<Encargado> {
        return try {
            Try.Success(remoteDataSource.create(RegisterRequest(correo, contrasena, nombre, apellido, telefono)).toDomain())
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al crear encargado", e))
        }
    }

    override suspend fun update(
        id: Int, nombre: String, apellido: String, telefono: String, correo: String
    ): Try<Encargado> {
        return try {
            Try.Success(remoteDataSource.update(id, UpdateUserRequest(nombre, apellido, correo, telefono, "")).toDomain())
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al actualizar encargado", e))
        }
    }

    override suspend fun delete(id: Int): Try<Unit> {
        return try {
            remoteDataSource.delete(id)
            Try.Success(Unit)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al eliminar encargado", e))
        }
    }

    override suspend fun getAvailableManagers(): Try<List<Encargado>> {
        return try {
            Try.Success(remoteDataSource.getAvailableManagers().map { it.toDomain() })
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar encargados disponibles", e))
        }
    }
}
