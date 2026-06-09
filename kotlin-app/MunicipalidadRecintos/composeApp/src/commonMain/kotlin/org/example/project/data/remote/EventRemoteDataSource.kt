package org.example.project.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import org.example.project.data.remote.dto.BatchAttendanceRequest
import org.example.project.data.remote.dto.InscriptionRequest
import org.example.project.data.remote.dto.InscriptionStatus
import org.example.project.data.remote.dto.ManagerEventDTO
import org.example.project.data.remote.dto.ManagerEventWithAttendeesDTO

class EventRemoteDataSource(
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

    suspend fun getAllEvents(): List<ManagerEventDTO> {
        return httpClient.get("event/client").body()
    }

    suspend fun getEventById(id: Int): ManagerEventDTO {
        val response = httpClient.get("event/$id")
        when (response.status.value) {
            200 -> return response.body()
            404 -> throw Exception("Evento con ID $id no encontrado en el servidor")
            401, 403 -> throw Exception("No autorizado para acceder al evento")
            500 -> throw Exception("Error interno del servidor")
            else -> throw Exception("Error al obtener evento: ${response.status.description}")
        }
    }

    suspend fun getManagerEventsByEncargado(idEncargado: Int): List<ManagerEventWithAttendeesDTO> {
        val responseBody = httpClient.get("event/manager/$idEncargado").body<String>()
        return Json { ignoreUnknownKeys = true }.decodeFromString(responseBody)
    }

    suspend fun checkEnrollment(eventId: Int, userId: Int): InscriptionStatus {
        val response = httpClient.get("inscripcion/check") {
            parameter("idEvento", eventId)
            parameter("idUsuario", userId)
        }
        val rawJson = response.bodyAsText()
        println("=== DEBUG event/checkEnrollment($eventId, $userId) status: ${response.status} ===")
        println("=== RAW JSON: ${rawJson.take(2000)} ===")
        return try {
            debugJson.decodeFromString<InscriptionStatus>(rawJson)
        } catch (e: Exception) {
            println("=== DEBUG DESERIALIZATION ERROR: ${e.message} ===")
            throw e
        }
    }

    suspend fun registerInscription(eventId: Int, userId: Int, nombre: String = "", apellido: String = "", edad: Int = 0): String {
        val response = httpClient.post("inscripcion/register") {
            setBody(InscriptionRequest(idTutor = userId, idEvento = eventId, nombre = nombre, apellido = apellido, edad = edad))
        }
        return when (response.status.value) {
            200, 201 -> "Inscripción realizada con éxito"
            else -> {
                val errorBody: String = try { response.body() } catch (e: Exception) { response.status.description }
                throw Exception(errorBody.ifBlank { "Error al inscribir: ${response.status.value}" })
            }
        }
    }

    suspend fun changeEventStatus(eventId: Int, status: String) {
        httpClient.put("event/change-status/$eventId") { setBody(status) }
    }

    suspend fun updateAttendanceBatch(idEvento: Int, ids: List<Int>) {
        httpClient.put("inscripcion/attendance-batch") {
            setBody(BatchAttendanceRequest(idEvento, ids))
        }
    }
}
