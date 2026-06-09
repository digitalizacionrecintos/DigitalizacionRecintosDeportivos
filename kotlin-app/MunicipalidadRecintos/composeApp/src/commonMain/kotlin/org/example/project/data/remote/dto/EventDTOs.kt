package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
        val recinto: RecintoDTO? = null,
        val encargadoId: Int? = null,
        val categoria: CategoriaDTO? = null,
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
data class AttendeeDTO(
        val idInscripcion: Int,
        val idUsuario: Int? = null,
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
        val recinto: RecintoDTO? = null,
        val categoria: CategoriaDTO? = null,
        val asistentes: List<AttendeeDTO>,
        val imagenUrl: String? = null
)

@Serializable data class BatchAttendanceRequest(val idEvento: Int, val ids: List<Int>)
