package com.example.playlist_maker.presentation.ui.search

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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker.R
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.presentation.TrackAdapter
import com.example.playlist_maker.presentation.ui.player.KEY
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

const val CLICK_DEBOUNCE_DELAY = 1000L

class SearchFragment : Fragment(R.layout.fragment_search) {

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

    private val gson = Gson()
    private var isClickAllowed = true
    private var currentQuery: String = ""

    private val viewModel: SearchViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycle_view)
        inputEditText = view.findViewById(R.id.inputEditText)
        clearButton = view.findViewById(R.id.clearIcon)
        mainView = view.findViewById(R.id.main)
        youSearchText = view.findViewById(R.id.you_search)
        clearHistoryBtn = view.findViewById(R.id.clear_history_btn)
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
        progressBar = view.findViewById(R.id.progress_bar)

        val inflater = LayoutInflater.from(requireContext())
        internetErrorView = inflater.inflate(R.layout.internet_error_item, mainView, false)
        searchErrorView = inflater.inflate(R.layout.search_error_item, mainView, false)

        mainView.addView(internetErrorView)
        mainView.addView(searchErrorView)

        internetErrorView.isVisible = false
        searchErrorView.isVisible = false

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        clearButton.setOnClickListener {
            inputEditText.setText("")
            currentQuery = ""
            viewModel.search("")
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s?.toString() ?: ""
                clearButton.isVisible = !s.isNullOrEmpty()
                viewModel.searchDebounced(currentQuery)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        internetErrorView.findViewById<Button>(R.id.update_btn).setOnClickListener {
            viewModel.search(currentQuery)
        }

        clearHistoryBtn.setOnClickListener {
            viewModel.clearHistory()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchViewModel.SearchState.HistoryEmpty -> {
                    if (currentQuery.isEmpty()) {
                        clearSearch()
                        clearHistory()
                    }
                }
                is SearchViewModel.SearchState.HistoryContent -> {
                    if (currentQuery.isEmpty()) {
                        clearSearch()
                        showHistory(state.tracks)
                    }
                }
                is SearchViewModel.SearchState.Loading -> showLoading()
                is SearchViewModel.SearchState.Content -> showTracks(state.tracks)
                is SearchViewModel.SearchState.Error -> handleError(state.error)
            }
        }
    }

    private fun showLoading() {
        progressBar.isVisible = true
        recyclerView.isVisible = false
        internetErrorView.isVisible = false
        searchErrorView.isVisible = false
        historyRecyclerView.isVisible = false
        youSearchText.isVisible = false
        clearHistoryBtn.isVisible = false
    }

    private fun showTracks(tracks: List<Track>) {
        progressBar.isVisible = false
        recyclerView.isVisible = true
        internetErrorView.isVisible = false
        searchErrorView.isVisible = false
        historyRecyclerView.isVisible = false
        youSearchText.isVisible = false
        clearHistoryBtn.isVisible = false

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
        val bundle = Bundle().apply {
            putString(KEY, gson.toJson(track))
        }
        findNavController().navigate(
            R.id.action_search_to_player,
            bundle
        )
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