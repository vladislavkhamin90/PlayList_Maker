package com.example.playlist_maker.presentation.ui.player

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.domain.models.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

const val KEY = "KEY"

class AudioPlayerFragment : Fragment(R.layout.fragment_audio_player) {

    private val gson = Gson()
    private val viewModel: AudioPlayerViewModel by viewModel()

    private lateinit var play: View
    private lateinit var trackTime: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val message = arguments?.getString(KEY)
        val track = gson.fromJson(message, Track::class.java)
        initViews(track)
        setupObservers()

        track.previewUrl?.let { url ->
            if (url.isNotBlank()) {
                viewModel.preparePlayer(url)
            }
        }

        play.setOnClickListener {
            if (viewModel.playerState.value?.status != AudioPlayerViewModel.PlayerState.Status.DEFAULT) {
                viewModel.playbackControl()
            }
        }
    }

    private fun initViews(track: Track) {
        fun getCoverArtwork(track: Track) =
            track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

        val trackName = view?.findViewById<TextView>(R.id.track_name)
        val artistName = view?.findViewById<TextView>(R.id.artist_name)
        val durationValue = view?.findViewById<TextView>(R.id.duration_value)
        val yearValue = view?.findViewById<TextView>(R.id.year_value)
        val genreValue = view?.findViewById<TextView>(R.id.genre_value)
        val countryValue = view?.findViewById<TextView>(R.id.country_value)
        val albumImage = view?.findViewById<ImageView>(R.id.album_image)
        val albumName = view?.findViewById<TextView>(R.id.album_name)
        val albumNameValue = view?.findViewById<TextView>(R.id.album_name_value)
        val toolBar = view?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        trackTime = view?.findViewById(R.id.preview_track_time) ?: return
        play = view?.findViewById(R.id.play) ?: return

        toolBar?.setNavigationOnClickListener {
            val bundle = Bundle().apply {
                putString(KEY,"")
            }
            findNavController().navigate(
                R.id.action_player_to_search,
                bundle
            )
        }

        albumNameValue?.isVisible = true
        albumName?.isVisible = true

        if(track.collectionName == null){
            albumNameValue?.isVisible = false
            albumName?.isVisible = false
        } else{
            albumNameValue?.text = track.collectionName
        }

        trackName?.text = track.trackName
        artistName?.text = track.artistName
        durationValue?.text = track.trackTimeMillis
        albumImage?.let {
            Glide.with(requireContext())
                .load(getCoverArtwork(track)).transform(RoundedCorners(8))
                .placeholder(R.drawable.placeholder_big)
                .into(it)
        }
        yearValue?.text = track.releaseDate.substringBefore('-')
        genreValue?.text = track.primaryGenreName
        countryValue?.text = track.country
    }

    private fun setupObservers() {
        viewModel.playerState.observe(viewLifecycleOwner, Observer { state ->
            state?.let {
                updatePlayButtonIcon(it.status)
                trackTime.text = it.currentPosition
            }
        })
    }

    private fun updatePlayButtonIcon(status: AudioPlayerViewModel.PlayerState.Status) {
        val isDarkTheme = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        when (status) {
            AudioPlayerViewModel.PlayerState.Status.PLAYING -> {
                play.setBackgroundResource(
                    if (isDarkTheme) R.drawable.pause_button_dark else R.drawable.pause_button
                )
            }
            AudioPlayerViewModel.PlayerState.Status.PREPARED,
            AudioPlayerViewModel.PlayerState.Status.PAUSED -> {
                play.setBackgroundResource(
                    if (isDarkTheme) R.drawable.play_button_dark else R.drawable.play_button
                )
            }
            else -> {}
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
    }
}