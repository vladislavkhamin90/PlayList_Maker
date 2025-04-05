package com.example.playlist_maker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {
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
            this.finish()
        }

        val shareLine = findViewById<TextView>(R.id.sharing)
        val supportLine = findViewById<TextView>(R.id.support)
        val agreementLine = findViewById<TextView>(R.id.agreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)

        val app = applicationContext as App
        themeSwitcher.isChecked = app.isDarkTheme()

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            app.switchTheme(checked)
            recreate()
        }

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
    }
}