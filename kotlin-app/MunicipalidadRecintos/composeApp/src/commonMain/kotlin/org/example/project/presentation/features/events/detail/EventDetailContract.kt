package org.example.project.presentation.features.events.detail

import org.example.project.domain.model.Event

data class EventDetailState(
        val event: Event? = null,
        val inscriptionStatus: String = "",
        val isLoading: Boolean = true,
        val showInscriptionTooltip: Boolean = false,
        val feedbackMessage: String? = null,
        val participantes: List<ParticipanteUI> = listOf(ParticipanteUI()),
        val isEnrolling: Boolean = false
)

data class ParticipanteUI(
    val nombre: String = "",
    val apellido: String = "",
    val edad: String = ""
)

sealed interface EventDetailEvent {
    data object OnBackClick : EventDetailEvent
    data class AddParticipante(val index: Int) : EventDetailEvent
    data class RemoveParticipante(val index: Int) : EventDetailEvent
    data class UpdateParticipanteNombre(val index: Int, val nombre: String) : EventDetailEvent
    data class UpdateParticipanteApellido(val index: Int, val apellido: String) : EventDetailEvent
    data class UpdateParticipanteEdad(val index: Int, val edad: String) : EventDetailEvent
    data object Enroll : EventDetailEvent
    data object ClearFeedback : EventDetailEvent
}
