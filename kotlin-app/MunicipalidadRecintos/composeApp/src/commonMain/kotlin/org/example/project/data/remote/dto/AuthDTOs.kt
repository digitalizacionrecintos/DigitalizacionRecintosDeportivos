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
        val yaInscrito: Boolean,
        val cupoDisponible: Boolean,
        val puedeInscribirse: Boolean,
        val mensaje: String
)

@Serializable data class InscriptionRequest(val idUsuario: Int, val idEvento: Int)
