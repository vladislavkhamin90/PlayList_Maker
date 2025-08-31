package com.example.playlist_maker.presentation.ui.player

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentAudioPlayerBinding
import com.example.playlist_maker.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

const val KEY = "KEY"

class AudioPlayerFragment : Fragment(R.layout.fragment_audio_player) {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private val gson = Gson()
    private val viewModel: AudioPlayerViewModel by viewModel()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistAdapter: PlaylistSelectionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAudioPlayerBinding.bind(view)

        val message = arguments?.getString(KEY)
        val track = gson.fromJson(message, Track::class.java)
        viewModel.setTrack(track)
        initViews(track)
        setupBottomSheet()
        setupPlaylistRecyclerView()
        setupObservers()

        track.previewUrl?.let { url ->
            if (url.isNotBlank()) {
                viewModel.preparePlayer(url)
            }
        }

        binding.play.setOnClickListener {
            if (viewModel.playerState.value?.status != AudioPlayerViewModel.PlayerState.Status.DEFAULT) {
                viewModel.playbackControl()
            }
        }

        binding.favorite.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.queue.setOnClickListener {
            viewModel.loadPlaylists()
            showPlaylistSelectionBottomSheet()
        }

        binding.toolbar.setNavigationOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                hidePlaylistSelectionBottomSheet()
            } else {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupObservers() {
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            state?.let {
                updatePlayButtonIcon(it.status)
                binding.previewTrackTime.text = it.currentPosition
                updateFavoriteButton(it.isFavorite)
            }
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.updatePlaylists(playlists)
        }

        viewModel.addToPlaylistResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AudioPlayerViewModel.AddToPlaylistResult.Success -> {
                    val playlistName = viewModel.playlists.value?.find { it.id == result.playlistId }?.name
                    Toast.makeText(
                        requireContext(),
                        "Добавлено в плейлист $playlistName",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is AudioPlayerViewModel.AddToPlaylistResult.AlreadyExists -> {
                    val playlistName = viewModel.playlists.value?.find { it.id == result.playlistId }?.name
                    Toast.makeText(
                        requireContext(),
                        "Трек уже добавлен в плейлист $playlistName",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is AudioPlayerViewModel.AddToPlaylistResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка: ${result.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        val isDarkTheme = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        val resId = when {
            isFavorite && isDarkTheme -> R.drawable.favorite_active_dark_theme
            isFavorite && !isDarkTheme -> R.drawable.favorite_active_white_theme
            !isFavorite && isDarkTheme -> R.drawable.favorite_inactive_dark_theme
            else -> R.drawable.favorite_inactive_white_theme
        }
        binding.favorite.setBackgroundResource(resId)
    }

    private fun initViews(track: Track) {
        fun getCoverArtwork(track: Track) =
            track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

        val cornerRadius = resources.getDimensionPixelSize(R.dimen.image_corner_radius)

        with(binding) {
            albumNameValue.isVisible = true
            albumName.isVisible = true

            if (track.collectionName == null) {
                albumNameValue.isVisible = false
                albumName.isVisible = false
            } else {
                albumNameValue.text = track.collectionName
            }

            trackName.text = track.trackName
            artistName.text = track.artistName
            durationValue.text = track.trackTimeMillis

            Glide.with(requireContext())
                .load(getCoverArtwork(track))
                .transform(RoundedCorners(cornerRadius))
                .placeholder(R.drawable.placeholder_big)
                .into(albumImage)

            yearValue.text = track.releaseDate.substringBefore('-')
            genreValue.text = track.primaryGenreName
            countryValue.text = track.country
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                        binding.overlay.isClickable = false
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.overlay.visibility = View.VISIBLE
                        binding.overlay.isClickable = true
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                        binding.overlay.isClickable = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset.coerceIn(0f, 1f)
            }
        })

        binding.overlay.setOnClickListener {
            hidePlaylistSelectionBottomSheet()
        }

        binding.newPlaylistBtn.setOnClickListener {
            navigateToCreatePlaylist()
        }
    }

    private fun setupPlaylistRecyclerView() {
        playlistAdapter = PlaylistSelectionAdapter(emptyList()) { playlist ->
            viewModel.addTrackToPlaylist(playlist.id)
            hidePlaylistSelectionBottomSheet()
        }

        binding.playlistsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistsRecyclerView.adapter = playlistAdapter
    }

    private fun updatePlayButtonIcon(status: AudioPlayerViewModel.PlayerState.Status) {
        val isDarkTheme = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        val resId = when (status) {
            AudioPlayerViewModel.PlayerState.Status.PLAYING -> {
                if (isDarkTheme) R.drawable.pause_button_dark else R.drawable.pause_button
            }
            AudioPlayerViewModel.PlayerState.Status.PREPARED,
            AudioPlayerViewModel.PlayerState.Status.PAUSED -> {
                if (isDarkTheme) R.drawable.play_button_dark else R.drawable.play_button
            }
            else -> return
        }
        binding.play.setBackgroundResource(resId)
    }

    private fun showPlaylistSelectionBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hidePlaylistSelectionBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun navigateToCreatePlaylist() {
        hidePlaylistSelectionBottomSheet()
        findNavController().navigate(R.id.action_audioPlayer_to_createPlaylist)
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.playerState.value?.status ==
            AudioPlayerViewModel.PlayerState.Status.PLAYING) {
            viewModel.pause()
        }
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            hidePlaylistSelectionBottomSheet()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
    }
}