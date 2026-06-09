package org.example.project.domain.repository

import org.example.project.core.error.Try
import org.example.project.domain.model.Encargado

interface EncargadoRepository {
    suspend fun getAll(): Try<List<Encargado>>
    suspend fun getById(id: Int): Try<Encargado>
    suspend fun create(correo: String, contrasena: String, nombre: String, apellido: String, telefono: String): Try<Encargado>
    suspend fun update(id: Int, nombre: String, apellido: String, telefono: String, correo: String): Try<Encargado>
    suspend fun delete(id: Int): Try<Unit>
    suspend fun getAvailableManagers(): Try<List<Encargado>>
}
