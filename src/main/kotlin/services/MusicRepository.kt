package com.example.services

import com.example.database.DatabaseFactory.dbQuery
import com.example.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

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
                    duration = row[Tracks.duration]
                )
            }

        mapOf(
            "name" to albumRow[Albumes.title],
            "artists" to listOf(mapOf("name" to albumRow[Artistas.name])),
            "images" to listOf(mapOf("url" to "assets/img/album_${albumId}.jpg")),
            "tracks" to mapOf("items" to tracks)
        )
    }
    suspend fun createArtist(data: CreateArtistRequest): String = dbQuery {
        val insertStatement = Artistas.insert {
            it[name] = data.name
            it[genre] = data.genre
        }
        insertStatement[Artistas.id].toString()
    }

    suspend fun deleteArtist(id: String): Boolean = dbQuery {
        val uuid = java.util.UUID.fromString(id)
        Artistas.deleteWhere { Artistas.id eq uuid } > 0
    }

    suspend fun createAlbum(data: CreateAlbumRequest): String = dbQuery {
        val artistUuid = java.util.UUID.fromString(data.artistId)
        val insertStatement = Albumes.insert {
            it[title] = data.title
            it[releaseYear] = data.releaseYear
            it[artistId] = artistUuid
        }
        insertStatement[Albumes.id].toString()
    }

    suspend fun deleteAlbum(id: String): Boolean = dbQuery {
        val uuid = java.util.UUID.fromString(id)
        Albumes.deleteWhere { Albumes.id eq uuid } > 0
    }

    suspend fun createTrack(data: CreateTrackRequest): String = dbQuery {
        val albumUuid = java.util.UUID.fromString(data.albumId)
        val insertStatement = Tracks.insert {
            it[title] = data.title
            it[duration] = data.duration
            it[albumId] = albumUuid
        }
        insertStatement[Tracks.id].toString()
    }

    suspend fun updateTrack(id: String, data: UpdateTrackRequest): Boolean = dbQuery {
        val uuid = java.util.UUID.fromString(id)
        Tracks.update({ Tracks.id eq uuid }) {
            if (data.title != null) it[title] = data.title
            if (data.duration != null) it[duration] = data.duration
        } > 0
    }

    suspend fun deleteTrack(id: String): Boolean = dbQuery {
        val uuid = java.util.UUID.fromString(id)
        Tracks.deleteWhere { Tracks.id eq uuid } > 0
    }
}