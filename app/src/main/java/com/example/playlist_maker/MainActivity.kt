package com.example.playlist_maker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val searchButton = findViewById<Button>(R.id.search_button)
        val mediaButton = findViewById<Button>(R.id.media_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        searchButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Вы нажали на кнопку 'Поиск'", Toast.LENGTH_SHORT).show()
        }
        mediaButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Вы нажали на кнопку 'Медиатека'", Toast.LENGTH_SHORT).show()
        }
        settingsButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Вы нажали на кнопку 'Настройки'", Toast.LENGTH_SHORT).show()
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}