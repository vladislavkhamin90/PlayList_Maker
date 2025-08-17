package com.example.playlist_maker.data.repository

import com.example.playlist_maker.data.db.FavoriteTrackEntity
import com.example.playlist_maker.data.db.FavoriteTracksDao
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val favoriteTracksDao: FavoriteTracksDao
) : FavoriteTracksRepository {

    override suspend fun addToFavorites(track: Track) {
        val favoriteIds = favoriteTracksDao.getAllIds()
        if (!favoriteIds.contains(track.trackId)) {
            favoriteTracksDao.insert(track.toEntity())
        }
    }

    override suspend fun removeFromFavorites(track: Track) {
        favoriteTracksDao.delete(track.toEntity())
    }

    override fun getAllFavorites(): Flow<List<Track>> {
        return favoriteTracksDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun Track.toEntity(): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            trackId = trackId,
            artworkUrl100 = artworkUrl100,
            trackName = trackName,
            artistName = artistName,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            trackTimeMillis = trackTimeMillis,
            previewUrl = previewUrl
        )
    }

    private fun FavoriteTrackEntity.toDomain(): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl,
            isFavorite = true
        )
    }
}