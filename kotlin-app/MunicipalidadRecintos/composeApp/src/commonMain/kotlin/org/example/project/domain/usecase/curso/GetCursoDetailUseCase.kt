package org.example.project.domain.usecase.curso

import org.example.project.core.error.Try
import org.example.project.domain.model.Curso
import org.example.project.domain.repository.CursoRepository

class GetCursoDetailUseCase(
    private val repository: CursoRepository
) {
    suspend operator fun invoke(id: Int): Try<Curso> = repository.getCursoDetail(id)
}
