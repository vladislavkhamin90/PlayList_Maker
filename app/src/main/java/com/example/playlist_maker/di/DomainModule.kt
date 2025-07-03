package com.example.playlist_maker.di

import com.example.playlist_maker.domain.api.TrackInteractor
import com.example.playlist_maker.domain.impl.TracksInteractorImpl
import com.example.playlist_maker.domain.useCase.PlayerControlUseCase
import com.example.playlist_maker.domain.useCase.ThemeUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.Executors

val domainModule = module {
    single<TrackInteractor> { TracksInteractorImpl(get()) }

    factory { ThemeUseCase(repository = get(), context = androidContext()) }

    single { PlayerControlUseCase(get()) }

    single { Executors.newCachedThreadPool() }
}