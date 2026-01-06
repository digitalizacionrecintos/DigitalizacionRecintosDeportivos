package org.example.project.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Recinto(
        val idRecinto: String? = null,
        val nombre: String? = null,
        val ubicacion: String? = null,
        val descripcion: String? = null,
        @SerialName("imagenUrl") val imagenUrl: String? = null,
        val coordenadasGPS: String? = null
)

@Serializable data class Categoria(val id: Int, val nombre: String, val descripcion: String)
