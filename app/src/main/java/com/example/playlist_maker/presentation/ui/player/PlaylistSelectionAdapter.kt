package com.example.playlist_maker.presentation.ui.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlist_maker.R
import com.example.playlist_maker.domain.models.Playlist

class PlaylistSelectionAdapter(
    private var playlists: List<Playlist> = emptyList(),
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistSelectionAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
        private val trackCount: TextView = itemView.findViewById(R.id.playlist_track_count)
        private val playlistCover: ImageView = itemView.findViewById(R.id.playlist_cover)

        fun bind(playlist: Playlist) {
            playlistName.text = playlist.name
            trackCount.text = itemView.context.resources.getQuantityString(
                R.plurals.tracks_count, playlist.trackCount, playlist.trackCount
            )

            if (!playlist.coverImagePath.isNullOrEmpty()) {
                Glide.with(itemView)
                    .load(playlist.coverImagePath)
                    .placeholder(R.drawable.placeholder)
                    .into(playlistCover)
            } else {
                playlistCover.setImageResource(R.drawable.placeholder)
            }

            itemView.setOnClickListener {
                onPlaylistClick(playlist)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottom_sheet, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }
}