package com.example.playlist_maker

import com.google.gson.annotations.SerializedName


class Songs(val results: List<SongsResult>)


data class SongsResult(
    @SerializedName("trackName")val trackName: String,
    @SerializedName("artistName")val artistName: String,
    @SerializedName("trackTimeMillis")val trackTimeMillis: Int,
    @SerializedName("artworkUrl100")val artworkUrl100: String,
    @SerializedName("trackId")val id: Int
)