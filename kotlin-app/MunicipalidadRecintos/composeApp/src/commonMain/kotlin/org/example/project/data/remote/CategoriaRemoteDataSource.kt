package org.example.project.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import org.example.project.data.remote.dto.CategoriaDTO
import org.example.project.data.remote.dto.CreateCategoriaRequest

class CategoriaRemoteDataSource(
    private val client: KtorClient = KtorClient
) {
    private val httpClient get() = client.client

    suspend fun getAll(): List<CategoriaDTO> {
        return httpClient.get("categoria/all").body()
    }

    suspend fun getById(id: Int): CategoriaDTO {
        return httpClient.get("categoria/$id").body()
    }

    suspend fun create(nombre: String, descripcion: String): CategoriaDTO {
        return httpClient.post("categoria/create") {
            setBody(CreateCategoriaRequest(nombre, descripcion))
        }.body()
    }

    suspend fun update(id: Int, nombre: String, descripcion: String): CategoriaDTO {
        return httpClient.put("categoria/edit/$id") {
            setBody(CreateCategoriaRequest(nombre, descripcion))
        }.body()
    }

    suspend fun delete(id: Int) {
        httpClient.delete("categoria/$id")
    }
}
