package org.example.project.domain.usecase.user

import org.example.project.core.error.Try
import org.example.project.domain.model.Encargado
import org.example.project.domain.repository.UserRepository

class UpdateProfileUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        userId: Int,
        nombre: String,
        apellido: String,
        correo: String,
        telefono: String,
        informacion: String
    ): Try<Encargado> = repository.updateProfile(
        userId, nombre, apellido, correo, telefono, informacion
    )
}
