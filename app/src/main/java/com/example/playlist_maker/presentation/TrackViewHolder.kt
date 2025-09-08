package com.example.playlist_maker.presentation

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.domain.models.Track

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val image: ImageView = itemView.findViewById(R.id.image)
    private val titleMusic: TextView = itemView.findViewById(R.id.title_music)
    private val artist: TextView = itemView.findViewById(R.id.artist)
    private val time: TextView = itemView.findViewById(R.id.time)

    fun bind(track: Track) {
        titleMusic.text = track.trackName
        artist.text = track.artistName
        time.text = track.trackTimeMillis

        if (track.artworkUrl100.isNotEmpty()) {
            val radius = (8 * itemView.context.resources.displayMetrics.density).toInt()
            Glide.with(itemView)
                .load(track.artworkUrl100.replace("100x100", "500x500"))
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(radius))
                .into(image)
        } else {
            image.setImageResource(R.drawable.placeholder)
        }
    }
}