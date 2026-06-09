package org.example.project.presentation.features.cursos.detail

import org.example.project.domain.model.Curso
import org.example.project.domain.repository.CursoEnrollmentStatus

data class CursoDetailState(
    val curso: Curso? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val enrollmentStatus: CursoEnrollmentStatus? = null,
    val isCheckingEnrollment: Boolean = false,
    val isEnrolling: Boolean = false,
    val enrollmentMessage: String? = null,
    val participantes: List<ParticipanteUI> = listOf(ParticipanteUI()),
    val participantesRegistrados: List<ParticipanteUI> = emptyList()
)

data class ParticipanteUI(
    val nombre: String = "",
    val apellido: String = "",
    val edad: String = ""
)

sealed interface CursoDetailEvent {
    data class AddParticipante(val index: Int) : CursoDetailEvent
    data class RemoveParticipante(val index: Int) : CursoDetailEvent
    data class UpdateParticipanteNombre(val index: Int, val nombre: String) : CursoDetailEvent
    data class UpdateParticipanteApellido(val index: Int, val apellido: String) : CursoDetailEvent
    data class UpdateParticipanteEdad(val index: Int, val edad: String) : CursoDetailEvent
    data object Enroll : CursoDetailEvent
    data object ClearMessage : CursoDetailEvent
}
