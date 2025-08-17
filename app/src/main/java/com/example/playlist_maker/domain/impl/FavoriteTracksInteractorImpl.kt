package com.example.playlist_maker.domain.impl

import com.example.playlist_maker.domain.api.FavoriteTracksInteractor
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override suspend fun toggleFavorite(track: Track) {
        if (track.isFavorite) {
            repository.removeFromFavorites(track)
        } else {
            repository.addToFavorites(track)
        }
        track.isFavorite = !track.isFavorite
    }

    override fun getFavorites(): Flow<List<Track>> {
        return repository.getAllFavorites()
    }

    override suspend fun isFavorite(trackId: Int): Boolean {
        return try {
            repository.getAllFavorites()
                .first()
                .any { it.trackId == trackId }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}