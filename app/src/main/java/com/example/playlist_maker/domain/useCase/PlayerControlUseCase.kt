package com.example.playlist_maker.domain.useCase

import com.example.playlist_maker.data.repository.PlayerRepositoryImpl

class PlayerControlUseCase(
    private val repository: PlayerRepositoryImpl
) {
    fun prepare(url: String) = repository.prepare(url)
    fun play() = repository.play()
    fun pause() = repository.pause()
    fun release() = repository.release()
    fun getCurrentPosition(): Int = repository.getCurrentPosition()
    fun isPlaying(): Boolean = repository.isPlaying()
    fun setOnCompletionListener(listener: () -> Unit) {
        repository.setOnCompletionListener(listener)
    }
    fun setOnPreparedListener(listener: () -> Unit) {
        repository.setOnCompletionListener(listener)
    }
}