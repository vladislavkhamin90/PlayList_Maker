package com.example.playlist_maker.presentation

import android.app.Application
import com.example.playlist_maker.Creator

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.applyAppTheme(this)
    }
}