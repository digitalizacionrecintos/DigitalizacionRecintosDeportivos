package org.example.project.domain.usecase.event

import org.example.project.core.error.Try
import org.example.project.domain.model.Event
import org.example.project.domain.repository.EventRepository

class GetEventsUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(): Try<List<Event>> = repository.getEvents()
}
