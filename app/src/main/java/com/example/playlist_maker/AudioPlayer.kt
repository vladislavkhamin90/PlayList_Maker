package com.example.playlist_maker

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson

const val KEY = "KEY"

class AudioPlayer : AppCompatActivity() {

    private val gson = Gson()

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
        Log.i("MyLog", message!!)

        fun getCoverArtwork(track: Track) = track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")



        val trackName = findViewById<TextView>(R.id.track_name)
        val artistName = findViewById<TextView>(R.id.artist_name)
        val durationValue = findViewById<TextView>(R.id.duration_value)
        val yearValue = findViewById<TextView>(R.id.year_value)
        val genreValue = findViewById<TextView>(R.id.genre_value)
        val countryValue = findViewById<TextView>(R.id.country_value)
        val albumImage = findViewById<ImageView>(R.id.album_image)
        val albumName = findViewById<TextView>(R.id.album_name)
        val albumNameValue = findViewById<TextView>(R.id.album_name_value)

        albumNameValue.isVisible = true
        albumName.isVisible = true

        Log.i("MyLog", track.collectionName)

        if(track.collectionName == null){
            Log.i("MyLog", track.collectionName)
            albumNameValue.isVisible = false
            albumName.isVisible = false
        } else{
            albumNameValue.text = track.collectionName
        }

        trackName.text = track.trackName
        artistName.text = track.artistName
        durationValue.text = track.trackTime
        Glide.with(applicationContext)
            .load(getCoverArtwork(track)).transform(RoundedCorners(8))
            .placeholder(R.drawable.placeholder_big)
            .into(albumImage)
        yearValue.text = track.releaseDate.substringBefore('-')
        genreValue.text = track.primaryGenreName
        countryValue.text = track.country



    }
}