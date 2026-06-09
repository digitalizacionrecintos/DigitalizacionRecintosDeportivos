package org.example.project.data.mapper

import org.example.project.data.remote.dto.UserDTO
import org.example.project.domain.model.Encargado
import org.example.project.domain.model.UserRole

fun UserDTO.toDomain(): Encargado = Encargado(
    idUsuario = id,
    nombre = nombre,
    apellido = apellido,
    correo = correo,
    rol = rol,
    telefono = telefono,
    informacion = informacion
)

fun UserDTO.toUserRole(): UserRole = when {
    rol.uppercase() == "ROLE_ENCARGADO" -> UserRole.MANAGER
    else -> UserRole.USER
}
