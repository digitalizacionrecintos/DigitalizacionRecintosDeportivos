package org.example.project.data.repository

import org.example.project.core.error.AppError
import org.example.project.core.error.Try
import org.example.project.data.mapper.toCursoHistorial
import org.example.project.data.mapper.toEvent
import org.example.project.data.remote.AuthRemoteDataSource
import org.example.project.data.remote.UserRemoteDataSource
import org.example.project.data.remote.dto.UpdateUserRequest
import org.example.project.domain.model.CursoHistorial
import org.example.project.domain.model.Encargado
import org.example.project.domain.model.Event
import org.example.project.domain.repository.UserRepository

class UserRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun getUserEventHistory(userId: Int): Try<List<Event>> {
        return try {
            val response = userRemoteDataSource.getUserHistory(userId)
            println("=== DEBUG UserRepository eventos count: ${response.eventos.size} ===")
            val events = response.eventos.map { it.toEvent() }
            Try.Success(events)
        } catch (e: Exception) {
            println("=== DEBUG UserRepository getUserEventHistory ERROR: ${e.message} ===")
            e.printStackTrace()
            Try.Failure(AppError.Network(e.message ?: "Error al cargar historial de eventos", e))
        }
    }

    override suspend fun getUserCourseHistory(userId: Int): Try<List<CursoHistorial>> {
        return try {
            val response = userRemoteDataSource.getUserHistory(userId)
            println("=== DEBUG UserRepository cursos count: ${response.cursos.size} ===")
            val cursos = response.cursos.map { it.toCursoHistorial() }
            Try.Success(cursos)
        } catch (e: Exception) {
            println("=== DEBUG UserRepository getUserCourseHistory ERROR: ${e.message} ===")
            e.printStackTrace()
            Try.Failure(AppError.Network(e.message ?: "Error al cargar historial de cursos", e))
        }
    }

    override suspend fun updateProfile(
        userId: Int,
        nombre: String,
        apellido: String,
        correo: String,
        telefono: String,
        informacion: String
    ): Try<Encargado> {
        return try {
            val dto = authRemoteDataSource.updateUser(
                userId,
                UpdateUserRequest(nombre, apellido, correo, telefono, informacion)
            )
            Try.Success(
                Encargado(
                    idUsuario = dto.id,
                    nombre = dto.nombre,
                    apellido = dto.apellido,
                    correo = dto.correo,
                    rol = dto.rol
                )
            )
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al actualizar perfil", e))
        }
    }
}
