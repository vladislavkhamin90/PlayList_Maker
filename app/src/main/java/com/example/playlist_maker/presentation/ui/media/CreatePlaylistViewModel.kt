package com.example.playlist_maker.presentation.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.domain.api.PlaylistInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlistName = MutableLiveData<String>("")
    val playlistName: LiveData<String> = _playlistName

    private val _playlistDescription = MutableLiveData<String?>("")
    val playlistDescription: LiveData<String?> = _playlistDescription

    private val _playlistCoverPath = MutableLiveData<String?>(null)
    val playlistCoverPath: LiveData<String?> = _playlistCoverPath

    private val _isCreateButtonEnabled = MutableLiveData<Boolean>(false)
    val isCreateButtonEnabled: LiveData<Boolean> = _isCreateButtonEnabled

    private val _navigateBack = MutableLiveData<Boolean>(false)
    val navigateBack: LiveData<Boolean> = _navigateBack

    fun loadPlaylistForEditing(playlistId: Long) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            playlist?.let {
                _playlistName.value = it.name
                _playlistDescription.value = it.description
                _playlistCoverPath.value = it.coverImagePath
                validateName(it.name)
            }
        }
    }

    fun setCoverImagePath(path: String?) {
        _playlistCoverPath.value = path
    }

    fun validateName(name: String) {
        _isCreateButtonEnabled.value = name.isNotBlank()
    }

    fun createPlaylist(name: String, description: String?, coverImagePath: String?) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(name, description, coverImagePath)
            _navigateBack.value = true
        }
    }

    fun updatePlaylist(playlistId: Long, name: String, description: String?, coverImagePath: String?) {
        viewModelScope.launch {
            val existingPlaylist = playlistInteractor.getPlaylistById(playlistId)
            existingPlaylist?.let { playlist ->
                val updatedPlaylist = playlist.copy(
                    name = name,
                    description = description,
                    coverImagePath = coverImagePath,
                    trackCount = playlist.trackCount
                )
                playlistInteractor.updatePlaylist(updatedPlaylist)
                _navigateBack.value = true
            }
        }
    }

    fun resetNavigation() {
        _navigateBack.value = false
    }
}