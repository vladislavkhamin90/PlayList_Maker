package com.example.playlist_maker.domain.impl

import android.util.Log
import com.example.playlist_maker.domain.api.TrackInteractor
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class TrackInteractorImpl @Inject constructor(
    private val repository: TrackRepository
) : TrackInteractor {
    override suspend fun searchTrack(expression: String): Flow<List<Track>> {
        return repository.searchTrack(expression)
            .catch { e ->
                Log.e("TrackInteractor", "Error searching tracks", e)
                emit(emptyList())
            }
    }
}