package com.example.playlist_maker.data.repository

import android.content.SharedPreferences
import com.example.playlist_maker.domain.models.Theme
import com.example.playlist_maker.domain.repository.ThemeRepository

class ThemeRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : ThemeRepository {

    override fun getCurrentTheme(): Theme {
        return if (sharedPreferences.getBoolean(DARK_THEME_KEY, false)) {
            Theme.DARK
        } else {
            Theme.LIGHT
        }
    }

    override fun setCurrentTheme(theme: Theme) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, theme == Theme.DARK)
            .apply()
    }

    companion object {
        private const val DARK_THEME_KEY = "dark_theme_enabled"
    }
}