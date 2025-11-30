package com.example.adapters.outbound

import com.example.domain.models.*
import com.example.domain.ports.MusicRepository
import com.example.adapters.outbound.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.UUID

class ExposedMusicRepository : MusicRepository {

    override suspend fun search(query: String): Map<String, Any> = dbQuery {
        val tracks = (Tracks innerJoin Albumes innerJoin Artistas)
            .select { Tracks.title.lowerCase() like "%${query.lowercase()}%" }
            .limit(5)
            .map { row ->
                Track(
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
                Artist(row[Artistas.id].toString(), row[Artistas.name], row[Artistas.genre])
            }

        val albums = (Albumes innerJoin Artistas)
            .select { Albumes.title.lowerCase() like "%${query.lowercase()}%" }
            .limit(5)
            .map { row ->
                Album(row[Albumes.id].toString(), row[Albumes.title], row[Artistas.name])
            }

        mapOf(
            "tracks" to mapOf("items" to tracks),
            "artists" to mapOf("items" to artists),
            "albums" to mapOf("items" to albums)
        )
    }

    override suspend fun getAlbumDetails(albumId: String): Map<String, Any> = dbQuery {
        val uuid = UUID.fromString(albumId)
        val albumRow = (Albumes innerJoin Artistas).select { Albumes.id eq uuid }.single()

        val tracks = Tracks.select { Tracks.albumId eq uuid }.map { row ->
            Track(
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

    override suspend fun getArtistWithAlbums(artistId: String): ArtistWithAlbums = dbQuery {
        val uuid = UUID.fromString(artistId)
        val artistRow = Artistas.select { Artistas.id eq uuid }.single()

        val albums = Albumes.select { Albumes.artistId eq uuid }.map { row ->
            Album(
                id = row[Albumes.id].toString(),
                title = row[Albumes.title],
                artist = artistRow[Artistas.name]
            )
        }

        ArtistWithAlbums(
            id = artistRow[Artistas.id].toString(),
            name = artistRow[Artistas.name],
            genre = artistRow[Artistas.genre],
            albums = albums
        )
    }


    override suspend fun createArtist(data: CreateArtistRequest): String = dbQuery {
        val insertStatement = Artistas.insert {
            it[name] = data.name
            it[genre] = data.genre
        }
        insertStatement[Artistas.id].toString()
    }

    override suspend fun deleteArtist(id: String): Boolean = dbQuery {
        val uuid = UUID.fromString(id)
        Artistas.deleteWhere { Artistas.id eq uuid } > 0
    }

    override suspend fun createAlbum(data: CreateAlbumRequest): String = dbQuery {
        val artistUuid = UUID.fromString(data.artistId)
        val insertStatement = Albumes.insert {
            it[title] = data.title
            it[releaseYear] = data.releaseYear
            it[artistId] = artistUuid
        }
        insertStatement[Albumes.id].toString()
    }

    override suspend fun deleteAlbum(id: String): Boolean = dbQuery {
        val uuid = UUID.fromString(id)
        Albumes.deleteWhere { Albumes.id eq uuid } > 0
    }

    override suspend fun createTrack(data: CreateTrackRequest): String = dbQuery {
        val albumUuid = UUID.fromString(data.albumId)
        val insertStatement = Tracks.insert {
            it[title] = data.title
            it[duration] = data.duration
            it[albumId] = albumUuid
        }
        insertStatement[Tracks.id].toString()
    }

    override suspend fun updateTrack(id: String, data: UpdateTrackRequest): Boolean = dbQuery {
        val uuid = UUID.fromString(id)
        Tracks.update({ Tracks.id eq uuid }) {
            if (data.title != null) it[title] = data.title
            if (data.duration != null) it[duration] = data.duration
        } > 0
    }

    override suspend fun deleteTrack(id: String): Boolean = dbQuery {
        val uuid = UUID.fromString(id)
        Tracks.deleteWhere { Tracks.id eq uuid } > 0
    }
}