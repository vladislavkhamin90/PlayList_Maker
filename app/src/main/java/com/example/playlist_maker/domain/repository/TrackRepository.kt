package com.example.playlist_maker.domain.repository

import com.example.playlist_maker.domain.models.Track

interface TrackRepository {
    fun searchTrack(expression: String): List<Track>
}