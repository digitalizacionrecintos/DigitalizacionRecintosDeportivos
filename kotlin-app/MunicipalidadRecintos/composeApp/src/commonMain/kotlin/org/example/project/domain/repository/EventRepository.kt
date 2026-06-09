package org.example.project.domain.repository

import org.example.project.core.error.Try
import org.example.project.domain.model.Categoria
import org.example.project.domain.model.Event
import org.example.project.domain.model.ManagerEvent
import org.example.project.domain.model.ManagerEventDetail

interface EventRepository {
    suspend fun getEvents(): Try<List<Event>>
    suspend fun getEventDetail(eventId: Int): Try<Event>
    suspend fun getManagerEvents(managerId: Int): Try<List<ManagerEventDetail>>
    suspend fun getAllManagerEvents(): Try<List<ManagerEvent>>
    suspend fun checkEnrollment(eventId: Int, userId: Int): EnrollmentStatus
    suspend fun enrollUser(eventId: Int, userId: Int, participantes: List<ParticipanteInfo>): Try<String>
    suspend fun changeEventStatus(eventId: Int, status: String): Try<Unit>
    suspend fun updateAttendanceBatch(eventId: Int, inscriptionIds: List<Int>): Try<Unit>
}

data class EnrollmentStatus(
    val yaInscrito: Boolean,
    val cupoDisponible: Boolean,
    val puedeInscribirse: Boolean,
    val mensaje: String
)
