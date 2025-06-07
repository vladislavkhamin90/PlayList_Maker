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

    private val searchLiveData = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = searchLiveData

    private val historyLiveData = MutableLiveData<HistoryState>()
    val historyState: LiveData<HistoryState> = historyLiveData

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search(inputEditText) }
    private var inputEditText: String = ""

    init {
        loadHistory()
    }

    private fun loadHistory() {
        val history = searchHistory.load()
        if (history.isEmpty()) {
            historyLiveData.value = HistoryState.Empty
        } else {
            historyLiveData.value = HistoryState.Content(history)
        }
    }

    fun searchDebounced(query: String) {
        inputEditText = query
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun search(query: String) {
        if (query.isEmpty()) {
            searchLiveData.value = SearchState.Empty
            return
        }

        searchLiveData.value = SearchState.Loading

        interactor.searchTrack(query, object : TrackInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                if (foundTracks.isEmpty()) {
                    searchLiveData.postValue(SearchState.Error(SearchError.NO_RESULTS))
                } else {
                    searchLiveData.postValue(SearchState.Content(foundTracks))
                }
            }

            override fun failure() {
                searchLiveData.postValue(SearchState.Error(SearchError.NETWORK_ERROR))
            }
        })
    }

    fun updateHistory(track: Track) {
        val currentHistory = (historyLiveData.value as? HistoryState.Content)?.tracks ?: emptyList()
        val newHistory = listOf(track) + currentHistory.distinctBy { it.trackId }.take(10)
        searchHistory.save(newHistory)
        historyLiveData.value = HistoryState.Content(newHistory)
    }

    fun clearHistory() {
        searchHistory.save(emptyList())
        historyLiveData.value = HistoryState.Empty
    }

    sealed class SearchState {
        data object Empty : SearchState()
        data object Loading : SearchState()
        data class Content(val tracks: List<Track>) : SearchState()
        data class Error(val error: SearchError) : SearchState()
    }

    sealed class HistoryState {
        data object Empty : HistoryState()
        data class Content(val tracks: List<Track>) : HistoryState()
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