package org.example.project.domain.usecase.notificacion

import org.example.project.core.error.Try
import org.example.project.domain.repository.NotificacionRepository

class MarkNotificacionReadUseCase(
    private val repository: NotificacionRepository
) {
    suspend operator fun invoke(idNotificacion: Int): Try<Unit> =
        repository.markAsRead(idNotificacion)
}
