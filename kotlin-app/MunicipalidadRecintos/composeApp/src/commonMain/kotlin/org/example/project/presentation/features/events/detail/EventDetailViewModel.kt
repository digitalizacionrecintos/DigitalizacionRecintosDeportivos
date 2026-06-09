package org.example.project.presentation.features.events.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.error.Try
import org.example.project.core.error.displayMessage
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.repository.EventRepository
import org.example.project.domain.repository.ParticipanteInfo
import org.example.project.domain.usecase.event.EnrollUserUseCase
import org.example.project.domain.usecase.event.GetEventDetailUseCase

class EventDetailViewModel(
    private val getEventDetailUseCase: GetEventDetailUseCase,
    private val enrollUserUseCase: EnrollUserUseCase,
    private val eventRepository: EventRepository
) : ScreenModel {
    private val _state = MutableStateFlow(EventDetailState())
    val state = _state.asStateFlow()

    fun checkTutorialStatus() {
    }

    fun dismissTooltip() {
    }

    fun loadEvent(eventId: String) {
        val currentUser = SessionManager.getCurrentUser()
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val idInt = eventId.toIntOrNull()
            if (idInt == null || eventId.startsWith("temp_")) {
                _state.update { it.copy(isLoading = false, event = null) }
                return@launch
            }

            when (val result = getEventDetailUseCase(idInt)) {
                is Try.Success -> {
                    var event = result.value
                    if (currentUser != null) {
                        val enrollment = eventRepository.checkEnrollment(idInt, currentUser.idUsuario)
                        event = event.copy(
                            isEnrolled = !enrollment.puedeInscribirse,
                            canEnroll = enrollment.puedeInscribirse,
                            enrolledStatus = enrollment.mensaje
                        )
                    }
                    _state.update { it.copy(isLoading = false, event = event) }
                }
                is Try.Failure -> {
                    _state.update { it.copy(isLoading = false, event = null) }
                }
            }
        }
    }

    fun onEvent(event: EventDetailEvent) {
        when (event) {
            is EventDetailEvent.OnBackClick -> {}
            is EventDetailEvent.AddParticipante -> {
                val list = _state.value.participantes.toMutableList()
                list.add(ParticipanteUI())
                _state.update { it.copy(participantes = list) }
            }
            is EventDetailEvent.RemoveParticipante -> {
                val list = _state.value.participantes.toMutableList()
                if (list.size > 1) list.removeAt(event.index)
                _state.update { it.copy(participantes = list) }
            }
            is EventDetailEvent.UpdateParticipanteNombre -> {
                val list = _state.value.participantes.toMutableList()
                list[event.index] = list[event.index].copy(nombre = event.nombre)
                _state.update { it.copy(participantes = list) }
            }
            is EventDetailEvent.UpdateParticipanteApellido -> {
                val list = _state.value.participantes.toMutableList()
                list[event.index] = list[event.index].copy(apellido = event.apellido)
                _state.update { it.copy(participantes = list) }
            }
            is EventDetailEvent.UpdateParticipanteEdad -> {
                val list = _state.value.participantes.toMutableList()
                list[event.index] = list[event.index].copy(edad = event.edad)
                _state.update { it.copy(participantes = list) }
            }
            is EventDetailEvent.Enroll -> enroll()
            is EventDetailEvent.ClearFeedback -> {
                _state.update { it.copy(feedbackMessage = null) }
            }
        }
    }

    private fun enroll() {
        val event = _state.value.event ?: return
        val userId = SessionManager.getCurrentUser()?.idUsuario ?: return
        val eventId = event.id.toIntOrNull() ?: return
        val participantes = _state.value.participantes.map {
            ParticipanteInfo(nombre = it.nombre, apellido = it.apellido, edad = it.edad.toIntOrNull() ?: 0)
        }

        screenModelScope.launch {
            _state.update { it.copy(isEnrolling = true) }
            when (val result = enrollUserUseCase(eventId, userId, participantes)) {
                is Try.Success -> {
                    _state.update { it.copy(isEnrolling = false, feedbackMessage = "Inscripción exitosa") }
                    loadEvent(eventId.toString())
                }
                is Try.Failure -> {
                    _state.update { it.copy(isEnrolling = false, feedbackMessage = result.error.displayMessage()) }
                }
            }
        }
    }
}
