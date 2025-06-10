package com.example.playlist_maker.presentation.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.domain.useCase.PlayerControlUseCase
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(private val playerControlUseCase: PlayerControlUseCase) : ViewModel() {

    sealed class PlayerState {
        object Default : PlayerState()
        object Prepared : PlayerState()
        object Playing : PlayerState()
        object Paused : PlayerState()
    }

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default)
    val playerState: LiveData<PlayerState> = _playerState

    private val _currentPosition = MutableLiveData<String>()
    val currentPosition: LiveData<String> = _currentPosition

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (_playerState.value == PlayerState.Playing) {
                updateCurrentPosition()
                handler.postDelayed(this, 500)
            }
        }
    }

    fun preparePlayer(url: String) {
        playerControlUseCase.prepare(url)
        playerControlUseCase.setOnPreparedListener {
            _playerState.postValue(PlayerState.Prepared)
        }
        playerControlUseCase.setOnCompletionListener {
            _playerState.postValue(PlayerState.Paused)
            handler.removeCallbacks(updateTimeRunnable)
            _currentPosition.postValue(
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(0)
            )
        }
    }

    fun play() {
        playerControlUseCase.play()
        _playerState.value = PlayerState.Playing
        startTimer()
    }

    fun pause() {
        playerControlUseCase.pause()
        _playerState.value = PlayerState.Paused
        stopTimer()
        if (playerControlUseCase.getCurrentPosition() >= playerControlUseCase.getCurrentPosition()) {
            _currentPosition.postValue(
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(0)
            )
        }
    }

    fun release() {
        playerControlUseCase.release()
        handler.removeCallbacks(updateTimeRunnable)
    }

    fun playbackControl() {
        when (_playerState.value) {
            PlayerState.Playing -> pause()
            PlayerState.Prepared, PlayerState.Paused -> play()
            else -> {}
        }
    }

    private fun updateCurrentPosition() {
        val position = playerControlUseCase.getCurrentPosition()
        _currentPosition.postValue(
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(position)
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