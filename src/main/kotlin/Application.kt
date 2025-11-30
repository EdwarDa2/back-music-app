package com.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.database.DatabaseFactory
import com.example.services.MusicRepository
import com.example.models.*

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()

    val repository = MusicRepository()

    install(ContentNegotiation) { json() }
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHost("localhost:4200", schemes = listOf("http", "https"))
        anyHost()
    }

    routing {
        get("/") {
            call.respondText("API Ktor MusicApp Lista 🚀")
        }

        get("/search") {
            val query = call.request.queryParameters["q"] ?: ""
            val result = repository.search(query)
            call.respond(result)
        }

        get("/albumes/{id}") {
            val id = call.parameters["id"]
            if (id != null) {
                try {
                    val result = repository.getAlbumDetails(id)
                    call.respond(result)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound, "Álbum no encontrado")
                }
            }
        }

        post("/artistas") {
            try {
                val req = call.receive<CreateArtistRequest>()
                val newId = repository.createArtist(req)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error al crear artista: ${e.localizedMessage}")
            }
        }

        delete("/artistas/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (repository.deleteArtist(id)) call.respond(HttpStatusCode.OK, "Artista eliminado")
            else call.respond(HttpStatusCode.NotFound)
        }

        post("/albumes") {
            try {
                val req = call.receive<CreateAlbumRequest>()
                val newId = repository.createAlbum(req)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error al crear álbum: ${e.localizedMessage}")
            }
        }

        delete("/albumes/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (repository.deleteAlbum(id)) call.respond(HttpStatusCode.OK, "Álbum eliminado")
            else call.respond(HttpStatusCode.NotFound)
        }

        // --- TRACKS ---
        post("/tracks") {
            try {
                val req = call.receive<CreateTrackRequest>()
                val newId = repository.createTrack(req)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error al crear track: ${e.localizedMessage}")
            }
        }

        put("/tracks/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            try {
                val req = call.receive<UpdateTrackRequest>()
                if (repository.updateTrack(id, req)) call.respond(HttpStatusCode.OK, "Track actualizado")
                else call.respond(HttpStatusCode.NotFound)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Datos inválidos")
            }
        }

        delete("/tracks/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (repository.deleteTrack(id)) call.respond(HttpStatusCode.OK, "Track eliminado")
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}