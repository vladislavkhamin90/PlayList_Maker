package com.example.playlist_maker.domain.useCase

import com.example.playlist_maker.domain.models.Theme
import com.example.playlist_maker.domain.repository.ThemeRepository

class ThemeUseCase(private val repository: ThemeRepository) {
    fun getTheme(): Theme = repository.getCurrentTheme()
    fun setTheme(theme: Theme) = repository.setCurrentTheme(theme)
}