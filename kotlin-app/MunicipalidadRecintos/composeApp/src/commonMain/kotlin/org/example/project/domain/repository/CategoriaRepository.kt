package org.example.project.domain.repository

import org.example.project.core.error.Try
import org.example.project.domain.model.Categoria

interface CategoriaRepository {
    suspend fun getAll(): Try<List<Categoria>>
    suspend fun getById(id: Int): Try<Categoria>
    suspend fun create(nombre: String, descripcion: String): Try<Categoria>
    suspend fun update(id: Int, nombre: String, descripcion: String): Try<Categoria>
    suspend fun delete(id: Int): Try<Unit>
}
