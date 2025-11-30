package com.example.services

import com.example.database.DatabaseFactory.dbQuery
import com.example.models.*
import org.jetbrains.exposed.sql.*

class MusicRepository {

    suspend fun search(query: String): Map<String, Any> = dbQuery {

        val tracks = (Tracks innerJoin Albumes innerJoin Artistas)
            .select { Tracks.title.lowerCase() like "%${query.lowercase()}%" }
            .limit(5)
            .map { row ->
                TrackDTO(
                    id = row[Tracks.id].toString(),
                    title = row[Tracks.title],
                    duration = row[Tracks.duration],
                    previewUrl = row[Tracks.previewUrl],
                    artist = row[Artistas.name],
                    cover = "assets/img/album_${row[Albumes.id]}.jpg"
                )
            }


        val artists = Artistas
            .select { Artistas.name.lowerCase() like "%${query.lowercase()}%" }
            .limit(5)
            .map { row ->
                ArtistDTO(row[Artistas.id].toString(), row[Artistas.name], row[Artistas.genre])
            }


        val albums = (Albumes innerJoin Artistas)
            .select { Albumes.title.lowerCase() like "%${query.lowercase()}%" }
            .limit(5)
            .map { row ->
                AlbumDTO(row[Albumes.id].toString(), row[Albumes.title], row[Artistas.name])
            }

        mapOf(
            "tracks" to mapOf("items" to tracks),
            "artists" to mapOf("items" to artists),
            "albums" to mapOf("items" to albums)
        )
    }

    suspend fun getAlbumDetails(albumId: String): Map<String, Any> = dbQuery {
        val uuid = java.util.UUID.fromString(albumId)

        val albumRow = (Albumes innerJoin Artistas)
            .select { Albumes.id eq uuid }
            .single()


        val tracks = Tracks
            .select { Tracks.albumId eq uuid }
            .map { row ->
                TrackDTO(
                    id = row[Tracks.id].toString(),
                    title = row[Tracks.title],
                    duration = row[Tracks.duration],
                    previewUrl = row[Tracks.previewUrl]
                )
            }

        mapOf(
            "name" to albumRow[Albumes.title],
            "artists" to listOf(mapOf("name" to albumRow[Artistas.name])),
            "images" to listOf(mapOf("url" to "assets/img/album_${albumId}.jpg")),
            "tracks" to mapOf("items" to tracks)
        )
    }
}