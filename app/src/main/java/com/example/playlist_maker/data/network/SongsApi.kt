package com.example.playlist_maker.data.network

import com.example.playlist_maker.data.dto.TrackSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SongsApi {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): TrackSearchResponse
}