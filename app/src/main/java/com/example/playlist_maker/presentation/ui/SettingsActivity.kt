package com.example.playlist_maker.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlist_maker.Creator
import com.example.playlist_maker.R
import com.example.playlist_maker.domain.models.Theme
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tittleBackIcon = findViewById<Toolbar>(R.id.title)
        tittleBackIcon.setNavigationOnClickListener {
            finish()
        }

        val shareLine = findViewById<TextView>(R.id.sharing)
        val supportLine = findViewById<TextView>(R.id.support)
        val agreementLine = findViewById<TextView>(R.id.agreement)
        themeSwitcher = findViewById(R.id.theme_switcher)

        shareLine.setOnClickListener{
            val urlAndroidDev = getString(R.string.url_android_dev)
            val share = getString(R.string.share_app)
            val url = Uri.parse(urlAndroidDev)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, url)
            startActivity(Intent.createChooser(intent, share))
        }

        supportLine.setOnClickListener{
            val intent = Intent(Intent.ACTION_SEND)
            val title = getString(R.string.title)
            val text = getString(R.string.text)
            val mail = getString(R.string.mail)
            val send = getString(R.string.send)
            val mailto = getString(R.string.mailto)
            val textPlain = getString(R.string.text_plain)
            intent.putExtra(Intent.EXTRA_EMAIL, mail)
            intent.putExtra(Intent.EXTRA_SUBJECT, title)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.data = Uri.parse(mailto)
            intent.type = textPlain
            startActivity(Intent.createChooser(intent, send))
        }

        agreementLine.setOnClickListener{
            val offer = getString(R.string.offer)
            val open = getString(R.string.open_url)
            val url = Uri.parse(offer)
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(Intent.createChooser(intent,open))
        }

        setupViews()
        applyCurrentTheme()
    }

    private fun setupViews() {
        themeSwitcher.isChecked = Creator.provideThemeUseCase(this).getTheme() == Theme.DARK

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            val theme = if (isChecked) Theme.DARK else Theme.LIGHT
            Creator.provideThemeUseCase(this).setTheme(theme)
            applyTheme(theme)
        }
    }

    private fun applyCurrentTheme() {
        applyTheme(Creator.provideThemeUseCase(this).getTheme())
    }

    private fun applyTheme(theme: Theme) {
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}