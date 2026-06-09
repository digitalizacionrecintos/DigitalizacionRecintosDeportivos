package org.example.project.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import org.example.project.data.remote.dto.CsrfTokenResponse
import org.example.project.data.remote.dto.FCMTokenRequest
import org.example.project.data.remote.dto.NotificacionDTO

class NotificacionRemoteDataSource(
    private val client: KtorClient = KtorClient
) {
    private val httpClient get() = client.client

    suspend fun getByUser(idUsuario: Int): List<NotificacionDTO> {
        return httpClient.get("notificaciones/usuario/$idUsuario").body()
    }

    suspend fun markAsRead(idNotificacion: Int) {
        httpClient.put("notificaciones/$idNotificacion/leer")
    }

    suspend fun saveFCMToken(request: FCMTokenRequest) {
        httpClient.post("notificaciones/token") { setBody(request) }
    }

    suspend fun getCsrfToken(): CsrfTokenResponse {
        return httpClient.get("csrf-token").body()
    }
}
