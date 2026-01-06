package org.example.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.domain.manager.ServerConfigManager

object KtorClient {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
            )
        }

        install(HttpCookies) {

        }

        defaultRequest {
            url(ServerConfigManager.getServerUrl())
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }
}
