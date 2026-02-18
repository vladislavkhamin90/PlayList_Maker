package com.example.playlist_maker.presentation.ui.media

import MediaPagerAdapter
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentMediaBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragment : Fragment(R.layout.fragment_media) {

    private lateinit var binding: FragmentMediaBinding
    private val viewModel: MediaViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMediaBinding.bind(view)

        setupToolbar()
        view.post {
            setupViewPager()
        }
    }

    private fun setupToolbar() {
        binding.toolBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupViewPager() {
        val adapter = MediaPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.featured_tracks)
                1 -> getString(R.string.playlists)
                else -> ""
            }
        }.attach()
    }
}