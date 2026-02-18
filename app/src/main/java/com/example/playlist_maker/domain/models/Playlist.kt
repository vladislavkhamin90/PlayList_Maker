package com.example.playlist_maker.domain.models

data class Playlist(
    val id: Long,
    val name: String,
    val description: String?,
    val coverImagePath: String?,
    val trackCount: Int,
    val hasTrack: Boolean = false
)