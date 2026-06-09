package org.example.project.domain.usecase.curso

import org.example.project.core.error.Try
import org.example.project.domain.model.Curso
import org.example.project.domain.repository.CursoRepository

class GetAvailableCursosUseCase(
    private val repository: CursoRepository
) {
    suspend operator fun invoke(): Try<List<Curso>> = repository.getAvailable()
}
