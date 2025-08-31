package com.example.playlist_maker.presentation.ui.player

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.domain.api.FavoriteTracksInteractor
import com.example.playlist_maker.domain.api.PlaylistInteractor
import com.example.playlist_maker.domain.models.Playlist
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.useCase.PlayerControlUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val playerControlUseCase: PlayerControlUseCase,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    data class PlayerState(
        val status: Status,
        val currentPosition: String = "00:00",
        val isFavorite: Boolean = false
    ) {
        enum class Status {
            DEFAULT, PREPARED, PLAYING, PAUSED
        }
    }

    private var currentTrack: Track? = null
    private val _playerState = MutableLiveData(PlayerState(PlayerState.Status.DEFAULT, isFavorite = false))
    val playerState: LiveData<PlayerState> = _playerState

    private var updatePositionJob: Job? = null
    private var currentUrl: String? = null

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists


    private val _addToPlaylistResult = MutableLiveData<AddToPlaylistResult>()
    val addToPlaylistResult: LiveData<AddToPlaylistResult> = _addToPlaylistResult

    private val _playlistTrackInfo = MutableLiveData<Map<Long, Boolean>>()

    fun setTrack(track: Track) {
        currentTrack = track
        _playerState.postValue(
            PlayerState(
                status = PlayerState.Status.DEFAULT,
                isFavorite = track.isFavorite,
                currentPosition = "00:00"
            )
        )
        viewModelScope.launch {
            val actualFavorite = favoriteTracksInteractor.isFavorite(track.trackId)
            if (track.isFavorite != actualFavorite) {
                track.isFavorite = actualFavorite
                _playerState.postValue(
                    _playerState.value?.copy(isFavorite = actualFavorite)
                )
            }
        }
    }

    fun onFavoriteClicked() {
        currentTrack?.let { track ->
            viewModelScope.launch {
                try {
                    val newFavoriteState = !track.isFavorite
                    _playerState.postValue(
                        _playerState.value?.copy(isFavorite = newFavoriteState)
                    )
                    favoriteTracksInteractor.toggleFavorite(track)
                    track.isFavorite = newFavoriteState
                } catch (e: Exception) {
                    _playerState.postValue(
                        _playerState.value?.copy(isFavorite = track.isFavorite)
                    )
                    Log.e("MyLog", "$e")
                }
            }
        }
    }

    fun preparePlayer(url: String) {
        if (currentUrl == url && playerState.value?.status == PlayerState.Status.PREPARED) {
            return
        }

        currentUrl = url
        stopPositionUpdates()

        try {
            playerControlUseCase.prepare(url)
            playerControlUseCase.setOnPreparedListener {
                _playerState.postValue(
                    _playerState.value?.copy(status = PlayerState.Status.PREPARED)
                        ?: PlayerState(PlayerState.Status.PREPARED, isFavorite = currentTrack?.isFavorite ?: false)
                )
            }
            playerControlUseCase.setOnCompletionListener {
                _playerState.postValue(
                    _playerState.value?.copy(status = PlayerState.Status.PAUSED, currentPosition = "00:00")
                        ?: PlayerState(PlayerState.Status.PAUSED, "00:00", currentTrack?.isFavorite ?: false)
                )
                stopPositionUpdates()
            }
        } catch (e: Exception) {
            _playerState.postValue(
                _playerState.value?.copy(status = PlayerState.Status.DEFAULT)
                    ?: PlayerState(PlayerState.Status.DEFAULT, isFavorite = currentTrack?.isFavorite ?: false)
            )
        }
    }

    fun play() {
        playerControlUseCase.play()
        _playerState.value = PlayerState(PlayerState.Status.PLAYING)
        startPositionUpdates()
    }

    fun pause() {
        if (playerState.value?.status == PlayerState.Status.PLAYING) {
            playerControlUseCase.pause()
            _playerState.value = PlayerState(PlayerState.Status.PAUSED)
            stopPositionUpdates()
        }
    }

    fun release() {
        playerControlUseCase.release()
        stopPositionUpdates()
    }

    fun playbackControl() {
        when (_playerState.value?.status) {
            PlayerState.Status.PLAYING -> pause()
            PlayerState.Status.PREPARED, PlayerState.Status.PAUSED -> play()
            else -> {}
        }
    }

    private fun startPositionUpdates() {
        stopPositionUpdates()
        updatePositionJob = viewModelScope.launch {
            while (isActive) {
                updateCurrentPosition()
                delay(UPDATE_TIME)
            }
        }
    }

    private fun stopPositionUpdates() {
        updatePositionJob?.cancel()
        updatePositionJob = null
    }

    private fun updateCurrentPosition() {
        val position = playerControlUseCase.getCurrentPosition()
        _playerState.postValue(
            _playerState.value?.copy(
                currentPosition = SimpleDateFormat("mm:ss", Locale.getDefault()).format(position)
            )
        )
    }

    fun loadPlaylists() {
        currentTrack?.let { track ->
            viewModelScope.launch {
                try {
                    val playlistsWithTrack = playlistInteractor.getPlaylistsWithTrack(track.trackId.toLong())
                    val allPlaylists = playlistInteractor.getAllPlaylists()

                    val trackInfoMap = allPlaylists.associate { playlist ->
                        playlist.id to playlistsWithTrack.any { it.id == playlist.id }
                    }

                    _playlists.postValue(allPlaylists)
                    _playlistTrackInfo.postValue(trackInfoMap)
                } catch (e: Exception) {
                    Log.e("MyLog", "Error: $e")
                }
            }
        }
    }

    fun addTrackToPlaylist(playlistId: Long) {
        currentTrack?.let { track ->
            viewModelScope.launch {
                try {
                    val success = playlistInteractor.addTrackToPlaylist(playlistId, track)
                    if (success) {
                        _addToPlaylistResult.postValue(AddToPlaylistResult.Success(playlistId))
                    } else {
                        _addToPlaylistResult.postValue(AddToPlaylistResult.AlreadyExists(playlistId))
                    }
                    loadPlaylists()
                } catch (e: Exception) {
                    Log.e("MyLog", "Error: $e")
                }
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        release()
    }

    companion object {
        private const val UPDATE_TIME = 300L
    }

    sealed class AddToPlaylistResult {
        data class Success(val playlistId: Long) : AddToPlaylistResult()
        data class AlreadyExists(val playlistId: Long) : AddToPlaylistResult()
        data class Error(val message: String) : AddToPlaylistResult()
    }
}