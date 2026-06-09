package org.example.project.domain.repository

import org.example.project.core.error.Try
import org.example.project.domain.model.Encargado
import org.example.project.domain.model.UserRole

data class AuthResult(
    val user: Encargado,
    val role: UserRole
)

interface AuthRepository {
    suspend fun login(email: String, password: String): Try<AuthResult>
    suspend fun register(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        telefono: String
    ): Try<Unit>
}
