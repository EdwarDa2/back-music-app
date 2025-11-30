package com.example.adapters.outbound


import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

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