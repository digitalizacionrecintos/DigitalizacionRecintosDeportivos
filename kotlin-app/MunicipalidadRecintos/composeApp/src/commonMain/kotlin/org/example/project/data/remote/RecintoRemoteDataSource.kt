package org.example.project.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import org.example.project.data.remote.dto.CreateRecintoRequest
import org.example.project.data.remote.dto.RecintoDTO
import org.example.project.data.remote.dto.UpdateRecintoRequest

class RecintoRemoteDataSource(
    private val client: KtorClient = KtorClient
) {
    private val httpClient get() = client.client

    suspend fun getAll(): List<RecintoDTO> {
        return httpClient.get("recinto/all").body()
    }

    suspend fun getAvailable(): List<RecintoDTO> {
        return httpClient.get("recinto/available").body()
    }

    suspend fun getById(id: Int): RecintoDTO {
        return httpClient.get("recinto/$id").body()
    }

    suspend fun create(request: CreateRecintoRequest): RecintoDTO {
        return httpClient.post("recinto/create") { setBody(request) }.body()
    }

    suspend fun update(id: Int, request: UpdateRecintoRequest): RecintoDTO {
        return httpClient.put("recinto/edit/$id") { setBody(request) }.body()
    }

    suspend fun delete(id: Int) {
        httpClient.delete("recinto/$id")
    }

    suspend fun changeStatus(id: Int, status: String): RecintoDTO {
        return httpClient.put("recinto/change-status/$id") { setBody(status) }.body()
    }
}
