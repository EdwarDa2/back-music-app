package com.example.models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import kotlinx.serialization.Serializable

object Artistas : UUIDTable("artistas") {


    val name = varchar("name", 100)
    val genre = varchar("genre", 50).nullable()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
}

object Albumes : UUIDTable("albumes") {

    val title = varchar("title", 150)
    val releaseYear = integer("release_year").nullable()

    val artistId = reference("artist_id", Artistas)

    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
}

object Tracks : UUIDTable("tracks") {

    val title = varchar("title", 150)
    val duration = integer("duration")


    val albumId = reference("album_id", Albumes)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
}


@Serializable
data class ArtistDTO(val id: String, val name: String, val genre: String?)

@Serializable
data class AlbumDTO(val id: String, val title: String, val artist: String?)

@Serializable
data class TrackDTO(
    val id: String,
    val title: String,
    val duration: Int,
    val artist: String? = null,
    val cover: String? = null
)
@Serializable
data class CreateArtistRequest(val name: String, val genre: String)

@Serializable
data class CreateAlbumRequest(val title: String, val releaseYear: Int, val artistId: String)

@Serializable
data class CreateTrackRequest(
    val title: String,
    val duration: Int,
    val albumId: String,
)

@Serializable
data class UpdateTrackRequest(val title: String?, val duration: Int?)