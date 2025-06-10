package com.example.playlist_maker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker.R
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.presentation.TrackAdapter
import com.example.playlist_maker.presentation.ui.player.AudioPlayer
import com.google.gson.Gson

const val CLICK_DEBOUNCE_DELAY = 1000L

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var youSearchText: TextView
    private lateinit var clearHistoryBtn: Button
    private lateinit var clearButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var mainView: LinearLayout
    private lateinit var internetErrorView: View
    private lateinit var searchErrorView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolBar: Toolbar

    private val gson = Gson()
    private var isClickAllowed = true

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(Creator.provideTrackInteractor(), applicationContext)
    }

    companion object {
        const val KEY = "KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recycle_view)
        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)
        mainView = findViewById(R.id.main)
        youSearchText = findViewById(R.id.you_search)
        clearHistoryBtn = findViewById(R.id.clear_history_btn)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        progressBar = findViewById(R.id.progress_bar)
        toolBar = findViewById(R.id.tool_bar)

        val inflater = LayoutInflater.from(this)
        internetErrorView = inflater.inflate(R.layout.internet_error_item, mainView, false)
        searchErrorView = inflater.inflate(R.layout.search_error_item, mainView, false)

        mainView.addView(internetErrorView)
        mainView.addView(searchErrorView)

        internetErrorView.isVisible = false
        searchErrorView.isVisible = false

        recyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        clearButton.setOnClickListener {
            inputEditText.setText("")
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                viewModel.searchDebounced(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        internetErrorView.findViewById<Button>(R.id.update_btn).setOnClickListener {
            viewModel.search(inputEditText.text.toString())
        }

        clearHistoryBtn.setOnClickListener {
            viewModel.clearHistory()
        }

        viewModel.state.observe(this) { state ->
            when (state) {
                is SearchViewModel.SearchState.HistoryEmpty -> {
                    clearSearch()
                    clearHistory()
                }
                is SearchViewModel.SearchState.HistoryContent -> {
                    clearSearch()
                    showHistory(state.tracks)
                }
                is SearchViewModel.SearchState.Loading -> showLoading()
                is SearchViewModel.SearchState.Content -> showTracks(state.tracks)
                is SearchViewModel.SearchState.Error -> handleError(state.error)
            }
        }
        toolBar.setNavigationOnClickListener {
            this.finish()
        }
    }

    private fun showLoading() {
        progressBar.isVisible = true
        recyclerView.isVisible = false
        internetErrorView.isVisible = false
        searchErrorView.isVisible = false
    }

    private fun showTracks(tracks: List<Track>) {
        progressBar.isVisible = false
        recyclerView.isVisible = true
        internetErrorView.isVisible = false
        searchErrorView.isVisible = false

        recyclerView.adapter = TrackAdapter(tracks) { track ->
            if (clickDebounce()) {
                viewModel.updateHistory(track)
                navigateToPlayer(track)
            }
        }
    }

    private fun handleError(error: SearchViewModel.SearchError) {
        progressBar.isVisible = false
        recyclerView.isVisible = false

        when (error) {
            SearchViewModel.SearchError.NETWORK_ERROR -> {
                internetErrorView.isVisible = true
                searchErrorView.isVisible = false
            }
            SearchViewModel.SearchError.NO_RESULTS -> {
                internetErrorView.isVisible = false
                searchErrorView.isVisible = true
            }
        }
    }

    private fun clearSearch() {
        progressBar.isVisible = false
        recyclerView.isVisible = false
        internetErrorView.isVisible = false
        searchErrorView.isVisible = false
    }

    private fun showHistory(tracks: List<Track>) {
        youSearchText.isVisible = true
        clearHistoryBtn.isVisible = true
        historyRecyclerView.isVisible = true

        historyRecyclerView.adapter = TrackAdapter(tracks) { track ->
            if (clickDebounce()) {
                navigateToPlayer(track)
            }
        }
    }

    private fun clearHistory() {
        youSearchText.isVisible = false
        clearHistoryBtn.isVisible = false
        historyRecyclerView.isVisible = false
    }

    private fun navigateToPlayer(track: Track) {
        val intent = Intent(this, AudioPlayer::class.java).apply {
            putExtra(KEY, gson.toJson(track))
        }
        startActivity(intent)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            inputEditText.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}