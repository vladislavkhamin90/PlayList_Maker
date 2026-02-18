package com.example.playlist_maker.presentation.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.domain.api.PlaylistInteractor
import com.example.playlist_maker.domain.models.Playlist
import com.example.playlist_maker.domain.models.Track
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist?>()
    val playlist: LiveData<Playlist?> = _playlist

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _totalDuration = MutableLiveData<Long>(0)
    val totalDuration: LiveData<Long> = _totalDuration

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            _playlist.value = playlist

            val tracks = playlistInteractor.getPlaylistTracks(playlistId)
            _tracks.value = tracks

            val duration = calculateTotalDuration(tracks)
            _totalDuration.value = duration
        }
    }

    private fun calculateTotalDuration(tracks: List<Track>): Long {
        return tracks.sumOf { track ->
            try {
                val parts = track.trackTimeMillis.split(":")
                when (parts.size) {
                    2 -> {
                        val minutes = parts[0].toLong()
                        val seconds = parts[1].toLong()
                        (minutes * 60 + seconds) * 1000
                    }
                    1 -> {
                        parts[0].toLong() * 60 * 1000
                    }
                    else -> 0L
                }
            } catch (e: Exception) {
                0L
            }
        }
    }

    fun removeTrackFromPlaylist(playlistId: Long, track: Track) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(playlistId, track)
            loadPlaylist(playlistId)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistId)
        }
    }
}