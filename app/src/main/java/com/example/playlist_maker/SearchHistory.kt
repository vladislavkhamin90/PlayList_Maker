package com.example.playlist_maker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val APP_PREF = "APP_PREFS"

class SearchHistory(context: Context) {
    private val sharedPref = context.applicationContext.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val TRACK_HISTORY_KEY = "track_history"
    }

    fun save(tracks: List<Track>) {
        val jsonString = gson.toJson(tracks)
        sharedPref.edit().putString(TRACK_HISTORY_KEY, jsonString).apply()
    }

    fun load(): List<Track> {
        val jsonString = sharedPref.getString(TRACK_HISTORY_KEY, null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(jsonString, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
}