package org.example.project.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import org.example.project.data.remote.dto.LoginRequest
import org.example.project.data.remote.dto.RegisterRequest
import org.example.project.data.remote.dto.UpdateUserRequest
import org.example.project.data.remote.dto.UserDTO

class AuthRemoteDataSource(
    private val client: KtorClient = KtorClient
) {
    private val httpClient get() = client.client

    suspend fun login(request: LoginRequest): UserDTO {
        val response = httpClient.post("user/login") { setBody(request) }
        println("=== DEBUG login response status: ${response.status} ===")
        println("=== DEBUG login all headers: ${response.headers.entries()} ===")
        
        val setCookieHeaders = response.headers.getAll("Set-Cookie")
        if (setCookieHeaders != null) {
            println("=== DEBUG login Set-Cookie headers: $setCookieHeaders ===")
            val parsedCookies = setCookieHeaders.mapNotNull { raw ->
                raw.split(";").firstOrNull()?.trim()
            }
            SessionCookieStore.cookies = parsedCookies
            println("=== DEBUG Stored cookies: ${SessionCookieStore.cookies} ===")
        }
        if (response.status == io.ktor.http.HttpStatusCode.OK) {
            return response.body()
        } else if (response.status == io.ktor.http.HttpStatusCode.Unauthorized) {
            throw Exception("Usuario o contraseÃ±a incorrectos")
        } else {
            throw Exception("Error en el servidor: ${response.status}")
        }
    }

    suspend fun register(request: RegisterRequest) {
        val response = httpClient.post("user/register") { setBody(request) }
        if (response.status.value !in 200..299) {
            val errorBody: String = try { response.body() } catch (e: Exception) { response.status.description }
            throw Exception(errorBody.ifBlank { "Error al registrar usuario: ${response.status.value}" })
        }
    }

    suspend fun updateUser(userId: Int, request: UpdateUserRequest): UserDTO {
        val response = httpClient.put("user/$userId") { setBody(request) }
        if (response.status.value in 200..299) {
            return response.body()
        } else {
            val errorBody: String = try { response.body() } catch (e: Exception) { response.status.description }
            throw Exception(errorBody.ifBlank { "Error al actualizar perfil: ${response.status.value}" })
        }
    }
}
