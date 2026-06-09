package org.example.project.domain.repository

import org.example.project.core.error.Try
import org.example.project.domain.model.Notificacion

interface NotificacionRepository {
    suspend fun getByUser(idUsuario: Int): Try<List<Notificacion>>
    suspend fun markAsRead(idNotificacion: Int): Try<Unit>
    suspend fun saveFCMToken(idUsuario: Int, token: String): Try<Unit>
    suspend fun getCsrfToken(): Try<String>
}
