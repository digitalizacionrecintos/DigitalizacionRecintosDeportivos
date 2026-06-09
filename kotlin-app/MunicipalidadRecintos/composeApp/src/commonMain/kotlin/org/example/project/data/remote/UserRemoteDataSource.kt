package org.example.project.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import org.example.project.data.remote.dto.HistorialUsuarioDTO

class UserRemoteDataSource(
    private val client: KtorClient = KtorClient
) {
    private val httpClient get() = client.client

    companion object {
        private val debugJson = Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }

    suspend fun getUserHistory(userId: Int): HistorialUsuarioDTO {
        val response = httpClient.get("user/$userId/history")
        val rawJson = response.bodyAsText()
        println("=== DEBUG getUserHistory(userId=$userId) RAW JSON (first 3000 chars): ===")
        println(rawJson.take(3000))
        return try {
            debugJson.decodeFromString<HistorialUsuarioDTO>(rawJson)
        } catch (e: Exception) {
            println("=== DEBUG getUserHistory DESERIALIZATION ERROR: ${e.message} ===")
            println("=== Full JSON (${rawJson.length} chars) ===")
            println(rawJson)
            throw e
        }
    }
}
