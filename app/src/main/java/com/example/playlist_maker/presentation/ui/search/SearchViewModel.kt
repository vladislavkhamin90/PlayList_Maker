package com.example.playlist_maker.presentation.ui.search

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.data.sharedprefs.SearchHistory
import com.example.playlist_maker.domain.api.TrackInteractor
import com.example.playlist_maker.domain.models.Track

class SearchViewModel(
    private val interactor: TrackInteractor,
    private val searchHistory: SearchHistory
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search(inputEditText) }
    private var inputEditText: String = ""

    init {
        loadHistory()
    }

    private fun loadHistory() {
        val history = searchHistory.load()
        _state.value = if (history.isEmpty()) {
            SearchState.HistoryEmpty
        } else {
            SearchState.HistoryContent(history)
        }
    }

    fun searchDebounced(query: String) {
        inputEditText = query
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun search(query: String) {
        inputEditText = query
        if (query.isEmpty()) {
            val history = searchHistory.load()
            _state.value = if (history.isEmpty()) {
                SearchState.HistoryEmpty
            } else {
                SearchState.HistoryContent(history)
            }
            return
        }

        _state.value = SearchState.Loading

        interactor.searchTrack(query, object : TrackInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                _state.postValue(
                    if (foundTracks.isEmpty()) {
                        SearchState.Error(SearchError.NO_RESULTS)
                    } else {
                        SearchState.Content(foundTracks)
                    }
                )
            }

            override fun failure() {
                _state.postValue(SearchState.Error(SearchError.NETWORK_ERROR))
            }
        })
    }

    fun updateHistory(track: Track) {
        val currentHistory = searchHistory.load()
        val newHistory = listOf(track) + currentHistory.filter { it.trackId != track.trackId }.take(9)
        searchHistory.save(newHistory)
        if (inputEditText.isEmpty()) {
            _state.value = SearchState.HistoryContent(newHistory)
        }
    }

    fun clearHistory() {
        searchHistory.save(emptyList())
        if (inputEditText.isEmpty()) {
            _state.value = SearchState.HistoryEmpty
        }
    }

    sealed class SearchState {
        data object HistoryEmpty : SearchState()
        data class HistoryContent(val tracks: List<Track>) : SearchState()
        data object Loading : SearchState()
        data class Content(val tracks: List<Track>) : SearchState()
        data class Error(val error: SearchError) : SearchState()
    }

    enum class SearchError {
        NETWORK_ERROR, NO_RESULTS
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }
}