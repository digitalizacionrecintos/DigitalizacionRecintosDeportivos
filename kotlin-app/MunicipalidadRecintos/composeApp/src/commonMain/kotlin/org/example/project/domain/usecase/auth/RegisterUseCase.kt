package org.example.project.domain.usecase.auth

import org.example.project.core.error.Try
import org.example.project.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        telefono: String
    ): Try<Unit> = repository.register(email, password, nombre, apellido, telefono)
}
