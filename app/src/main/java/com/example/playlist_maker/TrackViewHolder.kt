package com.example.playlist_maker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val nameTrack: TextView = itemView.findViewById(R.id.title_music)
    private val artistTrack: TextView = itemView.findViewById(R.id.artist)
    private val time: TextView = itemView.findViewById(R.id.time)
    private val image: ImageView = itemView.findViewById(R.id.image)

    fun bind(track: Track){
        nameTrack.text = track.trackName
        artistTrack.text = track.artistName
        time.text = track.trackTime

        Glide.with(itemView)
            .load(track.artworkUrl100).transform(RoundedCorners(2))
            .placeholder(R.drawable.placeholder)
            .into(image)
    }
}