package org.example.project.domain.usecase.curso

import org.example.project.core.error.Try
import org.example.project.domain.repository.CursoRepository
import org.example.project.domain.repository.ParticipanteInfo

class EnrollCursoUseCase(
    private val repository: CursoRepository
) {
    suspend operator fun invoke(
        cursoId: Int,
        userId: Int,
        participantes: List<ParticipanteInfo>
    ): Try<String> = repository.enrollUser(cursoId, userId, participantes)
}
