package org.example.project.domain.usecase.event

import org.example.project.core.error.Try
import org.example.project.domain.repository.EventRepository
import org.example.project.domain.repository.ParticipanteInfo

class EnrollUserUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(eventId: Int, userId: Int, participantes: List<ParticipanteInfo>): Try<String> =
        repository.enrollUser(eventId, userId, participantes)
}
