package org.example.project.presentation.features.events.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.remote.ApiService
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.model.Event

class EventDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow(EventDetailState())
    val state = _state.asStateFlow()

    private val apiService = ApiService()

    fun checkTutorialStatus() {

    }

    fun dismissTooltip() {

    }

    fun loadEvent(eventId: String) {
        val currentUser = SessionManager.getCurrentUser()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {

                if (eventId.startsWith("temp_")) {
                    _state.update { it.copy(isLoading = false, event = null) }
                    return@launch
                }

                val idInt = eventId.toIntOrNull()
                if (idInt == null) {
                    _state.update { it.copy(isLoading = false) }
                    return@launch
                }

                val dto = apiService.getEventById(idInt)
                println("\n=== DEBUG: Event DTO Received ===")
                println("Event ID: ${dto.id}")
                println("Title: ${dto.titulo}")
                println("Description: ${dto.descripcion}")
                println("Cupo Máximo: ${dto.cupoMaximo}")
                println("Recinto: ${dto.recinto}")
                println("Encargado: ${dto.encargado}")
                println("Categoría: ${dto.categoria}")
                println("================================\n")

                if (dto.titulo.isBlank()) {}
                if (dto.descripcion.isBlank()) {}

                println("\n=== DEBUG: Checking Enrollment Status ===")
                println("Event ID: $idInt")
                println("User ID: ${currentUser?.id ?: 0}")
                val isEnrolled = apiService.checkEnrollment(idInt, currentUser?.id ?: 0)
                println("\n=== DEBUG: Enrollment Response ===")
                println("yaInscrito: ${isEnrolled.yaInscrito}")
                println("cupoDisponible: ${isEnrolled.cupoDisponible}")
                println("puedeInscribirse: ${isEnrolled.puedeInscribirse}")
                println("mensaje: ${isEnrolled.mensaje}")
                println("================================\n")

                val event =
                        Event(
                                id = dto.id?.toString()
                                                ?: eventId,
                                title = dto.titulo,
                                imagenUrl = dto.imagenUrl?.takeIf { it.isNotBlank() }
                                                ?: dto.recinto?.imagenUrl ?: "",
                                location = dto.recinto?.nombre ?: "Sin Recinto",
                                date =
                                        org.example.project.domain.util.DateTimeUtils
                                                .formatEventDate(dto.fechaInicio, dto.horaInicio),
                                description = dto.descripcion,
                                organizerName = "Encargado ${dto.encargadoId ?: "?"}",
                                isEnrolled = !isEnrolled.puedeInscribirse,
                                canEnroll =
                                        isEnrolled.puedeInscribirse,
                                enrolledStatus = isEnrolled.mensaje,
                                recinto = dto.recinto,
                                encargado = dto.encargado,
                                categoria = dto.categoria,
                                cupoMaximo = dto.cupoMaximo
                        )

                println("\n=== DEBUG: Event Model Created ===")
                println("Event ID: ${event.id}")
                println("Title: ${event.title}")
                println("isEnrolled: ${event.isEnrolled} (mapped from !puedeInscribirse)")
                println("canEnroll: ${event.canEnroll} (mapped from puedeInscribirse)")
                println("enrolledStatus: ${event.enrolledStatus} (mensaje from backend)")
                println("================================\n")

                _state.update { it.copy(isLoading = false, event = event) }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(isLoading = false, event = null) }
            }
        }
    }

    fun inscribeUser() {
        val currentUser = SessionManager.getCurrentUser()
        val eventId = _state.value.event?.id?.toIntOrNull()

        if (currentUser == null || eventId == null) {
            _state.update { it.copy(feedbackMessage = "Error: Usuario o evento no válido") }
            return
        }

        viewModelScope.launch {

            println("\n=== DEBUG: Starting Inscription ===")
            println("User ID: ${currentUser.id}")
            println("Event ID: $eventId")
            println("================================\n")

            try {
                val message = apiService.registerInscription(eventId, currentUser.id)
                println("\n=== DEBUG: Inscription Response ===")
                println("Success Message: $message")
                println("================================\n")

                _state.update { it.copy(feedbackMessage = message) }

                println("\n=== DEBUG: Reloading Event After Inscription ===")
                loadEvent(eventId.toString())
            } catch (e: Exception) {
                println("\n=== DEBUG: Inscription Error ===")
                println("Error Type: ${e::class.simpleName}")
                println("Error Message: ${e.message}")
                println("================================\n")

                _state.update { it.copy(feedbackMessage = "Error: ${e.message}") }
            }
        }
    }

    fun clearFeedback() {
        _state.update { it.copy(feedbackMessage = null) }
    }
}
