package com.example.domain.ports

import com.example.domain.models.*

interface MusicRepository {
    suspend fun search(query: String): SearchResponse
    suspend fun getAlbumDetails(albumId: String): Map<String, Any>
    suspend fun getArtistWithAlbums(artistId: String): ArtistWithAlbums

    suspend fun createArtist(data: CreateArtistRequest): String
    suspend fun deleteArtist(id: String): Boolean

    suspend fun createAlbum(data: CreateAlbumRequest): String
    suspend fun deleteAlbum(id: String): Boolean

    suspend fun createTrack(data: CreateTrackRequest): String
    suspend fun updateTrack(id: String, data: UpdateTrackRequest): Boolean
    suspend fun deleteTrack(id: String): Boolean
    suspend fun getAllArtists(): List<Artist>
    suspend fun updateArtist(id: String, data: UpdateArtistRequest): Boolean

    suspend fun getAllAlbums(): List<Album>
    suspend fun getAlbumById(id: String): Album?
    suspend fun updateAlbum(id: String, data: UpdateAlbumRequest): Boolean

    suspend fun getAllTracks(): List<Track>
    suspend fun getTrackById(id: String): Track?
}