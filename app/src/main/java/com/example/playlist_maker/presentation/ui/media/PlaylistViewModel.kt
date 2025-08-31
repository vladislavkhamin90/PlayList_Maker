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

    fun loadPlaylists() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val playlists = playlistInteractor.getAllPlaylists()
                _playlists.value = playlists
            } catch (e: Exception) {
                Log.e("MyLog", "$e")
            } finally {
                _isLoading.value = false
            }
        }
    }

}