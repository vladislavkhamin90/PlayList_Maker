package com.example.playlist_maker.presentation.ui.media

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentPlaylistDetailBinding
import com.example.playlist_maker.domain.models.Playlist
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.presentation.TrackAdapter
import com.example.playlist_maker.presentation.ui.main.MainActivity
import com.example.playlist_maker.presentation.ui.player.KEY
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailFragment : Fragment() {
    private val viewModel: PlaylistDetailViewModel by viewModel()
    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<View>
    private val gson = Gson()

    private var playlistId: Long = -1
    private var menuBottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            playlistId = it.getLong(ARG_PLAYLIST_ID, -1)
        }

        trackAdapter = TrackAdapter(emptyList()) { track ->
            navigateToPlayer(track)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheet()
        setupMenuBottomSheet()
        setupRecyclerView()
        setupClickListeners()
        setupObservers()

        if (playlistId != -1L) {
            viewModel.loadPlaylist(playlistId)
        } else {
            Log.d("MyLog", "Invalid playlist ID")
        }
    }

    private fun navigateToPlayer(track: Track) {
        val trackJson = gson.toJson(track)
        val bundle = Bundle().apply {
            putString(KEY, trackJson)
        }
        findNavController().navigate(R.id.action_playlistDetailFragment_to_playerFragment, bundle)
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.peekHeight = 150
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun setupMenuBottomSheet() {
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet)
        menuBottomSheetBehavior.isHideable = true
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        menuBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (_binding == null) return

                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.menuOverlay.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.menuOverlay.visibility = View.GONE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (_binding == null) return
                binding.menuOverlay.alpha = slideOffset.coerceIn(0f, 1f)
            }
        }

        menuBottomSheetCallback?.let {
            menuBottomSheetBehavior.addBottomSheetCallback(it)
        }

        binding.menuOverlay.setOnClickListener {
            hideMenu()
        }
    }

    private fun setupRecyclerView() {
        trackAdapter.setOnItemLongClickListener { track ->
            showDeleteDialog(track)
            true
        }

        binding.tracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            navigateBackSafely()
        }

        binding.shareBtn.setOnClickListener {
            sharePlaylist()
        }

        binding.menuBtn.setOnClickListener {
            showMenu()
        }

        binding.menuShare.setOnClickListener {
            val tracks = viewModel.tracks.value ?: emptyList()
            if (tracks.isEmpty()) {
                hideMenu()
                showEmptyPlaylistShareMessage()
            } else {
                hideMenu()
                sharePlaylist()
            }
        }

        binding.menuDelete.setOnClickListener {
            hideMenu()
            showDeletePlaylistDialog()
        }

        binding.menuEdit.setOnClickListener {
            hideMenu()
            navigateToEditPlaylist()
        }
    }

    private fun showEmptyPlaylistShareMessage() {
        Toast.makeText(
            requireContext(),
            getString(R.string.empty_playlist_share_message),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun navigateToEditPlaylist() {
        val playlist = viewModel.playlist.value
        playlist?.let {
            val bundle = Bundle().apply {
                putBoolean("edit_mode", true)
                putLong("playlist_id", it.id)
            }
            findNavController().navigate(R.id.action_playlistDetailFragment_to_createPlaylistFragment, bundle)
        }
    }

    private fun setupObservers() {
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            Log.d("MyLog", "Playlist loaded: ${playlist?.name}, tracks: ${playlist?.trackCount}")
            playlist?.let { displayPlaylistInfo(it) }
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            Log.d("MyLog", "Tracks loaded: ${tracks.size}")
            updateUI(tracks)
        }
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            playlist?.let { displayPlaylistInfo(it) }
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            updateUI(tracks)
        }

        viewModel.totalDuration.observe(viewLifecycleOwner) {
            updatePlaylistInfoText()
        }
    }

    private fun updateUI(tracks: List<Track>) {
        if (tracks.isNotEmpty()) {
            trackAdapter.updateTracks(tracks)
            binding.bottomSheet.visibility = View.VISIBLE
            binding.emptyPlaylist.visibility = View.GONE
        } else {
            trackAdapter.updateTracks(emptyList())
            binding.bottomSheet.visibility = View.GONE
            binding.emptyPlaylist.visibility = View.VISIBLE
        }
    }

    private fun displayPlaylistInfo(playlist: Playlist) {
        if (_binding == null) return

        with(binding) {
            playlistName.text = playlist.name
            playlistDescription.text = playlist.description ?: ""
            if (playlist.description.isNullOrEmpty()) {
                playlistDescription.visibility = View.GONE
            } else {
                playlistDescription.visibility = View.VISIBLE
            }

            updatePlaylistInfoText()

            Glide.with(requireContext())
                .load(playlist.coverImagePath)
                .placeholder(R.drawable.placeholder_big)
                .into(playlistCover)
        }
    }

    private fun showDeleteDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track_title))
            .setMessage(getString(R.string.delete_track_message))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                viewModel.removeTrackFromPlaylist(playlistId, track)
                dialog.dismiss()
            }
            .show()
    }

    private fun updatePlaylistInfoText() {
        if (_binding == null) return

        val playlist = viewModel.playlist.value
        val duration = viewModel.totalDuration.value ?: 0L

        if (playlist != null) {
            val tracksText = resources.getQuantityString(
                R.plurals.tracks_count, playlist.trackCount, playlist.trackCount
            )
            val durationText = formatDuration(duration)

            binding.playlistDurationAndTracks.text = "$tracksText • $durationText"
        }
    }

    private fun formatDuration(milliseconds: Long): String {
        val minutes = milliseconds / 60000
        return "$minutes min"
    }

    private fun sharePlaylist() {
        if (_binding == null) return

        val tracks = viewModel.tracks.value ?: emptyList()
        val playlist = viewModel.playlist.value

        if (tracks.isEmpty() || playlist == null) {
            Toast.makeText(requireContext(), R.string.empty_playlist_message, Toast.LENGTH_SHORT).show()
            return
        }

        val shareText = buildShareText(playlist, tracks)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(shareIntent, "Поделиться плейлистом"))
    }

    private fun buildShareText(playlist: Playlist, tracks: List<Track>): String {
        val builder = StringBuilder()

        builder.append(playlist.name).append("\n")
        if (!playlist.description.isNullOrEmpty()) {
            builder.append(playlist.description).append("\n")
        }

        val tracksText = resources.getQuantityString(
            R.plurals.tracks_count, tracks.size, tracks.size
        )
        builder.append(tracksText).append("\n\n")

        tracks.forEachIndexed { index, track ->
            builder.append("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis})\n")
        }

        return builder.toString()
    }

    private fun showMenu() {
        if (_binding == null) return

        val playlist = viewModel.playlist.value
        if (playlist != null) {
            binding.menuPlaylistName.text = playlist.name
            binding.menuTrackCount.text = resources.getQuantityString(
                R.plurals.tracks_count, playlist.trackCount, playlist.trackCount
            )

            Glide.with(requireContext())
                .load(playlist.coverImagePath)
                .placeholder(R.drawable.placeholder)
                .into(binding.playlistImageSmall)
        }
        binding.menuBottomSheet.visibility = View.VISIBLE
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideMenu() {
        if (_binding == null) return
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить плейлист")
            .setMessage("Хотите удалить плейлист?")
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Да") { dialog, _ ->
                deletePlaylist()
                dialog.dismiss()
            }
            .show()
    }

    private fun deletePlaylist() {
        viewModel.deletePlaylist(playlistId)

        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "playlist_deleted", true
        )

        findNavController().popBackStack()
    }

    private fun navigateBackSafely() {
        try {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                requireActivity().supportFragmentManager.popBackStack()
            }
        } catch (e: Exception) {
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (playlistId != -1L) {
            viewModel.loadPlaylist(playlistId)
        }
        (requireActivity() as MainActivity).hideBottomNav()
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        menuBottomSheetCallback?.let {
            menuBottomSheetBehavior.removeBottomSheetCallback(it)
        }
        menuBottomSheetCallback = null
        _binding = null
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlistId"

        fun newInstance(playlistId: Long): PlaylistDetailFragment {
            val fragment = PlaylistDetailFragment()
            val args = Bundle().apply {
                putLong(ARG_PLAYLIST_ID, playlistId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}