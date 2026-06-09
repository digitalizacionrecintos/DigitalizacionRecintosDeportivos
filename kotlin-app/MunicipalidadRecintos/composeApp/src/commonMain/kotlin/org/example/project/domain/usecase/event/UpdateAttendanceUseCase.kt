package org.example.project.domain.usecase.event

import org.example.project.core.error.Try
import org.example.project.domain.repository.EventRepository

class UpdateAttendanceUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(eventId: Int, inscriptionIds: List<Int>): Try<Unit> =
        repository.updateAttendanceBatch(eventId, inscriptionIds)
}
