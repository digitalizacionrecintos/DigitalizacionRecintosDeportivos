package org.example.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.domain.manager.ServerConfigManager

object SessionCookieStore {
    var cookies: List<String> = emptyList()
}

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

        defaultRequest {
            url(ServerConfigManager.getServerUrl())
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            
            header(HttpHeaders.Origin, "http://localhost")
            if (SessionCookieStore.cookies.isNotEmpty()) {
                header(HttpHeaders.Cookie, SessionCookieStore.cookies.joinToString("; "))
            }
        }
    }
}
