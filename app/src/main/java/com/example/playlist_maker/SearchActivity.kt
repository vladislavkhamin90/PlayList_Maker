package com.example.playlist_maker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

const val BASE_URL = "https://itunes.apple.com"
const val CLICK_DEBOUNCE_DELAY = 1000L
private const val SEARCH_DEBOUNCE_DELAY = 2000L

class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText: EditText
    private lateinit var historyPreferences: SearchHistory

    private lateinit var historyAdapter: TrackAdapter

    private lateinit var historyRecyclerView: RecyclerView

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val songs = retrofit.create(SongsApi::class.java)

    private val trackList: MutableList<Track> = mutableListOf()
    private val trackListHistory: MutableList<Track> = mutableListOf()
    private val gson = Gson()

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())


    var textSearch = ""

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

        val recyclerView = findViewById<RecyclerView>(R.id.recycle_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        inputEditText = findViewById(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val tittleBackIcon = findViewById<Toolbar>(R.id.title)
        val mainView = findViewById<LinearLayout>(R.id.main)
        val youSearchText = findViewById<TextView>(R.id.you_search)
        val clearHistoryBtn = findViewById<Button>(R.id.clear_history_btn)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)

        historyPreferences = SearchHistory(this)
        trackListHistory.addAll((historyPreferences.load()))

        fun sendAndGoAudioPlayer(track: Track){
            val intent = Intent(this, AudioPlayer::class.java).apply {
                putExtra(KEY, gson.toJson(track))
            }
            startActivity(intent)
        }

        historyAdapter = TrackAdapter(trackListHistory, onItemClicked = { track ->
            sendAndGoAudioPlayer(track)
        })
        historyRecyclerView.adapter = historyAdapter

        val inflater = LayoutInflater.from(this)
        val internetError = inflater.inflate(R.layout.internet_error_item, mainView, false)
        val searchError = inflater.inflate(R.layout.search_error_item, mainView, false)
        val updateBtn = internetError.findViewById<Button>(R.id.update_btn)

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)

        fun historyVisibility(bool: Boolean) {
            youSearchText.isVisible = bool
            clearHistoryBtn.isVisible = bool
            historyRecyclerView.isVisible = bool
            if(bool){
                historyRecyclerView.adapter = historyAdapter
            }
        }

        clearHistoryBtn.setOnClickListener {
            trackListHistory.clear()
            historyRecyclerView.adapter = historyAdapter
            historyVisibility(false)
            historyPreferences.save(trackListHistory)
        }

        fun addHistoryUniqueItem(list: MutableList<Track>, track: Track) {
            list.filter { it.id == track.id }.toMutableList()
            list.add(0, track)
            if (list.size > 10) {
                list.removeAt(list.lastIndex)
            }
            historyPreferences.save(trackListHistory)
        }

        internetError.isVisible = false
        searchError.isVisible = false
        mainView.addView(internetError)
        mainView.addView(searchError)

        fun internetError() {
            internetError.isVisible = true
            trackList.clear()
        }

        fun searchError() {
            searchError.isVisible = true
            trackList.clear()
        }

        fun clearAllError() {
            internetError.isVisible = false
            searchError.isVisible = false
            recyclerView.isVisible = false
            progressBar.isVisible = false
        }

        fun fillTrackList(list: List<SongsResult>) {
            if (trackList.isNotEmpty()) {
                trackList.clear()
            }
            for (item in list) {
                trackList.add(
                    Track(
                        item.trackName,
                        item.artistName,
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)
                            .toString(),
                        item.artworkUrl100,
                        item.id,
                        item.collectionName,
                        item.releaseDate,
                        item.primaryGenreName,
                        item.country,
                        item.previewUrl
                    )
                )
            }

            recyclerView.adapter = TrackAdapter(trackList, onItemClicked = { track ->
                if (clickDebounce()) {
                    addHistoryUniqueItem(trackListHistory, track)
                    historyPreferences.save(trackListHistory)
                    historyAdapter.notifyDataSetChanged()
                    sendAndGoAudioPlayer(track)
                }
            })
            recyclerView.isVisible = true
        }

        fun search() {
            clearAllError()
            progressBar.isVisible = true
            songs.search(inputEditText.text.toString()).enqueue(object : Callback<Songs> {
                override fun onResponse(
                    call: Call<Songs>,
                    response: Response<Songs>,
                ) {
                    recyclerView.isVisible = false
                    val result = response.body()?.results
                    if (response.isSuccessful) {
                        if (result != null) {
                            @Nullable
                            if (result.isEmpty()) {
                                searchError()
                            } else {
                                fillTrackList(result)
                            }
                        } else {
                            Log.i("MyLog", "TrackList is null")
                        }
                    } else {
                        internetError()
                    }
                }

                override fun onFailure(call: Call<Songs>, t: Throwable) {
                    recyclerView.isVisible = false
                    internetError()
                    Log.i("MyLog", t.toString())
                }
            })
            if(inputEditText.text.isEmpty()){
                clearAllError()
                historyVisibility(true)
            }
        }

        val searchRunnable = Runnable { search() }

        fun searchDebounce() {
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }

        updateBtn.setOnClickListener {
            search()
        }

        tittleBackIcon.setNavigationOnClickListener {
            this.finish()
        }

        clearButton.setOnClickListener {
            clearAllError()
            inputEditText.setText("")
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?,start: Int,count: Int,after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()
                textSearch = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
                historyVisibility((inputEditText.hasFocus() && inputEditText.text.isEmpty()))
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        if(trackListHistory.isEmpty()){
            historyVisibility(false)
            clearAllError()
        }
    }


    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY, textSearch)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        textSearch = savedInstanceState.getString(KEY, "")
        inputEditText.setText(textSearch)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}
