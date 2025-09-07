package com.example.playlist_maker.data.repository

import android.util.Log
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
        Log.d("MyLog", "Deleting playlist with ID: $playlistId")
        val playlist = playlistDao.getPlaylistById(playlistId) ?: return
        val trackIds = gson.fromJson(playlist.trackIds, Array<Long>::class.java)

        playlistDao.deletePlaylist(playlistId)
        Log.d("MyLog", "Playlist deleted from database")

        trackIds.forEach { trackId ->
            cleanupUnusedTracks(trackId)
        }
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

    override suspend fun getPlaylistTracks(playlistId: Long): List<PlaylistTrackEntity> {
        val playlist = playlistDao.getPlaylistById(playlistId) ?: return emptyList()
        val trackIds = gson.fromJson(playlist.trackIds, Array<Long>::class.java)

        return trackIds.mapNotNull { trackId ->
            playlistTrackDao.getTrackById(trackId)
        }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, track: Track) {
        try {
            val playlist = playlistDao.getPlaylistById(playlistId) ?: return

            val currentTrackIds = gson.fromJson(playlist.trackIds, Array<Long>::class.java)
                .toMutableList()

            currentTrackIds.remove(track.trackId.toLong())

            val updatedPlaylist = playlist.copy(
                trackIds = gson.toJson(currentTrackIds),
                trackCount = currentTrackIds.size
            )

            playlistDao.updatePlaylist(updatedPlaylist)

            cleanupUnusedTracks(track.trackId.toLong())
        } catch (e: Exception) {
            Log.e("MyLog", "$e")
        }
    }

    private suspend fun cleanupUnusedTracks(trackId: Long) {
        val allPlaylists = playlistDao.getAllPlaylists().first()
        val isTrackUsed = allPlaylists.any { playlist ->
            val trackIds = gson.fromJson(playlist.trackIds, Array<Long>::class.java)
            trackIds.contains(trackId)
        }

        if (!isTrackUsed) {
            playlistTrackDao.deleteTrack(trackId)
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