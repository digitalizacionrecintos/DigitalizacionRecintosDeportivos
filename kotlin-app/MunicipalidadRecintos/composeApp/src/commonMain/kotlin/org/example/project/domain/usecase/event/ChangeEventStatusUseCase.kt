package org.example.project.domain.usecase.event

import org.example.project.core.error.Try
import org.example.project.domain.repository.EventRepository

class ChangeEventStatusUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(eventId: Int, status: String): Try<Unit> =
        repository.changeEventStatus(eventId, status)
}
