package com.example.playlist_maker.presentation.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.data.sharedprefs.SearchHistory
import com.example.playlist_maker.domain.api.TrackInteractor
import com.example.playlist_maker.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchViewModel(
    private val interactor: TrackInteractor,
    private val searchHistory: SearchHistory
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private var searchJob: Job? = null
    private var currentQuery: String = ""

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
        currentQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            search(query)
        }
    }

    fun search(query: String) {
        currentQuery = query
        searchJob?.cancel()

        if (query.isEmpty()) {
            showHistory()
            return
        }

        _state.value = SearchState.Loading

        searchJob = viewModelScope.launch {
            interactor.searchTrack(query).collectLatest { tracks ->
                _state.value = if (tracks.isEmpty()) {
                    SearchState.Error(SearchError.NO_RESULTS)
                } else {
                    SearchState.Content(tracks)
                }
            }
        }
    }

    private fun showHistory() {
        val history = searchHistory.load()
        _state.value = if (history.isEmpty()) {
            SearchState.HistoryEmpty
        } else {
            SearchState.HistoryContent(history)
        }
    }

    fun updateHistory(track: Track) {
        viewModelScope.launch {
            val currentHistory = searchHistory.load()
            val newHistory = listOf(track) +
                    currentHistory.filter { it.trackId != track.trackId }.take(9)
            searchHistory.save(newHistory)

            if (currentQuery.isEmpty()) {
                _state.value = SearchState.HistoryContent(newHistory)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistory.save(emptyList())
            if (currentQuery.isEmpty()) {
                _state.value = SearchState.HistoryEmpty
            }
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
}