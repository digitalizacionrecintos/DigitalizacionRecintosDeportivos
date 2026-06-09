package org.example.project.domain.usecase.event

import org.example.project.core.error.Try
import org.example.project.domain.model.Event
import org.example.project.domain.repository.EventRepository

class GetEventDetailUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(eventId: Int): Try<Event> = repository.getEventDetail(eventId)
}
