//package com.example.playlist_maker.presentation.ui.player
//
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.app.AppCompatDelegate
//import androidx.appcompat.widget.Toolbar
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.isVisible
//import androidx.lifecycle.Observer
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.resource.bitmap.RoundedCorners
//import com.example.playlist_maker.R
//import com.example.playlist_maker.domain.models.Track
//import com.google.gson.Gson
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//const val KEY = "KEY"
//
//class AudioPlayer : AppCompatActivity() {
//
//    private val gson = Gson()
//    private val viewModel: AudioPlayerViewModel by viewModel()
//
//    private lateinit var play: View
//    private lateinit var trackTime: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_audio_player)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        val toolBareBackIcon = findViewById<Toolbar>(R.id.toolbar)
//        toolBareBackIcon.setNavigationOnClickListener {
//            this.finish()
//        }
//
//        val message = intent.getStringExtra(KEY_MESSAGE)
//        val track = gson.fromJson(message, Track::class.java)
//        initViews(track)
//        setupObservers()
//        track.previewUrl?.let { url ->
//            if (url.isNotBlank()) {
//                viewModel.preparePlayer(url)
//            } else {
//                Log.i("MyLog","No audio available")
//            }
//        } ?:  Log.i("MyLog","No audio URL provided")
//        play.setOnClickListener {
//            if (viewModel.playerState.value?.status != AudioPlayerViewModel.PlayerState.Status.DEFAULT) {
//                viewModel.playbackControl()
//            }
//        }
//    }
//
//    private fun initViews(track: Track) {
//        fun getCoverArtwork(track: Track) =
//            track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
//
//        val trackName = findViewById<TextView>(R.id.track_name)
//        val artistName = findViewById<TextView>(R.id.artist_name)
//        val durationValue = findViewById<TextView>(R.id.duration_value)
//        val yearValue = findViewById<TextView>(R.id.year_value)
//        val genreValue = findViewById<TextView>(R.id.genre_value)
//        val countryValue = findViewById<TextView>(R.id.country_value)
//        val albumImage = findViewById<ImageView>(R.id.album_image)
//        val albumName = findViewById<TextView>(R.id.album_name)
//        val albumNameValue = findViewById<TextView>(R.id.album_name_value)
//        trackTime = findViewById(R.id.preview_track_time)
//        play = findViewById(R.id.play)
//
//        albumNameValue.isVisible = true
//        albumName.isVisible = true
//
//        if(track.collectionName == null){
//            albumNameValue.isVisible = false
//            albumName.isVisible = false
//        } else{
//            albumNameValue.text = track.collectionName
//        }
//
//        trackName.text = track.trackName
//        artistName.text = track.artistName
//        durationValue.text = track.trackTimeMillis
//        Glide.with(applicationContext)
//            .load(getCoverArtwork(track)).transform(RoundedCorners(8))
//            .placeholder(R.drawable.placeholder_big)
//            .into(albumImage)
//        yearValue.text = track.releaseDate.substringBefore('-')
//        genreValue.text = track.primaryGenreName
//        countryValue.text = track.country
//    }
//
//    private fun setupObservers() {
//        viewModel.playerState.observe(this, Observer { state ->
//            state?.let {
//                updatePlayButtonIcon(it.status)
//                trackTime.text = it.currentPosition
//            }
//        })
//    }
//
//    private fun updatePlayButtonIcon(status: AudioPlayerViewModel.PlayerState.Status) {
//        val isDarkTheme = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
//
//        when (status) {
//            AudioPlayerViewModel.PlayerState.Status.PLAYING -> {
//                play.setBackgroundResource(
//                    if (isDarkTheme) R.drawable.pause_button_dark else R.drawable.pause_button
//                )
//            }
//            AudioPlayerViewModel.PlayerState.Status.PREPARED,
//            AudioPlayerViewModel.PlayerState.Status.PAUSED -> {
//                play.setBackgroundResource(
//                    if (isDarkTheme) R.drawable.play_button_dark else R.drawable.play_button
//                )
//            }
//            else -> {}
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        viewModel.pause()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        viewModel.release()
//    }
//}