package com.example.playlist_maker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.R
import com.example.playlist_maker.domain.models.Theme
import com.example.playlist_maker.domain.useCase.ThemeUseCase

class SettingsViewModel(private val themeUseCase: ThemeUseCase): ViewModel() {

    fun thumbChecked():Boolean{
        return themeUseCase.getTheme() == Theme.DARK
    }

    fun setTheme(theme: Theme){
        themeUseCase.setTheme(theme)
    }

    fun getDarkTheme(): Theme {
        return Theme.DARK
    }

    fun getLightTheme(): Theme {
        return Theme.LIGHT
    }

    fun getUrlIntent():Intent{
        val url = Uri.parse(R.string.offer.toString())
        return Intent(Intent.ACTION_VIEW, url)
    }

    fun getOpenUrlString():String{
        return R.string.open_url.toString()
    }

    fun getIntentUrl():String{
        return R.string.send.toString()
    }

    fun getIntentEmail(): Intent{
        val intent = Intent(Intent.ACTION_SEND)
        val mail = R.string.mail
        val mailto = R.string.mailto.toString()
        val textPlain = R.string.text_plain.toString()
        val title = R.string.title
        val text = R.string.text
        intent.putExtra(Intent.EXTRA_EMAIL, mail)
        intent.putExtra(Intent.EXTRA_SUBJECT, title)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.data = Uri.parse(mailto)
        intent.type = textPlain
        return intent
    }

    fun getShareIntent():Intent{
        val url = Uri.parse(R.string.url_android_dev.toString())
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, url)
        return intent
    }

    fun getShareString():String{
        return R.string.share_app.toString()
    }
}