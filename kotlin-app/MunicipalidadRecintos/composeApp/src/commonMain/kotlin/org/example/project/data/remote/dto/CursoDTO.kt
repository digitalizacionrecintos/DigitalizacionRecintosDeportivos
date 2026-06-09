package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CursoDTO(
    val idCurso: Int? = null,
    val nombre: String = "",
    val descripcion: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val cupo: Int? = null,
    val maximoPorInscripcion: Int? = null,
    val estado: String = "BORRADOR",
    val recinto: RecintoDTO? = null,
    val encargado: EncargadoDTO? = null,
    val categoria: CategoriaDTO? = null,
    val horarios: List<HorarioDTO> = emptyList(),
    val sesiones: List<ManagerEventDTO> = emptyList(),
    val cantidadSesiones: Int = 0
)

@Serializable
data class HorarioDTO(
    val dia: String,
    val horaInicio: String,
    val horaFin: String
)

@Serializable
data class CreateCursoRequest(
    val nombre: String,
    val descripcion: String,
    val fechaInicio: String,
    val fechaFin: String,
    val horaInicio: String,
    val horaFin: String,
    val dias: String,
    val cupo: Int,
    val maximoPorInscripcion: Int,
    val idRecinto: Int,
    val idEncargado: Int,
    val idCategoria: Int,
    val horarios: List<HorarioDTO>
)

@Serializable
data class RegisterCourseInscriptionRequest(
    val idUsuario: Int,
    val idCurso: Int,
    val listaInscripcion: List<CursoInscripcionItemDTO>
)

@Serializable
data class CursoInscripcionItemDTO(
    val nombre: String,
    @SerialName("apellidos") val apellido: String,
    val edad: Int
)

@Serializable
data class InscripcionEstadoCursoResponseDTO(
    val inscrito: Boolean,
    val sesiones: List<SesionDTO> = emptyList()
)

@Serializable
data class SesionDTO(
    val tituloEvento: String,
    val fechaInicio: String,
    val horaInicio: String,
    val horaFin: String,
    val inscripciones: List<InscripcionDTO> = emptyList()
)

@Serializable
data class InscripcionDTO(
    val idInscripcion: Int,
    val nombre: String? = null,
    val apellido: String? = null,
    val edad: Int? = null,
    val estadoAsistencia: String = "PENDIENTE"
)

@Serializable
data class CreateCategoriaRequest(
    val nombre: String,
    val descripcion: String
)

@Serializable
data class CreateRecintoRequest(
    val nombre: String,
    val ubicacion: String,
    val descripcion: String,
    val capacidad: Int,
    val coordenadasGPS: String? = null
)

@Serializable
data class UpdateRecintoRequest(
    val nombre: String,
    val ubicacion: String,
    val descripcion: String,
    val capacidad: Int,
    val coordenadasGPS: String? = null,
    val estado: String
)
