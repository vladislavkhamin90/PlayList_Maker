package com.example.playlist_maker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.R
import com.example.playlist_maker.domain.models.Theme
import com.example.playlist_maker.domain.useCase.ThemeUseCase

class SettingsViewModel(private val themeUseCase: ThemeUseCase) : ViewModel() {
    private val _actionEvent = SingleLiveEvent<Intent>()
    val actionEvent: LiveData<Intent> = _actionEvent

    fun isDarkThemeEnabled(): Boolean {
        return themeUseCase.getTheme() == Theme.DARK
    }

    fun onThemeSwitched(isChecked: Boolean) {
        val theme = if (isChecked) Theme.DARK else Theme.LIGHT
        themeUseCase.setTheme(theme)
        applyTheme(theme)
    }

    fun onShareClicked() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.url_android_dev))
        }
        _actionEvent.postValue(intent)
    }

    fun onSupportClicked() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.title))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.text))
            data = Uri.parse("mailto:")
        }
        _actionEvent.postValue(intent)
    }

    fun onAgreementClicked() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(getString(R.string.offer))
        }
        _actionEvent.postValue(intent)
    }

    private fun getString(resId: Int): String {
        return themeUseCase.getContext().getString(resId)
    }

    private fun applyTheme(theme: Theme) {
        val mode = when (theme) {
            Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun initializeTheme() {
        applyTheme(themeUseCase.getTheme())
    }
}