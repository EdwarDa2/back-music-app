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
    embeddedServer(CIO, port = 3000, host = "0.0.0.0", module = Application::module)
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
        anyHost()
    }

    routing {

        get("/") { call.respondText("Exito de api") }

        route("/api/artistas") {
            post {
                try {
                    val req = call.receive<CreateArtistRequest>()
                    val newId = repository.createArtist(req)
                    call.respond(HttpStatusCode.Created, mapOf("id" to newId))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
                }
            }

            get("/{id}") {
                val id = call.parameters["id"]
                if (id != null) {
                    try {
                        val result = repository.getArtistWithAlbums(id)
                        call.respond(result)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }

        route("/api/albumes") {
            // POST (Crear)
            post {
                try {
                    val req = call.receive<CreateAlbumRequest>()
                    val newId = repository.createAlbum(req)
                    call.respond(HttpStatusCode.Created, mapOf("id" to newId))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
                }
            }
        }

        route("/api/tracks") {
            // POST (Crear)
            post {
                try {
                    val req = call.receive<CreateTrackRequest>()
                    val newId = repository.createTrack(req)
                    call.respond(HttpStatusCode.Created, mapOf("id" to newId))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
                }
            }
        }

        get("/api/search") {
            val query = call.request.queryParameters["q"] ?: ""
            val result = repository.search(query)
            call.respond(result)
        }

        get("/api/album/{id}") {
            val id = call.parameters["id"]
            if (id != null) {
                val result = repository.getAlbumDetails(id)
                call.respond(result)
            }
        }
    }
}