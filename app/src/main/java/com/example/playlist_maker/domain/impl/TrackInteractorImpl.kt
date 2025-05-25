package com.example.playlist_maker.domain.impl

import android.util.Log
import com.example.playlist_maker.domain.api.TrackInteractor
import com.example.playlist_maker.domain.repository.TrackRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    private val executor = Executors.newCachedThreadPool()
    override fun searchTrack(expression: String, consumer: TrackInteractor.TracksConsumer) {
        executor.execute {
            try {
                val results = repository.searchTrack(expression)
                consumer.consume(results)
            } catch (e: Exception) {
                Log.e("SEARCH!!", "Error searching tracks", e)
                consumer.failure()
            }
        }
    }
}