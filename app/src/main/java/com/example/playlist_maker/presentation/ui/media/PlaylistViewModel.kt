package com.example.playlist_maker.presentation.ui.media

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.domain.api.PlaylistInteractor
import com.example.playlist_maker.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var playlistsJob: kotlinx.coroutines.Job? = null

    fun loadPlaylists() {
        playlistsJob?.cancel()
        _isLoading.value = true

        playlistsJob = viewModelScope.launch {
            try {
                playlistInteractor.getAllPlaylists().collect { playlists ->
                    _playlists.value = playlists
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("MyLog", "Error loading playlists: $e")
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playlistsJob?.cancel()
    }
}