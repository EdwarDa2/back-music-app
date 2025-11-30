package com.example.adapters.inbound

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.domain.ports.MusicRepository
import com.example.domain.models.*

fun Route.musicRoutes(repository: MusicRepository) {

    get("/") { call.respondText("API exitosa") }

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
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (repository.deleteArtist(id)) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }
    }

    route("/api/albumes") {
        post {
            try {
                val req = call.receive<CreateAlbumRequest>()
                val newId = repository.createAlbum(req)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
            }
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (repository.deleteAlbum(id)) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }
    }

    route("/api/tracks") {
        post {
            try {
                val req = call.receive<CreateTrackRequest>()
                val newId = repository.createTrack(req)
                call.respond(HttpStatusCode.Created, mapOf("id" to newId))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
            }
        }
        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            try {
                val req = call.receive<UpdateTrackRequest>()
                if (repository.updateTrack(id, req)) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.NotFound)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (repository.deleteTrack(id)) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
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
    get("/api/artistas") {
        val artistas = repository.getAllArtists()
        call.respond(artistas)
    }

    // 2. Actualizar
    put("/api/artistas/{id}") {
        val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
        try {
            val req = call.receive<UpdateArtistRequest>()
            if (repository.updateArtist(id, req)) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    delete("/api/artistas/{id}") {
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        if (repository.deleteArtist(id)) call.respond(HttpStatusCode.OK)
        else call.respond(HttpStatusCode.NotFound)
    }

    get("/api/albumes") {
        val albumes = repository.getAllAlbums()
        call.respond(albumes)
    }

    get("/api/albumes/{id}") {
        val id = call.parameters["id"]
        if (id != null) {
            val album = repository.getAlbumById(id)
            if (album != null) call.respond(album)
            else call.respond(HttpStatusCode.NotFound)
        }
    }

    put("/api/albumes/{id}") {
        val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
        try {
            val req = call.receive<UpdateAlbumRequest>()
            if (repository.updateAlbum(id, req)) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    get("/api/tracks") {
        val tracks = repository.getAllTracks()
        call.respond(tracks)
    }

    get("/api/tracks/{id}") {
        val id = call.parameters["id"]
        if (id != null) {
            val track = repository.getTrackById(id)
            if (track != null) call.respond(track)
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}