package org.example.project.data.repository

import org.example.project.core.error.AppError
import org.example.project.core.error.Try
import org.example.project.data.mapper.toDomain
import org.example.project.data.remote.NotificacionRemoteDataSource
import org.example.project.data.remote.dto.FCMTokenRequest
import org.example.project.domain.model.Notificacion
import org.example.project.domain.repository.NotificacionRepository

class NotificacionRepositoryImpl(
    private val remoteDataSource: NotificacionRemoteDataSource
) : NotificacionRepository {

    override suspend fun getByUser(idUsuario: Int): Try<List<Notificacion>> {
        return try {
            Try.Success(remoteDataSource.getByUser(idUsuario).map { it.toDomain() })
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar notificaciones", e))
        }
    }

    override suspend fun markAsRead(idNotificacion: Int): Try<Unit> {
        return try {
            remoteDataSource.markAsRead(idNotificacion)
            Try.Success(Unit)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al marcar notificacion como leida", e))
        }
    }

    override suspend fun saveFCMToken(idUsuario: Int, token: String): Try<Unit> {
        return try {
            remoteDataSource.saveFCMToken(FCMTokenRequest(idUsuario, token))
            Try.Success(Unit)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al guardar token FCM", e))
        }
    }

    override suspend fun getCsrfToken(): Try<String> {
        return try {
            Try.Success(remoteDataSource.getCsrfToken().token)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al obtener token CSRF", e))
        }
    }
}
