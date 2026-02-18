package com.example.playlist_maker.domain.repository

import com.example.playlist_maker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    suspend fun searchTrack(expression: String): Flow<List<Track>>
}