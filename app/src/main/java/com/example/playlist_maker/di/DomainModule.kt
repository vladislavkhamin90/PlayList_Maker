package com.example.playlist_maker.di

import com.example.playlist_maker.domain.api.FavoriteTracksInteractor
import com.example.playlist_maker.domain.api.PlaylistInteractor
import com.example.playlist_maker.domain.api.TrackInteractor
import com.example.playlist_maker.domain.impl.FavoriteTracksInteractorImpl
import com.example.playlist_maker.domain.impl.PlaylistInteractorImpl
import com.example.playlist_maker.domain.impl.TrackInteractorImpl
import com.example.playlist_maker.domain.useCase.PlayerControlUseCase
import com.example.playlist_maker.domain.useCase.ThemeUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.Executors

val domainModule = module {
    single<TrackInteractor> { TrackInteractorImpl(get()) }
    single<FavoriteTracksInteractor> { FavoriteTracksInteractorImpl(get()) }
    single<PlaylistInteractor> { PlaylistInteractorImpl(get()) }

    factory { ThemeUseCase(get(), androidContext()) }

    single { PlayerControlUseCase(get()) }

    single { Executors.newCachedThreadPool() }
}