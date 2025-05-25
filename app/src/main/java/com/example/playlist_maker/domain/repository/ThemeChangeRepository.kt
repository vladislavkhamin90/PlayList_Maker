package com.example.playlist_maker.domain.repository

import com.example.playlist_maker.domain.models.Theme

interface ThemeRepository {
    fun getCurrentTheme(): Theme
    fun setCurrentTheme(theme: Theme)
}