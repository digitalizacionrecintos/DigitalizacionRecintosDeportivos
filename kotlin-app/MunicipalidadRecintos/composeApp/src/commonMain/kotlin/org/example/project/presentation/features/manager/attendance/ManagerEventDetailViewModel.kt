package org.example.project.presentation.features.manager.attendance

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.core.error.AppError
import org.example.project.core.error.Try
import org.example.project.domain.model.ManagerEventDetail
import org.example.project.domain.usecase.event.ChangeEventStatusUseCase
import org.example.project.domain.usecase.event.GetManagerEventsUseCase
import org.example.project.domain.usecase.event.UpdateAttendanceUseCase

class ManagerEventDetailViewModel(
    private val managerId: Int,
    private val getManagerEventsUseCase: GetManagerEventsUseCase,
    private val changeEventStatusUseCase: ChangeEventStatusUseCase,
    private val updateAttendanceUseCase: UpdateAttendanceUseCase
) : ScreenModel {
    private val _state = MutableStateFlow<ManagerEventDetailState>(ManagerEventDetailState.Loading)
    val state: StateFlow<ManagerEventDetailState> = _state.asStateFlow()

    fun loadEventDetail(eventId: Int) {
        screenModelScope.launch {
            _state.value = ManagerEventDetailState.Loading
            when (val result = getManagerEventsUseCase(managerId)) {
                is Try.Success -> {
                    val event = result.value.firstOrNull { it.idEvento == eventId }
                    _state.value = if (event != null) {
                        ManagerEventDetailState.Success(event)
                    } else {
                        ManagerEventDetailState.Error("Evento no encontrado")
                    }
                }
                is Try.Failure -> {
                    val msg = when (val err = result.error) {
                        is AppError.Network -> err.message
                        is AppError.Server -> err.message
                        is AppError.Unauthorized -> "Credenciales inválidas"
                        is AppError.NotFound -> "Recurso no encontrado"
                        is AppError.Unknown -> err.message
                    }
                    _state.value = ManagerEventDetailState.Error(msg)
                }
            }
        }
    }

    fun updateAttendanceStatus(inscriptionId: Int, newStatus: String) {
        screenModelScope.launch {
            val currentState = _state.value
            if (currentState is ManagerEventDetailState.Success) {
                val updatedAttendees = currentState.eventDetail.asistentes.map { attendee ->
                    if (attendee.idInscripcion == inscriptionId) {
                        attendee.copy(estadoAsistencia = newStatus)
                    } else attendee
                }
                _state.value = ManagerEventDetailState.Success(
                    currentState.eventDetail.copy(asistentes = updatedAttendees)
                )
            }
        }
    }

    fun changeEventStatus(newStatus: String) {
        screenModelScope.launch {
            val currentEvent = (_state.value as? ManagerEventDetailState.Success)?.eventDetail ?: return@launch

            if (newStatus == "FINALIZADO") {
                val presentAttendeeIds = currentEvent.asistentes
                    .filter { it.estadoAsistencia.uppercase() == "PRESENTE" }
                    .map { it.idInscripcion }

                updateAttendanceUseCase(currentEvent.idEvento, presentAttendeeIds)
            }

            when (changeEventStatusUseCase(currentEvent.idEvento, newStatus)) {
                is Try.Success -> loadEventDetail(currentEvent.idEvento)
                is Try.Failure -> {}
            }
        }
    }
}

sealed class ManagerEventDetailState {
    data object Loading : ManagerEventDetailState()
    data class Success(val eventDetail: ManagerEventDetail) : ManagerEventDetailState()
    data class Error(val message: String) : ManagerEventDetailState()
}
