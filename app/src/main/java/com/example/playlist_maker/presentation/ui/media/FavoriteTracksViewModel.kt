package com.example.playlist_maker.presentation.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.domain.api.FavoriteTracksInteractor
import com.example.playlist_maker.domain.models.Track
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class FavoriteTracksState {
    object Empty : FavoriteTracksState()
    data class Content(val tracks: List<Track>) : FavoriteTracksState()
}

class FavoriteTracksViewModel(
    private val interactor: FavoriteTracksInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoriteTracksState>(FavoriteTracksState.Empty)
    val state: LiveData<FavoriteTracksState> = _state

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            interactor.getFavorites().collectLatest { tracks ->
                _state.postValue(
                    if (tracks.isEmpty()) FavoriteTracksState.Empty
                    else FavoriteTracksState.Content(tracks)
                )
            }
        }
    }
}