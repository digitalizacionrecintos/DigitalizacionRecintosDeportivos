package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.project.domain.model.Categoria
import org.example.project.domain.model.EncargadoDTO
import org.example.project.domain.model.Recinto

@Serializable
data class ManagerEventDTO(
        @SerialName("idEvento") val id: Int? = null,
        val titulo: String = "",
        val descripcion: String = "",
        val horaInicio: String = "",
        val horaFin: String = "",
        val fechaInicio: String = "",
        val fechaFin: String? = null,
        val cupoMaximo: Int? = null,
        val publicoObjetivo: String? = null,
        val estado: String = "PENDIENTE",
        val recinto: Recinto? = null,
        val encargadoId: Int? = null,
        val categoria: Categoria? = null,
        val encargado: EncargadoDTO? = null,
        val imagenUrl: String? = null
)

@Serializable
data class CreateEventDTO(
        val titulo: String,
        val descripcion: String,
        val horaInicio: String,
        val horaFin: String,
        val fechaInicio: String,
        val categoriaId: Int,
        val cupoMaximo: Int,
        val publicoObjetivo: String,
        val recintoId: Int,
        val encargadoId: Int
)

@Serializable
data class UserHistoryDTO(
        val idInscripcion: Int,
        val idEvento: Int,
        @SerialName("tituloEvento") val titulo: String,
        @SerialName("fechaEvento") val fecha: String,
        @SerialName("ubicacionRecintop")
        val ubicacion: String? = null,
        val estadoEvento: String? = null,
        val estadoAsistencia: String? = null,
        val fechaInscripcion: String? = null
)

@Serializable
data class AttendeeDTO(
        val idInscripcion: Int,
        val idUsuario: Int,
        val nombreCompleto: String,
        val correo: String,
        val estadoAsistencia: String
)

@Serializable
data class ManagerEventWithAttendeesDTO(
        val idEvento: Int,
        val titulo: String,
        val fechaInicio: String,
        val horaInicio: String,
        val horaFin: String,
        val estado: String,
        val ubicacionRecinto: String,
        val recinto: Recinto? = null,
        val categoria: Categoria? = null,
        val asistentes: List<AttendeeDTO>,
        val imagenUrl: String? = null
)

@Serializable data class BatchAttendanceRequest(val idEvento: Int, val ids: List<Int>)
