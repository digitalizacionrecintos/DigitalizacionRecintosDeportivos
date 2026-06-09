package org.example.project.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.example.project.data.remote.dto.EstadisticasCursosDTO
import org.example.project.data.remote.dto.EstadisticasResponseDTO

class EstadisticasRemoteDataSource(
    private val client: KtorClient = KtorClient
) {
    private val httpClient get() = client.client

    suspend fun getGeneral(anio: Int? = null): EstadisticasResponseDTO {
        return httpClient.get("estadisticas/general") {
            anio?.let { parameter("anio", it) }
        }.body()
    }

    suspend fun getCursos(anio: Int? = null): EstadisticasCursosDTO {
        return httpClient.get("estadisticas/cursos") {
            anio?.let { parameter("anio", it) }
        }.body()
    }
}
