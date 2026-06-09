package org.example.project.domain.usecase.event

import org.example.project.core.error.Try
import org.example.project.domain.model.ManagerEventDetail
import org.example.project.domain.repository.EventRepository

class GetManagerEventsUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(managerId: Int): Try<List<ManagerEventDetail>> =
        repository.getManagerEvents(managerId)
}
