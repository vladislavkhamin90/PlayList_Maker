package com.example.playlist_maker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(context: Context) {
    private val sharedPref = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val TRACK_HISTORY_KEY = "track_history"
    }

    fun saveTrackHistory(tracks: List<Track>) {
        val jsonString = gson.toJson(tracks)
        sharedPref.edit().putString(TRACK_HISTORY_KEY, jsonString).apply()
    }

    fun loadTrackHistory(): List<Track> {
        val jsonString = sharedPref.getString(TRACK_HISTORY_KEY, null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(jsonString, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
}