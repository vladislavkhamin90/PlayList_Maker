package com.example.playlist_maker

import com.google.gson.annotations.SerializedName


class Songs(val results: List<SongsResult>)


data class SongsResult(
    @SerializedName("trackName")val trackName: String,
    @SerializedName("artistName")val artistName: String,
    @SerializedName("trackTimeMillis")val trackTimeMillis: Int,
    @SerializedName("artworkUrl100")val artworkUrl100: String,
    @SerializedName("trackId")val id: Int,
    @SerializedName("collectionName")val collectionName: String,
    @SerializedName("releaseDate")val releaseDate: String,
    @SerializedName("primaryGenreName")val primaryGenreName: String,
    @SerializedName("country")val country: String,
    @SerializedName("previewUrl")val previewUrl: String
)