package org.example.project.domain.usecase.curso

import org.example.project.domain.repository.CursoEnrollmentStatus
import org.example.project.domain.repository.CursoRepository

class CheckCursoEnrollmentUseCase(
    private val repository: CursoRepository
) {
    suspend operator fun invoke(cursoId: Int, userId: Int): CursoEnrollmentStatus =
        repository.checkEnrollment(cursoId, userId)
}
