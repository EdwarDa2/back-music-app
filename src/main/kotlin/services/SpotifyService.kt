package com.example.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

private const val CLIENT_ID = "f9e00fa73baf41b084a1fe6b7da9c9ba"
private const val CLIENT_SECRET = "3f7064d1dfd5451b8b02efb38234c7b8"

@Serializable
data class SpotifyTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int
)

class SpotifyService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private var currentToken: String? = null
    private var tokenExpiration: Long = 0

    private suspend fun getToken(): String {
        val now = System.currentTimeMillis()
        if (currentToken != null && now < tokenExpiration) return currentToken!!

        val response: SpotifyTokenResponse = client.submitForm(
            url = "https://accounts.spotify.com/api/token",
            formParameters = Parameters.build {
                append("grant_type", "client_credentials")
                append("client_id", CLIENT_ID)
                append("client_secret", CLIENT_SECRET)
            }
        ).body()

        currentToken = response.accessToken
        tokenExpiration = now + (response.expiresIn * 1000) - 60000
        return currentToken!!
    }

    suspend fun search(query: String): JsonObject {
        val token = getToken()
        return client.get("https://api.spotify.com/v1/search") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
            parameter("q", query)
            parameter("type", "track,artist,album")
            parameter("limit", "5")
        }.body()
    }

    suspend fun getAlbum(id: String): JsonObject {
        val token = getToken()
        return client.get("https://api.spotify.com/v1/albums/$id") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }.body()
    }
}