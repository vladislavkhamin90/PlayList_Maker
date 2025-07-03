package com.example.playlist_maker.presentation.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.playlist_maker.R
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeSwitcher: SwitchMaterial
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tittleBackIcon = findViewById<Toolbar>(R.id.tool_bar)
        tittleBackIcon.setNavigationOnClickListener {
            finish()
        }

        val shareLine = findViewById<TextView>(R.id.sharing)
        val supportLine = findViewById<TextView>(R.id.support)
        val agreementLine = findViewById<TextView>(R.id.agreement)
        themeSwitcher = findViewById(R.id.theme_switcher)

        themeSwitcher.isChecked = viewModel.isDarkThemeEnabled()

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitched(isChecked)
        }

        shareLine.setOnClickListener {
            viewModel.onShareClicked()
        }

        supportLine.setOnClickListener {
            viewModel.onSupportClicked()
        }

        agreementLine.setOnClickListener {
            viewModel.onAgreementClicked()
        }

        setupObservers()
        setupViews()
    }

    private fun setupObservers() {
        viewModel.actionEvent.observe(this, Observer { intent ->
            when (intent.action) {
                Intent.ACTION_SEND -> {
                    if (intent.type == "text/plain" && intent.data?.scheme == "mailto") {
                        startActivity(Intent.createChooser(intent, getString(R.string.send)))
                    } else {
                        startActivity(Intent.createChooser(intent, getString(R.string.share_app)))
                    }
                }
                Intent.ACTION_VIEW -> {
                    startActivity(Intent.createChooser(intent, getString(R.string.open_url)))
                }
            }
        })
    }

    private fun setupViews() {
        themeSwitcher.isChecked = viewModel.isDarkThemeEnabled()

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitched(isChecked)
        }
    }
}