package com.example.playlist_maker.data.repository

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.playlist_maker.domain.repository.PlayerRepository

class PlayerRepositoryImpl(private var mediaPlayer: MediaPlayer): PlayerRepository {
    private var onPreparedListener: (() -> Unit)? = null
    private var onCompletionListener: (() -> Unit)? = null

    override fun prepare(url: String) {
        try {
            release()

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    onPreparedListener?.invoke()
                }
                setOnCompletionListener {
                    onCompletionListener?.invoke()
                    release() // Auto-release after completion
                }
                setOnErrorListener { _, _, _ ->
                    release()
                    false
                }
            }
        } catch (e: Exception) {
            release()
            throw IllegalStateException("Error preparing MediaPlayer: ${e.message}")
        }
    }

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun setOnPreparedListener(listener: () -> Unit) {
        onPreparedListener = listener
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }
}