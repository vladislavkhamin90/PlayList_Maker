package com.example.playlist_maker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlist_maker.data.repository.ThemeRepositoryImpl
import com.example.playlist_maker.domain.models.Theme
import com.example.playlist_maker.domain.useCase.ThemeUseCase

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val themeRepository = ThemeRepositoryImpl(
            getSharedPreferences("app_theme", MODE_PRIVATE)
        )

        val themeUseCase = ThemeUseCase(themeRepository)

        AppCompatDelegate.setDefaultNightMode(
            when (themeUseCase.getTheme()) {
                Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}