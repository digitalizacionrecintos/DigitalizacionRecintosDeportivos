package org.example.project.domain.usecase.notificacion

import org.example.project.core.error.Try
import org.example.project.domain.model.Notificacion
import org.example.project.domain.repository.NotificacionRepository

class GetNotificacionesUseCase(
    private val repository: NotificacionRepository
) {
    suspend operator fun invoke(idUsuario: Int): Try<List<Notificacion>> =
        repository.getByUser(idUsuario)
}
