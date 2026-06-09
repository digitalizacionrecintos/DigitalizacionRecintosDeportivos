package org.example.project.data.repository

import org.example.project.core.error.AppError
import org.example.project.core.error.Try
import org.example.project.data.remote.CategoriaRemoteDataSource
import org.example.project.domain.model.Categoria
import org.example.project.domain.repository.CategoriaRepository

class CategoriaRepositoryImpl(
    private val remoteDataSource: CategoriaRemoteDataSource
) : CategoriaRepository {

    override suspend fun getAll(): Try<List<Categoria>> {
        return try {
            val list = remoteDataSource.getAll().map { Categoria(id = it.id, nombre = it.nombre, descripcion = it.descripcion) }
            Try.Success(list)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar categorias", e))
        }
    }

    override suspend fun getById(id: Int): Try<Categoria> {
        return try {
            val dto = remoteDataSource.getById(id)
            Try.Success(Categoria(id = dto.id, nombre = dto.nombre, descripcion = dto.descripcion))
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar categoria", e))
        }
    }

    override suspend fun create(nombre: String, descripcion: String): Try<Categoria> {
        return try {
            val dto = remoteDataSource.create(nombre, descripcion)
            Try.Success(Categoria(id = dto.id, nombre = dto.nombre, descripcion = dto.descripcion))
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al crear categoria", e))
        }
    }

    override suspend fun update(id: Int, nombre: String, descripcion: String): Try<Categoria> {
        return try {
            val dto = remoteDataSource.update(id, nombre, descripcion)
            Try.Success(Categoria(id = dto.id, nombre = dto.nombre, descripcion = dto.descripcion))
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al actualizar categoria", e))
        }
    }

    override suspend fun delete(id: Int): Try<Unit> {
        return try {
            remoteDataSource.delete(id)
            Try.Success(Unit)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al eliminar categoria", e))
        }
    }
}
