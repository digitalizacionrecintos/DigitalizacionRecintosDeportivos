package org.example.project.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import org.example.project.data.remote.KtorClient.client
import org.example.project.data.remote.dto.InscriptionStatus
import org.example.project.data.remote.dto.LoginRequest
import org.example.project.data.remote.dto.ManagerEventDTO
import org.example.project.data.remote.dto.ManagerEventWithAttendeesDTO
import org.example.project.data.remote.dto.RegisterRequest
import org.example.project.data.remote.dto.UserDTO

class ApiService {
    private val client = KtorClient.client

    suspend fun login(request: LoginRequest): UserDTO {
        val response = client.post("user/login") { setBody(request) }

        if (response.status == io.ktor.http.HttpStatusCode.OK) {
            return response.body()
        } else if (response.status == io.ktor.http.HttpStatusCode.Unauthorized) {
            throw Exception("Usuario o contraseña incorrectos")
        } else {
            throw Exception("Error en el servidor: ${response.status}")
        }
    }

    suspend fun register(request: RegisterRequest) {
        val response = client.post("user/register") { setBody(request) }

        if (response.status.value !in 200..299) {
            val errorBody: String =
                    try {
                        response.body()
                    } catch (e: Exception) {
                        response.status.description
                    }
            throw Exception(
                    errorBody.ifBlank { "Error al registrar usuario: ${response.status.value}" }
            )
        }
    }

    suspend fun updateUser(
            userId: Int,
            request: org.example.project.data.remote.dto.UpdateUserRequest
    ): UserDTO {
        val response = client.put("user/$userId") { setBody(request) }

        if (response.status.value in 200..299) {
            return response.body()
        } else {
            val errorBody: String =
                    try {
                        response.body()
                    } catch (e: Exception) {
                        response.status.description
                    }
            throw Exception(
                    errorBody.ifBlank { "Error al actualizar perfil: ${response.status.value}" }
            )
        }
    }

    suspend fun getManagerEvents(): List<ManagerEventDTO> {

        return client.get("event/all").body()
    }

    suspend fun getAllEvents(): List<ManagerEventDTO> {
        return client.get("event/all").body()
    }

    suspend fun getManagerEventsByEncargado(idEncargado: Int): List<ManagerEventWithAttendeesDTO> {
        val responseBody = client.get("event/manager/$idEncargado").body<String>()
        println("DEBUG: Raw JSON Response for Manager Events: $responseBody")
        return kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                .decodeFromString(responseBody)
    }

    suspend fun getRawManagerEventsResponse(idEncargado: Int): String {
        return client.get("event/manager/$idEncargado").body<String>()
    }

    suspend fun getEventAttendees(eventId: Int): ManagerEventWithAttendeesDTO {
        return client.get("event/$eventId/attendees").body()
    }

    suspend fun getEventById(id: Int): ManagerEventDTO {
        val response = client.get("event/$id")

        when (response.status.value) {
            200 -> return response.body()
            404 -> throw Exception("Evento con ID $id no encontrado en el servidor")
            401, 403 -> throw Exception("No autorizado para acceder al evento")
            500 -> throw Exception("Error interno del servidor")
            else -> throw Exception("Error al obtener evento: ${response.status.description}")
        }
    }

    suspend fun changeEventStatus(eventId: Int, status: String) {
        client.put("event/change-status/$eventId") { setBody(status) }
    }

    suspend fun checkEnrollment(eventId: Int, userId: Int): InscriptionStatus {
        println("\n=== DEBUG: API checkEnrollment Request ===")
        println("Endpoint: inscripcion/check")
        println("Event ID: $eventId")
        println("User ID: $userId")

        val response =
                client.get("inscripcion/check") {
                    parameter("idEvento", eventId)
                    parameter("idUsuario", userId)
                }

        val status: InscriptionStatus = response.body()

        println("\n=== DEBUG: API checkEnrollment Response ===")
        println("HTTP Status: ${response.status.value}")
        println("Response Body:")
        println("  yaInscrito: ${status.yaInscrito}")
        println("  cupoDisponible: ${status.cupoDisponible}")
        println("  puedeInscribirse: ${status.puedeInscribirse}")
        println("  mensaje: ${status.mensaje}")
        println("================================\n")

        return status
    }

    suspend fun registerInscription(eventId: Int, userId: Int): String {
        println("\n=== DEBUG: API registerInscription Request ===")
        println("Endpoint: inscripcion/register")
        println("Event ID: $eventId")
        println("User ID: $userId")

        val response =
                client.post("inscripcion/register") {
                    setBody(
                            org.example.project.data.remote.dto.InscriptionRequest(
                                    idUsuario = userId,
                                    idEvento = eventId
                            )
                    )
                }

        println("\n=== DEBUG: API registerInscription Response ===")
        println("HTTP Status: ${response.status.value} - ${response.status.description}")

        when (response.status.value) {
            200, 201 -> {
                val successMessage = "Inscripción realizada con éxito"
                println("Success: $successMessage")
                println("================================\n")
                return successMessage
            }
            else -> {

                val errorMessage =
                        try {
                            val errorBody: String = response.body()
                            println("Error Body: $errorBody")

                            if (errorBody.isNotBlank()) errorBody else "Error al inscribir"
                        } catch (e: Exception) {
                            println("Could not read error body: ${e.message}")
                            "Error al inscribir: ${response.status.description}"
                        }
                println("Error Message: $errorMessage")
                println("================================\n")
                throw Exception(errorMessage)
            }
        }
    }

    suspend fun updateAttendanceBatch(idEvento: Int, ids: List<Int>) {
        val response =
                client.put("inscripcion/attendance-batch") {
                    setBody(
                            org.example.project.data.remote.dto.BatchAttendanceRequest(
                                    idEvento,
                                    ids
                            )
                    )
                }
    }

    suspend fun getUserEnrollments(
            userId: Int
    ): List<org.example.project.data.remote.dto.UserHistoryDTO> {
        val response = client.get("user/$userId/history")
        return response.body()
    }
}
