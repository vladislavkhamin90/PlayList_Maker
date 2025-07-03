package com.example.playlist_maker.creator

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlist_maker.data.network.RetrofitNetworkClient
import com.example.playlist_maker.data.repository.PlayerRepositoryImpl
import com.example.playlist_maker.data.repository.ThemeRepositoryImpl
import com.example.playlist_maker.data.repository.TrackRepositoryImpl
import com.example.playlist_maker.domain.api.TrackInteractor
import com.example.playlist_maker.domain.impl.TracksInteractorImpl
import com.example.playlist_maker.domain.models.Theme
import com.example.playlist_maker.domain.repository.TrackRepository
import com.example.playlist_maker.domain.useCase.PlayerControlUseCase
import com.example.playlist_maker.domain.useCase.ThemeUseCase

object Creator {
    private lateinit var appContext: Context

    fun init(application: Application) {
        appContext = application.applicationContext
        applyAppTheme()
    }

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TracksInteractorImpl(getTrackRepository())
    }

    private fun getThemeRepository(): ThemeRepositoryImpl {
        return ThemeRepositoryImpl(
            appContext.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
        )
    }

    fun provideThemeUseCase(): ThemeUseCase {
        return ThemeUseCase(getThemeRepository(), appContext)
    }

    private fun applyAppTheme() {
        val themeUseCase = provideThemeUseCase()
        AppCompatDelegate.setDefaultNightMode(
            when (themeUseCase.getTheme()) {
                Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun providePlayerControlUseCase(): PlayerControlUseCase {
        return PlayerControlUseCase(PlayerRepositoryImpl(MediaPlayer()))
    }
}