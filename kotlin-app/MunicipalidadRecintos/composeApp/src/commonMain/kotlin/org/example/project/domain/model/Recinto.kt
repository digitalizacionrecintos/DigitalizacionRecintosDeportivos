package org.example.project.domain.model

data class Recinto(
    val idRecinto: String? = null,
    val nombre: String? = null,
    val ubicacion: String? = null,
    val descripcion: String? = null,
    val imagenUrl: String? = null,
    val coordenadasGPS: String? = null,
    val capacidad: Int? = null,
    val estado: String? = null
)

data class Categoria(
    val id: Int,
    val nombre: String,
    val descripcion: String
)
