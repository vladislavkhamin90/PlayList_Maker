package com.example.playlist_maker.data.repository

import com.example.playlist_maker.data.dto.TrackSearchRequest
import com.example.playlist_maker.data.network.NetworkClient
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTrack(expression: String): Flow<List<Track>> {
        return networkClient.doRequest(TrackSearchRequest(expression))
            .map { response ->
                if (response.resultCode == 200) {
                    (response as com.example.playlist_maker.data.dto.TrackSearchResponse).results.map { dto ->
                        Track(
                            dto.trackId,
                            dto.trackName,
                            dto.artistName,
                            SimpleDateFormat("mm:ss", Locale.getDefault()).format(dto.trackTimeMillis),
                            dto.artworkUrl100,
                            dto.collectionName,
                            dto.releaseDate,
                            dto.primaryGenreName,
                            dto.country,
                            dto.previewUrl
                        )
                    }
                } else {
                    emptyList()
                }
            }
    }
}