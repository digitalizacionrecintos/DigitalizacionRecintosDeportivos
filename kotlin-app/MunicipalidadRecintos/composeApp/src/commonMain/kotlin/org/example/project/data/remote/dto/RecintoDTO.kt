package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecintoDTO(
    @SerialName("idRecinto") val idRecinto: Int? = null,
    val nombre: String? = null,
    val ubicacion: String? = null,
    val descripcion: String? = null,
    @SerialName("imagenUrl") val imagenUrl: String? = null,
    val coordenadasGPS: String? = null,
    val capacidad: Int? = null,
    val estado: String? = null
)

@Serializable
data class CategoriaDTO(
    val id: Int = 0,
    val nombre: String = "",
    val descripcion: String = ""
)

@Serializable
data class EncargadoDTO(
    val id: Int? = null,
    @SerialName("idUsuario") val idUsuario: Int? = null,
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val rol: String = ""
)
