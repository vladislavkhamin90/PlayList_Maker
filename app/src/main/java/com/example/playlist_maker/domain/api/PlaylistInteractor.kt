package com.example.playlist_maker.domain.api

import com.example.playlist_maker.domain.models.Playlist
import com.example.playlist_maker.domain.models.Track

interface PlaylistInteractor {
    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverImagePath: String?
    ): Long

    suspend fun getAllPlaylists(): List<Playlist>
    suspend fun getPlaylistById(playlistId: Long): Playlist?
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean
    suspend fun getPlaylistsWithTrack(trackId: Long): List<Playlist>
}