package com.example.playlist_maker

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.*
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class TrackAdapter(private val tracks: List<Track>):
    RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size
}


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