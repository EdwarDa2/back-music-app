package com.example

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.database.DatabaseFactory
import com.example.services.MusicRepository

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()

    val repository = MusicRepository()

    install(ContentNegotiation) { json() }
    install(CORS) {
        allowHost("localhost:4200", schemes = listOf("http", "https"))
        anyHost()
    }

    routing {
        // Búsqueda
        get("/api/search") {
            val query = call.request.queryParameters["q"] ?: ""
            val result = repository.search(query)
            call.respond(result)
        }

        // Álbum
        get("/api/album/{id}") {
            val id = call.parameters["id"]
            if (id != null) {
                val result = repository.getAlbumDetails(id)
                call.respond(result)
            }
        }
    }
}