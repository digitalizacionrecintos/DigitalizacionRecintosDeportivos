package org.example.project.data.repository

import org.example.project.core.error.AppError
import org.example.project.core.error.Try
import org.example.project.data.mapper.toEvent
import org.example.project.data.mapper.toEventDetail
import org.example.project.data.mapper.toDomain
import org.example.project.data.mapper.toManagerEvent
import org.example.project.data.remote.EventRemoteDataSource
import org.example.project.domain.model.Categoria
import org.example.project.domain.model.Event
import org.example.project.domain.model.ManagerEvent
import org.example.project.domain.model.ManagerEventDetail
import org.example.project.domain.repository.EnrollmentStatus
import org.example.project.domain.repository.EventRepository
import org.example.project.domain.repository.ParticipanteInfo

class EventRepositoryImpl(
    private val remoteDataSource: EventRemoteDataSource
) : EventRepository {

    override suspend fun getEvents(): Try<List<Event>> {
        return try {
            val dtos = remoteDataSource.getAllEvents()
            val active = dtos.filter { dto ->
                val estado = dto.estado.uppercase()
                estado != "TERMINADO" && estado != "EN_ESPERA" && estado != "FINALIZADO"
            }
            val events = active.mapIndexed { index, dto ->
                dto.toEvent(index, dto.id?.toString() ?: "temp_$index")
            }
            Try.Success(events)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar eventos", e))
        }
    }

    override suspend fun getEventDetail(eventId: Int): Try<Event> {
        return try {
            val dto = remoteDataSource.getEventById(eventId)
            val encargado = dto.encargado?.let {
                org.example.project.domain.model.Encargado(
                    idUsuario = it.idUsuario?.toInt() ?: 1,
                    nombre = it.nombre,
                    apellido = it.apellido,
                    correo = it.correo,
                    rol = it.rol
                )
            }
            Try.Success(dto.toEventDetail(encargado))
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar evento", e))
        }
    }

    override suspend fun getManagerEvents(managerId: Int): Try<List<ManagerEventDetail>> {
        return try {
            val dtos = remoteDataSource.getManagerEventsByEncargado(managerId)
            val details = dtos.map { it.toDomain() }
            Try.Success(details)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar eventos", e))
        }
    }

    override suspend fun getAllManagerEvents(): Try<List<ManagerEvent>> {
        return try {
            val dtos = remoteDataSource.getAllEvents()
            val events = dtos.map { it.toManagerEvent() }
            Try.Success(events)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar eventos", e))
        }
    }

    override suspend fun checkEnrollment(eventId: Int, userId: Int): EnrollmentStatus {
        return try {
            val status = remoteDataSource.checkEnrollment(eventId, userId)
            EnrollmentStatus(
                yaInscrito = status.inscrito,
                cupoDisponible = true,
                puedeInscribirse = !status.inscrito,
                mensaje = if (status.inscrito) "Ya estás inscrito en este evento" else ""
            )
        } catch (e: Exception) {
            EnrollmentStatus(
                yaInscrito = false,
                cupoDisponible = false,
                puedeInscribirse = false,
                mensaje = "Error al verificar inscripción: ${e.message}"
            )
        }
    }

    override suspend fun enrollUser(eventId: Int, userId: Int, participantes: List<ParticipanteInfo>): Try<String> {
        return try {
            val messages = participantes.map { p ->
                remoteDataSource.registerInscription(eventId, userId, p.nombre, p.apellido, p.edad)
            }
            Try.Success(messages.lastOrNull() ?: "Inscripción realizada con éxito")
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al inscribir", e))
        }
    }

    override suspend fun changeEventStatus(eventId: Int, status: String): Try<Unit> {
        return try {
            remoteDataSource.changeEventStatus(eventId, status)
            Try.Success(Unit)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cambiar estado", e))
        }
    }

    override suspend fun updateAttendanceBatch(eventId: Int, inscriptionIds: List<Int>): Try<Unit> {
        return try {
            remoteDataSource.updateAttendanceBatch(eventId, inscriptionIds)
            Try.Success(Unit)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al actualizar asistencia", e))
        }
    }
}
