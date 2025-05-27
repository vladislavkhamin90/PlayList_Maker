package com.example.playlist_maker.data.dto

import com.google.gson.annotations.SerializedName

data class TrackDto(
    @SerializedName("trackId") val trackId: Int,
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Int,
    @SerializedName("artworkUrl100") val artworkUrl100: String,
    @SerializedName("collectionName") val collectionName: String?,
    @SerializedName("releaseDate") val releaseDate: String,
    @SerializedName("primaryGenreName") val primaryGenreName: String,
    @SerializedName("country") val country: String,
    @SerializedName("previewUrl") val previewUrl: String?
)