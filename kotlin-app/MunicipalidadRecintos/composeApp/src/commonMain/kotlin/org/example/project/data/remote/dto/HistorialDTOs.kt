package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistorialUsuarioDTO(
    val eventos: List<HistorialInscripcionDTO> = emptyList(),
    val cursos: List<CursoHistorialDTO> = emptyList()
)

@Serializable
data class HistorialInscripcionDTO(
    val idInscripcion: Int,
    val idEvento: Int,
    @SerialName("tituloEvento") val titulo: String = "",
    @SerialName("fechaEvento") val fecha: String = "",
    @SerialName("ubicacionRecinto") val ubicacion: String? = null,
    val estadoEvento: String? = null,
    val estadoAsistencia: String? = null,
    val fechaInscripcion: String? = null,
    val participantes: List<ParticipanteSimpleDTO>? = null
)

@Serializable
data class CursoHistorialDTO(
    val nombre: String = "",
    val participantes: List<ParticipanteSimpleDTO>? = null
)

@Serializable
data class ParticipanteSimpleDTO(
    val idInscripcion: Int,
    val nombre: String? = null,
    val apellido: String? = null,
    val estadoAsistencia: String = "PENDIENTE"
)
