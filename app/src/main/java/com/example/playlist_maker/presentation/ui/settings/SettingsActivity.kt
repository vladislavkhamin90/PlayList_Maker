package com.example.playlist_maker.presentation.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeSwitcher: SwitchMaterial

    private lateinit var vm: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        vm = ViewModelProvider(this,
            SettingsViewModelFactory(this)
        )[SettingsViewModel::class.java]

        val tittleBackIcon = findViewById<Toolbar>(R.id.tool_bar)
        tittleBackIcon.setNavigationOnClickListener {
            finish()
        }

        val shareLine = findViewById<TextView>(R.id.sharing)
        val supportLine = findViewById<TextView>(R.id.support)
        val agreementLine = findViewById<TextView>(R.id.agreement)
        themeSwitcher = findViewById(R.id.theme_switcher)

        shareLine.setOnClickListener{
            startActivity(Intent.createChooser(vm.getShareIntent(), vm.getShareString()))
        }

        supportLine.setOnClickListener{
            startActivity(Intent.createChooser(vm.getIntentEmail(), vm.getIntentUrl()))
        }

        agreementLine.setOnClickListener{
            startActivity(Intent.createChooser(vm.getUrlIntent(),vm.getOpenUrlString()))
        }

        setupViews()
        applyTheme()
    }

    private fun setupViews() {
        themeSwitcher.isChecked = vm.thumbChecked()

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            val theme = if (isChecked) vm.getDarkTheme() else vm.getLightTheme()
            vm.setTheme(theme)
            applyTheme()
        }
    }

    private fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if(vm.thumbChecked()){
                AppCompatDelegate.MODE_NIGHT_YES
            } else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}