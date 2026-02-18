package com.example.playlist_maker.domain.api

import com.example.playlist_maker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun toggleFavorite(track: Track)
    fun getFavorites(): Flow<List<Track>>
    suspend fun isFavorite(trackId: Int): Boolean
}