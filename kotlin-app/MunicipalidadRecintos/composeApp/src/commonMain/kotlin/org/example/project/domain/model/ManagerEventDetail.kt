package org.example.project.domain.model

data class ManagerEventDetail(
        val idEvento: Int,
        val titulo: String,
        val fechaInicio: String,
        val horaInicio: String,
        val horaFin: String,
        val estado: String,
        val ubicacionRecinto: String,
        val categoriaName: String,
        val imagenUrl: String? = null,
        val asistentes: List<AttendeeInfo>
)

data class AttendeeInfo(
        val idInscripcion: Int,
        val idUsuario: Int,
        val nombreCompleto: String,
        val correo: String,
        val estadoAsistencia: String
)
