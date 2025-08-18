package com.example.playlist_maker.presentation.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentFavoriteTracksBinding
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.presentation.TrackAdapter
import com.example.playlist_maker.presentation.ui.player.KEY
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {
    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteTracksViewModel by viewModel()
    private lateinit var adapter: TrackAdapter
    private var clickDebounceJob: Job? = null
    private val gson = Gson()

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(emptyList()) { track ->
            handleTrackClick(track)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoriteTracksFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteTracksState.Empty -> showEmptyState()
                is FavoriteTracksState.Content -> showContent(state.tracks)
            }
        }
    }

    private fun showEmptyState() {
        binding.recyclerView.visibility = View.GONE
        binding.root.findViewById<View>(R.id.emptyState).visibility = View.VISIBLE
    }

    private fun showContent(tracks: List<Track>) {
        binding.recyclerView.visibility = View.VISIBLE
        binding.root.findViewById<View>(R.id.emptyState).visibility = View.GONE
        adapter.tracks = tracks
        adapter.notifyDataSetChanged()
    }

    private fun handleTrackClick(track: Track) {
        clickDebounceJob?.cancel()
        clickDebounceJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(CLICK_DEBOUNCE_DELAY)
            navigateToPlayer(track)
        }
    }

    private fun navigateToPlayer(track: Track) {
        val bundle = Bundle().apply {
            putString(KEY, gson.toJson(track.copy(isFavorite = track.isFavorite)))
        }
        findNavController().navigate(
            R.id.action_media_to_player,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clickDebounceJob?.cancel()
        _binding = null
    }
}