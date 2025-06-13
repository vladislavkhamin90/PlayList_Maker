package com.example.playlist_maker.presentation.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.domain.useCase.PlayerControlUseCase
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

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (_playerState.value?.status == PlayerState.Status.PLAYING) {
                updateCurrentPosition()
                handler.postDelayed(this, 500)
            }
        }
    }

    private var currentUrl: String? = null

    fun preparePlayer(url: String) {
        if (currentUrl == url && playerState.value?.status == PlayerState.Status.PREPARED) {
            return
        }

        currentUrl = url
        _playerState.value = PlayerState(PlayerState.Status.DEFAULT)

        try {
            playerControlUseCase.prepare(url)
            playerControlUseCase.setOnPreparedListener {
                _playerState.postValue(PlayerState(PlayerState.Status.PREPARED))
            }
            playerControlUseCase.setOnCompletionListener {
                _playerState.postValue(PlayerState(PlayerState.Status.PAUSED, "00:00"))
                handler.removeCallbacks(updateTimeRunnable)
            }
        } catch (e: Exception) {
            _playerState.postValue(PlayerState(PlayerState.Status.DEFAULT))
        }
    }

    fun play() {
        playerControlUseCase.play()
        _playerState.value = PlayerState(PlayerState.Status.PLAYING)
        startTimer()
    }

    fun pause() {
        playerControlUseCase.pause()
        _playerState.value = PlayerState(PlayerState.Status.PAUSED)
        stopTimer()
    }

    fun release() {
        playerControlUseCase.release()
        handler.removeCallbacks(updateTimeRunnable)
    }

    fun playbackControl() {
        when (_playerState.value?.status) {
            PlayerState.Status.PLAYING -> pause()
            PlayerState.Status.PREPARED, PlayerState.Status.PAUSED -> play()
            else -> {}
        }
    }

    private fun updateCurrentPosition() {
        val position = playerControlUseCase.getCurrentPosition()
        _playerState.postValue(
            _playerState.value?.copy(
                currentPosition = SimpleDateFormat("mm:ss", Locale.getDefault()).format(position)
            )
        )
    }

    private fun startTimer() {
        handler.post(updateTimeRunnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(updateTimeRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }
}