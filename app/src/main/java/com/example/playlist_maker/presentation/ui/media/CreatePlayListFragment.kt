package com.example.playlist_maker.presentation.ui.media

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentCreatePlaylistBinding
import com.example.playlist_maker.domain.api.PlaylistInteractor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class CreatePlayListFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    private var hasUnsavedChanges: Boolean = false

    private val playlistInteractor: PlaylistInteractor by inject()

    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            loadSelectedImage(it)
            hasUnsavedChanges = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            checkForUnsavedChangesAndNavigate()
        }

        binding.createBtn.setOnClickListener {
            createPlaylist()
        }

        setupTextWatchers()
        updateCreateButtonState()
        setupImageSelection()
    }

    private fun createPlaylist() {
        val name = binding.playlistNameEditText.text.toString().trim()
        val description = binding.playlistDescriptionEditText.text.toString().trim().takeIf { it.isNotEmpty() }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val coverImagePath = selectedImageUri?.let { uri ->
                    copyImageToAppStorage(uri)
                }

                playlistInteractor.createPlaylist(name, description, coverImagePath)

                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.playlist_created_success, name),
                        Toast.LENGTH_LONG
                    ).show()
                    findNavController().navigateUp()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun copyImageToAppStorage(imageUri: Uri): String? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(imageUri)
            inputStream?.use { input ->
                val directory = File(requireContext().filesDir, "playlist_covers")
                if (!directory.exists()) {
                    directory.mkdirs()
                }

                val fileName = "cover_${UUID.randomUUID()}.jpg"
                val outputFile = File(directory, fileName)

                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }

                outputFile.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun setupImageSelection() {
        binding.playlistImage.setOnClickListener {
            openPhotoPicker()
        }
    }

    private fun openPhotoPicker() {
        photoPickerLauncher.launch(null)
    }

    private fun loadSelectedImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.placeholder_big)
            .centerCrop()
            .into(binding.playlistImage)
    }

    private fun setupTextWatchers() {
        binding.playlistNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateCreateButtonState()
                hasUnsavedChanges = true
            }
        })

        binding.playlistDescriptionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    hasUnsavedChanges = true
                }
            }
        })
    }

    private fun updateCreateButtonState() {
        val isNameEmpty = binding.playlistNameEditText.text?.toString().isNullOrEmpty()
        binding.createBtn.isEnabled = !isNameEmpty

        if (isNameEmpty) {
            binding.createBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
        } else {
            binding.createBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_theme))
        }
    }

    private fun checkForUnsavedChangesAndNavigate() {
        if (hasUnsavedChanges) {
            showExitConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.exit_dialog_title))
            .setMessage(getString(R.string.exit_dialog_message))
            .setPositiveButton(getString(R.string.exit_dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.exit_dialog_confirm)) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}