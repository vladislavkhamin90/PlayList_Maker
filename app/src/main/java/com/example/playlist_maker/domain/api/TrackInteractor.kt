package com.example.playlist_maker.domain.api

import com.example.playlist_maker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    suspend fun searchTrack(expression: String): Flow<List<Track>>
}