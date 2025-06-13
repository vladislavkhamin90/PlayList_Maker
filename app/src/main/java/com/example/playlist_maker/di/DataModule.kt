package com.example.playlist_maker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlist_maker.data.network.NetworkClient
import com.example.playlist_maker.data.network.RetrofitNetworkClient
import com.example.playlist_maker.data.network.SongsApi
import com.example.playlist_maker.data.repository.PlayerRepositoryImpl
import com.example.playlist_maker.data.repository.ThemeRepositoryImpl
import com.example.playlist_maker.data.repository.TrackRepositoryImpl
import com.example.playlist_maker.data.sharedprefs.SearchHistory
import com.example.playlist_maker.domain.repository.PlayerRepository
import com.example.playlist_maker.domain.repository.ThemeRepository
import com.example.playlist_maker.domain.repository.TrackRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<SongsApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SongsApi::class.java)
    }

    single<NetworkClient> { RetrofitNetworkClient() }

    single<SharedPreferences> {
        androidContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    single<TrackRepository> { TrackRepositoryImpl(get()) }

    single<ThemeRepository> { ThemeRepositoryImpl(get()) }

    single<PlayerRepository> { PlayerRepositoryImpl(get()) }

    single { MediaPlayer() }

    single { Gson() }

    single { SearchHistory(androidContext()) }

}