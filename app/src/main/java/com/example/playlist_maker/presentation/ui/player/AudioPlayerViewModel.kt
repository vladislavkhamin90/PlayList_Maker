package com.example.playlist_maker.presentation.ui.player

import androidx.lifecycle.ViewModel
import com.example.playlist_maker.domain.useCase.PlayerControlUseCase

class AudioPlayerViewModel(private val playerControlUseCase: PlayerControlUseCase) : ViewModel() {

    private var currentPosition = 0
    private var isPlaying = false

    fun preparePlayer(url: String) {
        playerControlUseCase.prepare(url)
        playerControlUseCase.setOnPreparedListener {
            isPlaying = false
        }
        playerControlUseCase.setOnCompletionListener {
            isPlaying = false
            currentPosition = 0
        }
    }

    fun play() {
        playerControlUseCase.play()
        isPlaying = true
    }

    fun pause() {
        playerControlUseCase.pause()
        isPlaying = false
    }

    fun release() {
        playerControlUseCase.release()
    }

    fun getCurrentPosition(): Int {
        currentPosition = playerControlUseCase.getCurrentPosition()
        return currentPosition
    }

    fun isPlaying(): Boolean = isPlaying
}