package org.example.project.presentation.features.notificaciones

import org.example.project.domain.model.Notificacion

data class NotificacionesState(
    val notificaciones: List<Notificacion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface NotificacionesEvent {
    data class MarkAsRead(val idNotificacion: Int) : NotificacionesEvent
}
