package com.example.playlist_maker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val THEME_KEY = "THEME_KEY"
const val THEME_PREFERENCES = "THEME_PREFERENCES"
const val DEFAULT_THEME = false

class App : Application() {
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        applySavedTheme()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        sharedPrefs.edit().putBoolean(THEME_KEY, darkThemeEnabled).apply()
        applyTheme(darkThemeEnabled)
    }

    fun applySavedTheme() {
        val darkTheme = sharedPrefs.getBoolean(THEME_KEY, DEFAULT_THEME)
        applyTheme(darkTheme)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun isDarkTheme(): Boolean = sharedPrefs.getBoolean(THEME_KEY, DEFAULT_THEME)
}