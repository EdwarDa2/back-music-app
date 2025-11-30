package com.example.domain.models

import kotlinx.serialization.Serializable


@Serializable
data class Artist(val id: String, val name: String, val genre: String?)

@Serializable
data class Album(val id: String, val title: String, val artist: String?)

@Serializable
data class Track(
    val id: String,
    val title: String,
    val duration: Int,
    val artist: String? = null,
    val cover: String? = null
)

@Serializable
data class ArtistWithAlbums(
    val id: String,
    val name: String,
    val genre: String?,
    val albums: List<Album>
)


@Serializable
data class CreateArtistRequest(val name: String, val genre: String)

@Serializable
data class CreateAlbumRequest(val title: String, val releaseYear: Int, val artistId: String)

@Serializable
data class CreateTrackRequest(val title: String, val duration: Int, val albumId: String)

@Serializable
data class UpdateTrackRequest(val title: String?, val duration: Int?)