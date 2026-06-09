package org.example.project.data.repository

import org.example.project.core.error.AppError
import org.example.project.core.error.Try
import org.example.project.data.mapper.toDomain
import org.example.project.data.mapper.toUserRole
import org.example.project.data.remote.AuthRemoteDataSource
import org.example.project.data.remote.dto.LoginRequest
import org.example.project.data.remote.dto.RegisterRequest
import org.example.project.domain.model.Encargado
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.repository.AuthResult

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Try<AuthResult> {
        return try {
            val dto = remoteDataSource.login(LoginRequest(email, password))
            Try.Success(
                AuthResult(
                    user = dto.toDomain(),
                    role = dto.toUserRole()
                )
            )
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error de conexión", e))
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        telefono: String
    ): Try<Unit> {
        return try {
            remoteDataSource.register(RegisterRequest(email, password, nombre, apellido, telefono))
            Try.Success(Unit)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al registrar", e))
        }
    }
}
