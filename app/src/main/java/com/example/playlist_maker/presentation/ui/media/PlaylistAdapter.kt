package com.example.playlist_maker.presentation.ui.media

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.ItemPlaylistBinding
import com.example.playlist_maker.domain.models.Playlist

class PlaylistAdapter(
    private var playlists: List<Playlist> = emptyList(),
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(
        private val binding: ItemPlaylistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            with(binding) {
                playlistName.text = playlist.name
                playlistTrackCount.text = itemView.context.resources.getQuantityString(
                    R.plurals.tracks_count, playlist.trackCount, playlist.trackCount
                )

                if (playlist.coverImagePath != null) {
                    Glide.with(itemView)
                        .load(playlist.coverImagePath)
                        .placeholder(R.drawable.placeholder_big)
                        .centerCrop()
                        .into(playlistCover)
                } else {
                    playlistCover.setImageResource(R.drawable.placeholder_big)
                }

                itemView.setOnClickListener {
                    onPlaylistClick(playlist)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
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