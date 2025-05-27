package com.example.playlist_maker.domain.repository


interface PlayerRepository {
    fun prepare(url: String)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun setOnCompletionListener(listener: () -> Unit)
    fun setOnPreparedListener(listener: () -> Unit)
}
