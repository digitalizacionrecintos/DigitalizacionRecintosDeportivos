package org.example.project.domain.usecase.auth

import org.example.project.core.error.Try
import org.example.project.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        repository.login(email, password)
}
