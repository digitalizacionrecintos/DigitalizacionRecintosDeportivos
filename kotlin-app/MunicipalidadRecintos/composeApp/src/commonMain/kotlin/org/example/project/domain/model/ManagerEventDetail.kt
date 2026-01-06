package org.example.project.domain.model

import org.example.project.data.remote.dto.AttendeeDTO
import org.example.project.data.remote.dto.ManagerEventWithAttendeesDTO

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

fun ManagerEventWithAttendeesDTO.toDomain(): ManagerEventDetail {
        return ManagerEventDetail(
                idEvento = this.idEvento,
                titulo = this.titulo,
                fechaInicio = this.fechaInicio,
                horaInicio = this.horaInicio,
                horaFin = this.horaFin,
                estado = this.estado,
                ubicacionRecinto = this.recinto?.nombre ?: "Sin recinto",
                categoriaName = this.categoria?.nombre ?: "Sin categoría",
                imagenUrl = this.imagenUrl?.takeIf { it.isNotBlank() } ?: this.recinto?.imagenUrl,
                asistentes = this.asistentes.map { it.toDomain() }
        )
}

fun AttendeeDTO.toDomain(): AttendeeInfo {
        return AttendeeInfo(
                idInscripcion = this.idInscripcion,
                idUsuario = this.idUsuario,
                nombreCompleto = this.nombreCompleto,
                correo = this.correo,
                estadoAsistencia = this.estadoAsistencia
        )
}
