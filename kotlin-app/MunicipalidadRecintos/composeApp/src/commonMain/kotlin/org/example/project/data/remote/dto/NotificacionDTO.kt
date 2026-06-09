package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificacionDTO(
    val idNotificacion: Int,
    val titulo: String,
    val mensaje: String,
    val leida: Boolean = false,
    val fechaCreacion: String? = null,
    val tipo: String? = null,
    val idReferencia: Int? = null
)

@Serializable
data class FCMTokenRequest(
    val idUsuario: Int,
    val token: String
)

@Serializable
data class CsrfTokenResponse(
    val token: String
)

@Serializable
data class ErrorResponse(
    val message: String? = null,
    val error: String? = null
)
