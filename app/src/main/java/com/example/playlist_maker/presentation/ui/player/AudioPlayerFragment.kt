package com.example.playlist_maker.presentation.ui.player

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentAudioPlayerBinding
import com.example.playlist_maker.domain.models.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

const val KEY = "KEY"

class AudioPlayerFragment : Fragment(R.layout.fragment_audio_player) {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private val gson = Gson()
    private val viewModel: AudioPlayerViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAudioPlayerBinding.bind(view)

        val message = arguments?.getString(KEY)
        val track = gson.fromJson(message, Track::class.java)
        initViews(track)
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

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initViews(track: Track) {
        fun getCoverArtwork(track: Track) =
            track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

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

    private fun setupObservers() {
        viewModel.playerState.observe(viewLifecycleOwner, Observer { state ->
            state?.let {
                updatePlayButtonIcon(it.status)
                binding.previewTrackTime.text = it.currentPosition
            }
        })
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

    override fun onPause() {
        super.onPause()
        viewModel.pause()
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