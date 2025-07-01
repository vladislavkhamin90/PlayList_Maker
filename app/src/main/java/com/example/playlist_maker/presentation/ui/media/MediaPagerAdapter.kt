package com.example.playlist_maker.presentation.ui.media

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediaPagerAdapter(
    activity: AppCompatActivity,
    private val fragments: List<Fragment>
) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}