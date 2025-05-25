package com.example.playlist_maker.data.repository

import com.example.playlist_maker.data.dto.TrackSearchRequest
import com.example.playlist_maker.data.dto.TrackSearchResponse
import com.example.playlist_maker.data.network.NetworkClient
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.repository.TrackRepository
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTrack(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)
                    .toString(),
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                ) }
        } else {
            return emptyList()
        }
    }
}