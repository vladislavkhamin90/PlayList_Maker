package com.example.playlist_maker.data.dto

data class TrackSearchResponse(val resultCount: Int,
                               val results: List<TrackDto>): Response()

