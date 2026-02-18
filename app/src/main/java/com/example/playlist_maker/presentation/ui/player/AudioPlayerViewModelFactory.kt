package com.example.playlist_maker.presentation.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.domain.api.FavoriteTracksInteractor
import com.example.playlist_maker.domain.api.PlaylistInteractor
import com.example.playlist_maker.domain.useCase.PlayerControlUseCase

class AudioPlayerViewModelFactory(
    private val playerControlUseCase: PlayerControlUseCase,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioPlayerViewModel::class.java)) {
            return AudioPlayerViewModel(
                playerControlUseCase,
                favoriteTracksInteractor,
                playlistInteractor
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}