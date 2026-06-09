package org.example.project.domain.model

data class Event(
    val id: String,
    val title: String,
    val imagenUrl: String,
    val location: String,
    val date: String,
    val description: String,
    val organizerName: String,
    val isEnrolled: Boolean = false,
    val canEnroll: Boolean = true,
    val enrolledStatus: String = "",
    val categoria: Categoria? = null,
    val rawDate: String = "",
    val cupoMaximo: Int? = null,
    val recinto: Recinto?,
    val encargado: Encargado? = null,
)

data class Encargado(
    val idUsuario: Int,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val rol: String,
    val telefono: String? = null,
    val informacion: String? = null
)
