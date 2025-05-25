package com.example.playlist_maker.data.repository

import com.example.playlist_maker.domain.repository.PlayerRepository

class PlayerRepositoryImpl(
    private val dataSource: com.example.playlist_maker.data.repository.PlayerRepository
) : PlayerRepository {
    override fun prepare(url: String) = dataSource.prepare(url)
    override fun play() = dataSource.play()
    override fun pause() = dataSource.pause()
    override fun release() = dataSource.release()
    override fun getCurrentPosition(): Int = dataSource.getCurrentPosition()
    override fun isPlaying(): Boolean = dataSource.isPlaying()
    override fun setOnCompletionListener(listener: () -> Unit) {
        dataSource.setOnCompletionListener(listener)
    }
    override fun setOnPreparedListener(listener: () -> Unit) {
        dataSource.setOnPreparedListener(listener)
    }
}