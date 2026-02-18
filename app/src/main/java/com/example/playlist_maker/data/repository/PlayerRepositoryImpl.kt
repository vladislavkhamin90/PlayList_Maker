package com.example.playlist_maker.data.repository

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.playlist_maker.domain.repository.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {
    private var mediaPlayer: MediaPlayer? = null
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
                    release()
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
        mediaPlayer?.start()
    }

    override fun pause() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: IllegalStateException) {
            release()
        }
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun setOnPreparedListener(listener: () -> Unit) {
        onPreparedListener = listener
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = {
            try {
                listener.invoke()
            } finally {
                release()
            }
        }
        mediaPlayer?.setOnCompletionListener { onCompletionListener?.invoke() }
    }
}