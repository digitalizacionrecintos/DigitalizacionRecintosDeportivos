package org.example.project.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import org.example.project.data.remote.dto.CursoDTO
import org.example.project.data.remote.dto.CreateCursoRequest
import org.example.project.data.remote.dto.InscripcionEstadoCursoResponseDTO
import org.example.project.data.remote.dto.RegisterCourseInscriptionRequest

class CursoRemoteDataSource(
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

    suspend fun getAll(): List<CursoDTO> {
        return httpClient.get("curso/all").body()
    }

    suspend fun getAvailable(): List<CursoDTO> {
        return httpClient.get("curso/avaible").body()
    }

    suspend fun getById(id: Int): CursoDTO {
        val response = httpClient.get("curso/$id")
        println("=== DEBUG curso/getById($id) status: ${response.status} ===")
        when (response.status.value) {
            200 -> {
                val rawJson = response.bodyAsText()
                println("=== DEBUG curso/getById raw JSON (first 2000): ===")
                println(rawJson.take(2000))
                return debugJson.decodeFromString<CursoDTO>(rawJson)
            }
            404 -> throw Exception("Curso con ID $id no encontrado")
            401, 403 -> throw Exception("No autorizado para acceder al curso")
            500 -> {
                val errBody = response.bodyAsText()
                println("=== DEBUG curso/getById 500 error body: $errBody ===")
                throw Exception("Error interno del servidor")
            }
            else -> throw Exception("Error al obtener curso: ${response.status.description}")
        }
    }

    suspend fun create(request: CreateCursoRequest): CursoDTO {
        val response = httpClient.post("curso/create") { setBody(request) }
        return response.body()
    }

    suspend fun update(id: Int, request: CreateCursoRequest): CursoDTO {
        val response = httpClient.put("curso/edit/$id") { setBody(request) }
        return response.body()
    }

    suspend fun delete(id: Int) {
        httpClient.delete("curso/$id")
    }

    suspend fun publicar(id: Int): CursoDTO {
        return httpClient.post("curso/$id/publicar").body()
    }

    suspend fun cancelar(id: Int): CursoDTO {
        return httpClient.post("curso/$id/cancelar").body()
    }

    suspend fun checkEnrollment(cursoId: Int, userId: Int): InscripcionEstadoCursoResponseDTO {
        val response = httpClient.get("inscripcion/check-course") {
            parameter("idCurso", cursoId)
            parameter("idUsuario", userId)
        }
        val rawJson = response.bodyAsText()
        println("=== DEBUG checkEnrollment(cursoId=$cursoId, userId=$userId) status: ${response.status} ===")
        println("=== RAW JSON (first 2000): ===")
        println(rawJson.take(2000))
        return try {
            debugJson.decodeFromString<InscripcionEstadoCursoResponseDTO>(rawJson)
        } catch (e: Exception) {
            println("=== DEBUG checkEnrollment DESERIALIZATION ERROR: ${e.message} ===")
            println("=== Full JSON (${rawJson.length} chars) ===")
            println(rawJson)
            throw e
        }
    }

    suspend fun registerInscription(request: RegisterCourseInscriptionRequest): String {
        val jsonBody = debugJson.encodeToString(RegisterCourseInscriptionRequest.serializer(), request)
        println("=== DEBUG registerInscription REQUEST JSON ===")
        println(jsonBody)
        println("=== END REQUEST ===")
        val response = httpClient.put("inscripcion/register-course") { setBody(request) }
        val status = response.status
        val bodyText = response.bodyAsText()
        println("=== DEBUG registerInscription RESPONSE status: $status ===")
        println("=== RESPONSE body (${bodyText.length} chars): ===")
        println(bodyText.take(2000))
        println("=== END RESPONSE ===")
        if (!status.value.toString().startsWith("2")) {
            throw Exception("Error al registrar inscripción: $status - $bodyText")
        }
        return bodyText
    }
}
