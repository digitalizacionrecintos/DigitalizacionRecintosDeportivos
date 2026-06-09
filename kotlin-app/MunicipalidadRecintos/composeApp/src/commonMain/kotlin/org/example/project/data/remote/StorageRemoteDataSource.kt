package org.example.project.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class StorageRemoteDataSource(
    private val client: KtorClient = KtorClient
) {
    private val httpClient get() = client.client

    suspend fun uploadFile(fileBytes: ByteArray, fileName: String, mimeType: String = "image/jpeg"): String {
        val response = httpClient.submitFormWithBinaryData(
            url = "storage/upload",
            formData = formData {
                append("file", fileBytes, Headers.build {
                    append(HttpHeaders.ContentType, mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        )
        return response.body()
    }
}
