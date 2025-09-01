package com.example.playlist_maker.domain.impl

import com.example.playlist_maker.domain.api.PlaylistInteractor
import com.example.playlist_maker.domain.models.Playlist
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverImagePath: String?
    ): Long {
        return playlistRepository.createPlaylist(name, description, coverImagePath)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistRepository.getPlaylistById(playlistId)?.toDomain()
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = playlist.toEntity()
        playlistRepository.updatePlaylist(entity)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistRepository.deletePlaylist(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean {
        return playlistRepository.addTrackToPlaylist(playlistId, track)
    }

    override suspend fun getPlaylistsWithTrack(trackId: Long): List<Playlist> {
        return playlistRepository.getPlaylistsWithTrack(trackId).map { it.toDomain() }
    }
}

private fun com.example.playlist_maker.data.db.PlaylistEntity.toDomain(): Playlist {
    return Playlist(
        id = this.playlistId,
        name = this.name,
        description = this.description,
        coverImagePath = this.coverImagePath,
        trackCount = this.trackCount,
        hasTrack = false
    )
}

private fun Playlist.toEntity(): com.example.playlist_maker.data.db.PlaylistEntity {
    return com.example.playlist_maker.data.db.PlaylistEntity(
        playlistId = this.id,
        name = this.name,
        description = this.description,
        coverImagePath = this.coverImagePath,
        trackIds = "[]",
        trackCount = this.trackCount
    )
}