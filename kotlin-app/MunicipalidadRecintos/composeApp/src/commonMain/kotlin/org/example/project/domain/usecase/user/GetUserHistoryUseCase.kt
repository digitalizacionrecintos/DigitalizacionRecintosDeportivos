package org.example.project.domain.usecase.user

import org.example.project.core.error.Try
import org.example.project.domain.model.Event
import org.example.project.domain.repository.UserRepository

class GetUserHistoryUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: Int): Try<List<Event>> =
        repository.getUserEventHistory(userId)
}
