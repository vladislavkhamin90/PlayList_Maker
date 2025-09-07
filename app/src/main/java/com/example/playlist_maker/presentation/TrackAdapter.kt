package com.example.playlist_maker.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker.R
import com.example.playlist_maker.domain.models.Track

class TrackAdapter(
    var tracks: List<Track>,
    private val onItemClicked: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var onItemLongClicked: (Track) -> Boolean = { false }

    fun setOnItemLongClickListener(listener: (Track) -> Boolean) {
        onItemLongClicked = listener
    }
    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onItemClicked(tracks[position])
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClicked(tracks[position])
        }
    }

    override fun getItemCount() = tracks.size
}