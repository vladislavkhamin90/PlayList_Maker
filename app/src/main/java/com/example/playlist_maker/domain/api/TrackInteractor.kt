package com.example.playlist_maker.domain.api

import com.example.playlist_maker.domain.models.Track

interface TrackInteractor {
    fun searchTrack(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
        fun failure()
    }
}