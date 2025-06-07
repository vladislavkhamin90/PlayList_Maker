package com.example.playlist_maker.presentation.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.data.repository.ThemeRepositoryImpl
import com.example.playlist_maker.domain.useCase.ThemeUseCase

class SettingsViewModelFactory(private val context: Context): ViewModelProvider.Factory {

    private fun provideThemeUseCase(context: Context): ThemeUseCase {
        return ThemeUseCase(getThemeRepository(context))
    }

    private fun getThemeRepository(context: Context): ThemeRepositoryImpl {
        return ThemeRepositoryImpl(
            context.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(provideThemeUseCase(context)) as T
    }
}