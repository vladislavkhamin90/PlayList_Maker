package com.example.playlist_maker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SongsApi {
    @GET("/search?entity=song&")
    fun search(@Query("term") text: String): Call<Songs>
}