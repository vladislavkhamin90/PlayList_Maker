package com.example.playlist_maker.presentation.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentPlaylistBinding
import com.example.playlist_maker.domain.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {
    private val viewModel: PlaylistViewModel by viewModel()
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        viewModel.loadPlaylists()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter(emptyList()) { playlist ->

        }

        binding.playlistsRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = playlistAdapter
        }
    }

    private fun setupObservers() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isNotEmpty()) {
                showPlaylists(playlists)
            } else {
                showEmptyState()
            }
        }
    }

    private fun setupClickListeners() {
        binding.newPlaylistBtn.setOnClickListener {
            navigateToCreatePlaylist()
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        binding.playlistsRecyclerView.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE
        playlistAdapter.updatePlaylists(playlists)
    }

    private fun showEmptyState() {
        binding.playlistsRecyclerView.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
    }

    private fun navigateToCreatePlaylist() {
        findNavController().navigate(R.id.action_media_to_createPlaylist)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}