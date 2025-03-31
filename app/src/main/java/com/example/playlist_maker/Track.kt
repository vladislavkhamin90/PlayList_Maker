package com.example.playlist_maker

import androidx.annotation.Keep

@Keep
data class Track(val trackName: String, val artistName: String, val trackTime: String, val artworkUrl100: String, val id: Int)