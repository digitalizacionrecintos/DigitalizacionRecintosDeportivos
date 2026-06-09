package org.example.project.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import org.example.project.data.remote.dto.RegisterRequest
import org.example.project.data.remote.dto.UserDTO

class EncargadoRemoteDataSource(
    private val client: KtorClient = KtorClient
) {
    private val httpClient get() = client.client

    suspend fun getAll(): List<UserDTO> {
        return httpClient.get("encargados").body()
    }

    suspend fun getById(id: Int): UserDTO {
        return httpClient.get("encargados/$id").body()
    }

    suspend fun create(request: RegisterRequest): UserDTO {
        return httpClient.post("encargados") { setBody(request) }.body()
    }

    suspend fun update(id: Int, request: org.example.project.data.remote.dto.UpdateUserRequest): UserDTO {
        return httpClient.put("encargados/$id") { setBody(request) }.body()
    }

    suspend fun delete(id: Int) {
        httpClient.delete("encargados/$id")
    }

    suspend fun getAvailableManagers(): List<UserDTO> {
        return httpClient.get("user/managers/available").body()
    }
}
