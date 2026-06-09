package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable data class LoginRequest(val correo: String, val contrasena: String)

@Serializable
data class RegisterRequest(
        val correo: String,
        val contrasena: String,
        val nombre: String,
        val apellido: String,
        val telefono: String
)

@Serializable
data class UserDTO(
        val id: Int,
        val nombre: String,
        val apellido: String,
        val correo: String,
        val telefono: String? = null,
        val informacion: String? = null,
        val rol: String
)

@Serializable
data class UpdateUserRequest(
        val nombre: String,
        val apellido: String,
        val correo: String,
        val telefono: String,
        val informacion: String
)

@Serializable
data class InscriptionStatus(
        val inscrito: Boolean,
        val tituloEvento: String = "",
        @kotlinx.serialization.SerialName("fechaInicio")
        val fecha: String = "",
        val horaInicio: String = "",
        val horaFin: String = "",
        val inscripciones: List<InscripcionEventoDTO> = emptyList()
)

@Serializable
data class InscripcionEventoDTO(
        val idInscripcion: Int,
        val nombre: String? = null,
        val apellido: String? = null,
        val apellidos: String? = null,
        val edad: Int? = null,
        val estadoAsistencia: String = "PENDIENTE"
)

@Serializable data class InscriptionRequest(
    val idTutor: Int,
    val idEvento: Int,
    val nombre: String = "",
    @kotlinx.serialization.SerialName("apellidos") val apellido: String = "",
    val edad: Int = 0
)
