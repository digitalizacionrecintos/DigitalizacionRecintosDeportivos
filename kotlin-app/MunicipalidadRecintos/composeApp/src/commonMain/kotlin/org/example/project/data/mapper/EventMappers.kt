package org.example.project.data.mapper

import org.example.project.data.remote.dto.AttendeeDTO
import org.example.project.data.remote.dto.CursoHistorialDTO
import org.example.project.data.remote.dto.HistorialInscripcionDTO
import org.example.project.data.remote.dto.ManagerEventDTO
import org.example.project.data.remote.dto.ManagerEventWithAttendeesDTO
import org.example.project.domain.model.AttendeeInfo
import org.example.project.domain.model.Categoria
import org.example.project.domain.model.CursoHistorial
import org.example.project.domain.model.Event
import org.example.project.domain.model.ManagerEvent
import org.example.project.domain.model.ParticipanteHistorial
import org.example.project.domain.model.ManagerEventDetail
import org.example.project.domain.model.Recinto
import org.example.project.domain.util.DateTimeUtils
import kotlinx.datetime.LocalDate

fun ManagerEventDTO.toEvent(index: Int, eventId: String): Event {
    val eventCategory = categoria?.let {
        Categoria(id = it.id, nombre = it.nombre, descripcion = it.descripcion)
    } ?: Categoria(id = 0, nombre = "General", descripcion = "Categoría por defecto")

    val recinto = recinto?.toDomain()

    return Event(
        id = id?.toString() ?: "temp_$index",
        title = titulo.ifBlank { "Sin título" },
        imagenUrl = imagenUrl?.takeIf { it.isNotBlank() } ?: recinto?.imagenUrl ?: "",
        location = recinto?.nombre ?: "Sin Recinto",
        date = DateTimeUtils.formatEventDate(fechaInicio, horaInicio),
        description = descripcion.ifBlank { "Sin descripción" },
        organizerName = "Encargado ${encargadoId ?: "?"}",
        recinto = recinto,
        categoria = eventCategory,
        cupoMaximo = cupoMaximo,
        rawDate = parseRawDate(fechaInicio)
    )
}

fun ManagerEventDTO.toEventDetail(encargado: org.example.project.domain.model.Encargado?): Event {
    val recinto = recinto?.toDomain()
    return Event(
        id = id?.toString() ?: "",
        title = titulo,
        imagenUrl = imagenUrl?.takeIf { it.isNotBlank() } ?: recinto?.imagenUrl ?: "",
        location = recinto?.nombre ?: "Sin Recinto",
        date = DateTimeUtils.formatEventDate(fechaInicio, horaInicio),
        description = descripcion,
        organizerName = encargado?.let { "${it.nombre} ${it.apellido}" } ?: "Encargado ${encargadoId ?: "?"}",
        recinto = recinto,
        encargado = encargado,
        categoria = categoria?.let {
            Categoria(id = it.id, nombre = it.nombre, descripcion = it.descripcion)
        },
        cupoMaximo = cupoMaximo
    )
}

fun ManagerEventDTO.toManagerEvent(): ManagerEvent {
    val recinto = recinto?.toDomain()
    return ManagerEvent(
        id = id?.toString() ?: "",
        title = titulo,
        date = "${DateTimeUtils.formatEventDate(fechaInicio, horaInicio)} - ${DateTimeUtils.formatTime(horaFin)}",
        location = recinto?.nombre ?: "Sin recinto",
        categoryName = categoria?.nombre ?: "General",
        imagenUrl = imagenUrl?.takeIf { it.isNotBlank() } ?: recinto?.imagenUrl
    )
}

fun ManagerEventWithAttendeesDTO.toDomain(): ManagerEventDetail {
    val recintoDomain = recinto?.toDomain()
    return ManagerEventDetail(
        idEvento = idEvento,
        titulo = titulo,
        fechaInicio = fechaInicio,
        horaInicio = horaInicio,
        horaFin = horaFin,
        estado = estado,
        ubicacionRecinto = recintoDomain?.nombre ?: "Sin recinto",
        categoriaName = categoria?.nombre ?: "Sin categoría",
        imagenUrl = imagenUrl?.takeIf { it.isNotBlank() } ?: recintoDomain?.imagenUrl,
        asistentes = asistentes.map { it.toDomain() }
    )
}

fun AttendeeDTO.toDomain(): AttendeeInfo = AttendeeInfo(
    idInscripcion = idInscripcion,
    idUsuario = idUsuario ?: 0,
    nombreCompleto = nombreCompleto,
    correo = correo,
    estadoAsistencia = estadoAsistencia
)

fun org.example.project.data.remote.dto.RecintoDTO.toDomain(): Recinto = Recinto(
    idRecinto = idRecinto?.toString(),
    nombre = nombre,
    ubicacion = ubicacion,
    descripcion = descripcion,
    imagenUrl = imagenUrl,
    coordenadasGPS = coordenadasGPS,
    capacidad = capacidad,
    estado = estado
)

fun HistorialInscripcionDTO.toEvent(): Event {
    val dateParts = fecha.split(" ", "T")
    val dateStr = dateParts.getOrElse(0) { "" }
    val timeStr = dateParts.getOrElse(1) { "" }

    return Event(
        id = idEvento.toString(),
        title = titulo,
        imagenUrl = "",
        location = ubicacion ?: "Ubicación desconocida",
        date = DateTimeUtils.formatEventDate(dateStr, timeStr),
        description = "Asistencia: ${estadoAsistencia ?: "No registrada"}",
        organizerName = "Muni Arica",
        isEnrolled = true,
        enrolledStatus = estadoEvento ?: "INSCRITO",
        recinto = null,
        rawDate = dateStr
    )
}

fun CursoHistorialDTO.toCursoHistorial(): CursoHistorial {
    val grouped = participantes?.groupBy { "${it.nombre?.trim()?.lowercase()}|${it.apellido?.trim()?.lowercase()}" }
        ?.map { (_, entries) ->
            val first = entries.first()
            ParticipanteHistorial(
                nombre = first.nombre ?: "",
                apellido = first.apellido ?: "",
                asistencias = entries.map { it.estadoAsistencia }
            )
        } ?: emptyList()
    return CursoHistorial(
        nombreCurso = nombre,
        participantes = grouped
    )
}

private fun parseRawDate(fechaInicio: String): String {
    return try {
        val datePart = when {
            fechaInicio.contains("T") -> fechaInicio.substringBefore("T")
            fechaInicio.contains(" ") -> fechaInicio.substringBefore(" ")
            else -> fechaInicio
        }
        LocalDate.parse(datePart).toString()
    } catch (e: Exception) {
        fechaInicio
    }
}
