package org.example.project.domain.repository

import org.example.project.core.error.Try
import org.example.project.domain.model.Recinto

interface RecintoRepository {
    suspend fun getAll(): Try<List<Recinto>>
    suspend fun getAvailable(): Try<List<Recinto>>
    suspend fun getById(id: Int): Try<Recinto>
    suspend fun create(nombre: String, ubicacion: String, descripcion: String, capacidad: Int, coordenadasGPS: String?): Try<Recinto>
    suspend fun update(id: Int, nombre: String, ubicacion: String, descripcion: String, capacidad: Int, coordenadasGPS: String?, estado: String): Try<Recinto>
    suspend fun delete(id: Int): Try<Unit>
    suspend fun changeStatus(id: Int, status: String): Try<Recinto>
}
