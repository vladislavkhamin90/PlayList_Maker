package com.example.playlist_maker.presentation.ui.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.data.sharedprefs.SearchHistory
import com.example.playlist_maker.domain.api.TrackInteractor

class SearchViewModelFactory(
    private val interactor: TrackInteractor,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            val searchHistory = SearchHistory(context)
            return SearchViewModel(interactor, searchHistory) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}