package com.example.playlist_maker.presentation.ui.media

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediaPagerAdapter( activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteTracksFragment.newInstance()
            1 -> PlaylistFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}