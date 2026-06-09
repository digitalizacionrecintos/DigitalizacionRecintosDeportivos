package org.example.project.presentation.features.notificaciones

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.error.Try
import org.example.project.core.error.displayMessage
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.usecase.notificacion.GetNotificacionesUseCase
import org.example.project.domain.usecase.notificacion.MarkNotificacionReadUseCase

class NotificacionesViewModel(
    private val getNotificacionesUseCase: GetNotificacionesUseCase,
    private val markNotificacionReadUseCase: MarkNotificacionReadUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(NotificacionesState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun onEvent(event: NotificacionesEvent) {
        when (event) {
            is NotificacionesEvent.MarkAsRead -> {
                screenModelScope.launch {
                    markNotificacionReadUseCase(event.idNotificacion)
                    load()
                }
            }
        }
    }

    private fun load() {
        val userId = SessionManager.getCurrentUser()?.idUsuario ?: return
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getNotificacionesUseCase(userId)) {
                is Try.Success -> {
                    _state.update { it.copy(notificaciones = result.value, isLoading = false) }
                }
                is Try.Failure -> {
                    _state.update { it.copy(isLoading = false, error = result.error.displayMessage()) }
                }
            }
        }
    }
}
