package com.example.playlist_maker

import android.os.Bundle
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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText: EditText

    private val baseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val songs = retrofit.create(SongsApi::class.java)

    private val trackList: ArrayList<Track> = arrayListOf()

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

//        var trackListPlug = mutableListOf((
//            Track(
//                "Smells Like Teen Spirit",
//                "Nirvana",
//                "5:01",
//                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
//            Track(
//                "Billie Jean",
//                "Michael Jackson",
//                "4:35",
//                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
//            Track(
//                "Stayin' Alive",
//                "Bee Gees",
//                "4:10",
//                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
//            Track(
//                "Whole Lotta Love",
//                "Led Zeppelin",
//                "5:33",
//                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
//            Track(
//                "Sweet Child O'Mine",
//                "Guns N' Roses",
//                "5:03",
//                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
//            )

        val recyclerView = findViewById<RecyclerView>(R.id.recycle_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TrackAdapter(trackList)

        inputEditText = findViewById(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val tittleBackIcon = findViewById<Toolbar>(R.id.title)
        val mainView = findViewById<LinearLayout>(R.id.main)

        val inflater = LayoutInflater.from(this)
        val internetError = inflater.inflate(R.layout.internet_error_item, mainView, false)
        val searchError = inflater.inflate(R.layout.search_error_item, mainView, false)
        val updateBtn = internetError.findViewById<Button>(R.id.update_btn)

        internetError.isVisible = false
        searchError.isVisible = false
        mainView.addView(internetError)
        mainView.addView(searchError)



        fun internetError() {
            internetError.isVisible = true
            trackList.clear()
        }

        fun searchError(){
            searchError.isVisible = true
        }

        fun clearAllError() {
            internetError.isVisible = false
            searchError.isVisible = false
        }

        fun fillTrackList(response: Response<Songs>) {
            for (i in response.body()!!.result) {
                trackList.add(
                    Track(
                        i.trackName,
                        i.artistName,
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(i.trackTimeMillis)
                            .toString(),
                        i.artworkUrl100
                    )
                )
            }
        }

        fun search(){
            clearAllError()
            songs.search(inputEditText.text.toString()).enqueue(object : Callback<Songs> {
                override fun onResponse(
                    call: Call<Songs>,
                    response: Response<Songs>,
                ) {
//                    Log.i("MyLog", response.code().toString() + " ${response.body()?.result}")
                    // Не удаётся получить корректный ответ на запрос - при response.code() = 200 у меня
                    // response.body()?.result = null, хотя должен возвращать как я понял наполненый List<SongsResult>.

                    if (response.code() == 200 && response.body()?.result != null) {
                        fillTrackList(response)
                    } else {
                        searchError()
                    }
                }

                override fun onFailure(call: Call<Songs>, t: Throwable) {
                    internetError()
                    Log.i("MyLog", t.toString())
                }
            })
        }

        updateBtn.setOnClickListener{
            search()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        tittleBackIcon.setNavigationOnClickListener {
            this.finish()
        }

        clearButton.setOnClickListener {
            clearAllError()
            inputEditText.setText("")
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textSearch = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
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
}