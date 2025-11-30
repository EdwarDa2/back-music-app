package com.example.domain.ports

import com.example.domain.models.*

interface MusicRepository {
    suspend fun search(query: String): Map<String, Any>
    suspend fun getAlbumDetails(albumId: String): Map<String, Any>
    suspend fun getArtistWithAlbums(artistId: String): ArtistWithAlbums

    suspend fun createArtist(data: CreateArtistRequest): String
    suspend fun deleteArtist(id: String): Boolean

    suspend fun createAlbum(data: CreateAlbumRequest): String
    suspend fun deleteAlbum(id: String): Boolean

    suspend fun createTrack(data: CreateTrackRequest): String
    suspend fun updateTrack(id: String, data: UpdateTrackRequest): Boolean
    suspend fun deleteTrack(id: String): Boolean
}