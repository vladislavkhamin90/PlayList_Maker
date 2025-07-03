package com.example.playlist_maker.domain.useCase

import android.util.Log
import com.example.playlist_maker.domain.repository.PlayerRepository

class PlayerControlUseCase(
    private val repository: PlayerRepository
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
        Log.i("MyLog", "PREPARiiiiing!")
        repository.setOnPreparedListener(listener)
    }
}