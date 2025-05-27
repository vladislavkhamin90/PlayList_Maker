package com.example.playlist_maker

import com.example.playlist_maker.data.network.RetrofitNetworkClient
import com.example.playlist_maker.data.repository.TrackRepositoryImpl
import com.example.playlist_maker.domain.api.TrackInteractor
import com.example.playlist_maker.domain.impl.TracksInteractorImpl
import com.example.playlist_maker.domain.repository.TrackRepository

object Creator {
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor{
        return TracksInteractorImpl(getTrackRepository())
    }
}