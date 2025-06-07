package com.example.playlist_maker.presentation.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.domain.useCase.PlayerControlUseCase

class AudioPlayerViewModelFactory(
    private val playerControlUseCase: PlayerControlUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioPlayerViewModel::class.java)) {
            return AudioPlayerViewModel(playerControlUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}