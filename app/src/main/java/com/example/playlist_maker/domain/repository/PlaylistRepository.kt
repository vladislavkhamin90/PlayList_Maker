package com.example.playlist_maker.domain.repository

import com.example.playlist_maker.data.db.PlaylistEntity
import com.example.playlist_maker.domain.models.Track

interface PlaylistRepository {
    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverImagePath: String?
    ): Long

    suspend fun updatePlaylist(playlist: PlaylistEntity)
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?
    suspend fun getAllPlaylists(): List<PlaylistEntity>
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean
    suspend fun getPlaylistsWithTrack(trackId: Long): List<PlaylistEntity>
}