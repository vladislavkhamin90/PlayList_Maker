//package com.example.playlist_maker.presentation.ui.media
//
//import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.playlist_maker.R
//import com.example.playlist_maker.databinding.ActivityMediaBinding
//import com.google.android.material.tabs.TabLayoutMediator
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//class MediaActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityMediaBinding
//    private val viewModel: MediaViewModel by viewModel()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_media)
//
//        binding = ActivityMediaBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setupToolbar()
//        setupViewPager()
//
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//
//    private fun setupToolbar() {
//        binding.toolBar.setNavigationOnClickListener {
//            onBackPressedDispatcher.onBackPressed()
//        }
//    }
//
//    private fun setupViewPager() {
//        val adapter = MediaPagerAdapter(this)
//        binding.viewPager.adapter = adapter
//
//        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
//            tab.text = when (position) {
//                0 -> getString(R.string.featured_tracks)
//                1 -> getString(R.string.playlists)
//                else -> ""
//            }
//        }.attach()
//    }
//}