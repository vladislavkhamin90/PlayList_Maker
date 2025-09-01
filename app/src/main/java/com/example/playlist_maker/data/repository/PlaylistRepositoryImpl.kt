package com.example.playlist_maker.data.repository

import com.example.playlist_maker.data.db.PlaylistDao
import com.example.playlist_maker.data.db.PlaylistEntity
import com.example.playlist_maker.data.db.PlaylistTrackDao
import com.example.playlist_maker.data.db.PlaylistTrackEntity
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.repository.PlaylistRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverImagePath: String?
    ): Long {
        val playlist = PlaylistEntity(
            name = name,
            description = description,
            coverImagePath = coverImagePath,
            trackIds = gson.toJson(emptyList<Long>()),
            trackCount = 0
        )
        return playlistDao.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: PlaylistEntity) {
        playlistDao.updatePlaylist(playlist)
    }

    override suspend fun getPlaylistById(playlistId: Long): PlaylistEntity? {
        return playlistDao.getPlaylistById(playlistId)
    }

    override fun getAllPlaylists(): Flow<List<PlaylistEntity>> {
        return playlistDao.getAllPlaylists()
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean {
        return try {
            val playlist = playlistDao.getPlaylistById(playlistId) ?: return false

            val currentTrackIds = gson.fromJson(playlist.trackIds, Array<Long>::class.java).toMutableList()

            if (currentTrackIds.contains(track.trackId.toLong())) {
                return false
            }

            currentTrackIds.add(track.trackId.toLong())

            playlistTrackDao.insertTrack(track.toPlaylistTrackEntity())

            val updatedPlaylist = playlist.copy(
                trackIds = gson.toJson(currentTrackIds),
                trackCount = currentTrackIds.size
            )

            playlistDao.updatePlaylist(updatedPlaylist)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getPlaylistsWithTrack(trackId: Long): List<PlaylistEntity> {
        val allPlaylists = playlistDao.getAllPlaylists().first()
        return allPlaylists.filter { playlist ->
            val trackIds = gson.fromJson(playlist.trackIds, Array<Long>::class.java)
            trackIds.contains(trackId)
        }
    }
}

private fun Track.toPlaylistTrackEntity(): PlaylistTrackEntity {
    return PlaylistTrackEntity(
        trackId = this.trackId.toLong(),
        trackName = this.trackName,
        artistName = this.artistName,
        trackTime = this.trackTimeMillis,
        artworkUrl = this.artworkUrl100,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        previewUrl = this.previewUrl
    )
}
