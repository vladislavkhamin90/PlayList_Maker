package com.example.playlist_maker.presentation.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.themeSwitcher.isChecked = viewModel.isDarkThemeEnabled()

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitched(isChecked)
        }

        binding.sharing.setOnClickListener {
            viewModel.onShareClicked()
        }

        binding.support.setOnClickListener {
            viewModel.onSupportClicked()
        }

        binding.agreement.setOnClickListener {
            viewModel.onAgreementClicked()
        }
    }

    private fun setupObservers() {
        viewModel.actionEvent.observe(viewLifecycleOwner) { intent ->
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}