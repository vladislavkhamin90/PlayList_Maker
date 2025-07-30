package com.example.playlist_maker.presentation.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.domain.useCase.PlayerControlUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(private val playerControlUseCase: PlayerControlUseCase) : ViewModel() {

    data class PlayerState(
        val status: Status,
        val currentPosition: String = "00:00"
    ) {
        enum class Status {
            DEFAULT, PREPARED, PLAYING, PAUSED
        }
    }

    private val _playerState = MutableLiveData(PlayerState(PlayerState.Status.DEFAULT))
    val playerState: LiveData<PlayerState> = _playerState

    private var updatePositionJob: Job? = null
    private var currentUrl: String? = null

    fun preparePlayer(url: String) {
        if (currentUrl == url && playerState.value?.status == PlayerState.Status.PREPARED) {
            return
        }

        currentUrl = url
        _playerState.value = PlayerState(PlayerState.Status.DEFAULT)
        stopPositionUpdates()

        try {
            playerControlUseCase.prepare(url)
            playerControlUseCase.setOnPreparedListener {
                _playerState.postValue(PlayerState(PlayerState.Status.PREPARED))
            }
            playerControlUseCase.setOnCompletionListener {
                _playerState.postValue(PlayerState(PlayerState.Status.PAUSED, "00:00"))
                stopPositionUpdates()
            }
        } catch (e: Exception) {
            _playerState.postValue(PlayerState(PlayerState.Status.DEFAULT))
        }
    }

    fun play() {
        playerControlUseCase.play()
        _playerState.value = PlayerState(PlayerState.Status.PLAYING)
        startPositionUpdates()
    }

    fun pause() {
        playerControlUseCase.pause()
        _playerState.value = PlayerState(PlayerState.Status.PAUSED)
        stopPositionUpdates()
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
                delay(300)
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

    override fun onCleared() {
        super.onCleared()
        release()
    }
}