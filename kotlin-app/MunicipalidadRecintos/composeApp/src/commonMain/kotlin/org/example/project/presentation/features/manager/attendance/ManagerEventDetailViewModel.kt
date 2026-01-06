package org.example.project.presentation.features.manager.attendance

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.remote.ApiService
import org.example.project.domain.model.ManagerEventDetail
import org.example.project.domain.model.toDomain

class ManagerEventDetailViewModel(
        private val apiService: ApiService = ApiService(),
        private val managerId: Int
) : ScreenModel {

    private val _state = MutableStateFlow<ManagerEventDetailState>(ManagerEventDetailState.Loading)
    val state: StateFlow<ManagerEventDetailState> = _state.asStateFlow()

    fun loadEventDetail(eventId: Int) {
        screenModelScope.launch {
            _state.value = ManagerEventDetailState.Loading
            try {

                val events = apiService.getManagerEventsByEncargado(managerId)

                val event = events.firstOrNull { it.idEvento == eventId }

                if (event != null) {
                    _state.value = ManagerEventDetailState.Success(event.toDomain())
                } else {
                    _state.value = ManagerEventDetailState.Error("Evento no encontrado")
                }
            } catch (e: Exception) {
                _state.value =
                        ManagerEventDetailState.Error(e.message ?: "Error al cargar el evento")
            }
        }
    }

    fun updateAttendanceStatus(inscriptionId: Int, newStatus: String) {
        screenModelScope.launch {
            try {
                val currentState = _state.value
                if (currentState is ManagerEventDetailState.Success) {
                    val updatedAttendees =
                            currentState.eventDetail.asistentes.map { attendee ->
                                if (attendee.idInscripcion == inscriptionId) {
                                    attendee.copy(estadoAsistencia = newStatus)
                                } else {
                                    attendee
                                }
                            }

                    _state.value =
                            ManagerEventDetailState.Success(
                                    currentState.eventDetail.copy(asistentes = updatedAttendees)
                            )
                }
            } catch (e: Exception) {

            }
        }
    }
    fun changeEventStatus(newStatus: String) {
        screenModelScope.launch {
            try {
                val currentEvent =
                        (_state.value as? ManagerEventDetailState.Success)?.eventDetail
                                ?: return@launch

                if (newStatus == "FINALIZADO") {
                    val presentAttendeeIds =
                            currentEvent.asistentes
                                    .filter { it.estadoAsistencia.uppercase() == "PRESENTE" }
                                    .map { it.idInscripcion }

                    apiService.updateAttendanceBatch(currentEvent.idEvento, presentAttendeeIds)
                }

                apiService.changeEventStatus(currentEvent.idEvento, newStatus)

                loadEventDetail(currentEvent.idEvento)
            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}

sealed class ManagerEventDetailState {
    data object Loading : ManagerEventDetailState()
    data class Success(val eventDetail: ManagerEventDetail) : ManagerEventDetailState()
    data class Error(val message: String) : ManagerEventDetailState()
}
