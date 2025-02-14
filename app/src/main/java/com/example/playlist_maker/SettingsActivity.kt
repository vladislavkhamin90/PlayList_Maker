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

        shareLine.setOnClickListener{
            val url = Uri.parse("https://practicum.yandex.ru/profile/android-developer/")
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, url)
            startActivity(Intent.createChooser(intent, "Поделиться ссылкой"))
        }

        supportLine.setOnClickListener{
            val intent = Intent(Intent.ACTION_SEND)
            val title = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
            val text = "Спасибо разработчикам и разработчицам за крутое приложение"
            val mail = "vladislavkhamin90@gmail.com"
            intent.putExtra(Intent.EXTRA_EMAIL, mail)
            intent.putExtra(Intent.EXTRA_SUBJECT, title)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.data = Uri.parse("mailto:")
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Send Email"))
        }

        agreementLine.setOnClickListener{
            val url = Uri.parse("https://yandex.ru/legal/practicum_offer/")
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(Intent.createChooser(intent,"Открыть ссылку"))
        }
    }
}