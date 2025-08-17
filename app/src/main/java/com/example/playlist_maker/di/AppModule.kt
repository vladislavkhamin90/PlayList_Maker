package com.example.playlist_maker.di

import android.content.Intent
import com.example.playlist_maker.data.sharedprefs.SearchHistory
import com.example.playlist_maker.domain.api.FavoriteTracksInteractor
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.presentation.TrackAdapter
import com.example.playlist_maker.presentation.ui.media.FavoriteTracksViewModel
import com.example.playlist_maker.presentation.ui.media.MediaViewModel
import com.example.playlist_maker.presentation.ui.media.PlaylistViewModel
import com.example.playlist_maker.presentation.ui.player.AudioPlayerViewModel
import com.example.playlist_maker.presentation.ui.player.AudioPlayerViewModelFactory
import com.example.playlist_maker.presentation.ui.search.SearchViewModel
import com.example.playlist_maker.presentation.ui.search.SearchViewModelFactory
import com.example.playlist_maker.presentation.ui.settings.SettingsViewModel
import com.example.playlist_maker.presentation.ui.settings.SettingsViewModelFactory
import com.example.playlist_maker.presentation.ui.settings.SingleLiveEvent
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { AudioPlayerViewModel(get(), get<FavoriteTracksInteractor>()) }

    viewModel { FavoriteTracksViewModel(get()) }

    viewModel { SearchViewModel(get(), get()) }

    viewModel { SettingsViewModel(get()) }

    viewModel { MediaViewModel() }

    viewModel { PlaylistViewModel() }

    factory { AudioPlayerViewModelFactory(get(), get()) }

    factory { SearchViewModelFactory(get(), androidContext()) }

    factory { SettingsViewModelFactory(androidContext()) }

    factory { (tracks: List<Track>, onItemClicked: (Track) -> Unit) ->
        TrackAdapter(tracks, onItemClicked)
    }

    single { SearchHistory(androidContext()) }

    single { SingleLiveEvent<Intent>() }
}