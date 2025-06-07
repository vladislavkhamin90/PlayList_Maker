package com.example.playlist_maker.presentation.ui.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.domain.models.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

const val KEY = "KEY"

class AudioPlayer : AppCompatActivity() {

    private val gson = Gson()
    private val viewModel: AudioPlayerViewModel by viewModels {
        AudioPlayerViewModelFactory(Creator.providePlayerControlUseCase())
    }
    private var url = String()
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var play: View
    private lateinit var trackTime: TextView

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val PLAY = 0
        private const val PAUSE = 1
    }

    private var currentIcon = PLAY
    private var playerState = STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolBareBackIcon = findViewById<Toolbar>(R.id.toolbar)
        toolBareBackIcon.setNavigationOnClickListener {
            this.finish()
        }
        val message = intent.getStringExtra(KEY)
        val track = gson.fromJson(message, Track::class.java)
        fun getCoverArtwork(track: Track) =
            track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

        val trackName = findViewById<TextView>(R.id.track_name)
        val artistName = findViewById<TextView>(R.id.artist_name)
        val durationValue = findViewById<TextView>(R.id.duration_value)
        val yearValue = findViewById<TextView>(R.id.year_value)
        val genreValue = findViewById<TextView>(R.id.genre_value)
        val countryValue = findViewById<TextView>(R.id.country_value)
        val albumImage = findViewById<ImageView>(R.id.album_image)
        val albumName = findViewById<TextView>(R.id.album_name)
        val albumNameValue = findViewById<TextView>(R.id.album_name_value)
        trackTime = findViewById(R.id.preview_track_time)
        play = findViewById(R.id.play)

        albumNameValue.isVisible = true
        albumName.isVisible = true

        if(track.collectionName == null){
            albumNameValue.isVisible = false
            albumName.isVisible = false
        } else{
            albumNameValue.text = track.collectionName
        }

        url = track.previewUrl!!
        viewModel.preparePlayer(url)
        playerState = STATE_PREPARED

        trackName.text = track.trackName
        artistName.text = track.artistName
        durationValue.text = track.trackTimeMillis
        Glide.with(applicationContext)
            .load(getCoverArtwork(track)).transform(RoundedCorners(8))
            .placeholder(R.drawable.placeholder_big)
            .into(albumImage)
        yearValue.text = track.releaseDate.substringBefore('-')
        genreValue.text = track.primaryGenreName
        countryValue.text = track.country

        play.setOnClickListener {
            playbackControl()
        }
    }

    private fun startPlayer() {
        viewModel.play()
        playerState = STATE_PLAYING
        currentIcon = PLAY
        setIcon()
    }

    private fun pausePlayer() {
        viewModel.pause()
        playerState = STATE_PAUSED
        currentIcon = PAUSE
        setIcon()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                stopTimer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                startTimer()
            }
        }
    }

    private fun setIcon(){
        when(currentIcon){
            PAUSE ->{
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    play.setBackgroundResource(R.drawable.play_button_dark)
                } else {
                    play.setBackgroundResource(R.drawable.play_button)
                }
            }
            PLAY ->{
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    play.setBackgroundResource(R.drawable.pause_button_dark)
                } else {
                    play.setBackgroundResource(R.drawable.pause_button)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
    }

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(viewModel.getCurrentPosition())
                handler.postDelayed(this, 500)
            }
        }
    }

    private fun startTimer() {
        handler.post(updateTimeRunnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(updateTimeRunnable)
    }
}