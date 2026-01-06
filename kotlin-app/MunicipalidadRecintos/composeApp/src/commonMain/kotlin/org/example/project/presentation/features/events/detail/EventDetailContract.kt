package org.example.project.presentation.features.events.detail

import org.example.project.domain.model.Event

data class EventDetailState(
        val event: Event? = null,
        val inscriptionStatus: String = "",
        val isLoading: Boolean = true,
        val showInscriptionTooltip: Boolean = false,
        val feedbackMessage: String? = null
)

sealed interface EventDetailEvent {
    data object OnBackClick : EventDetailEvent
    data object OnInscribeClick : EventDetailEvent
}
