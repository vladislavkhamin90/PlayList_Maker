package com.example.playlist_maker.presentation.ui.media

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentCreatePlaylistBinding
import com.example.playlist_maker.presentation.ui.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlayListFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreatePlaylistViewModel by viewModel()

    private var isEditMode = false
    private var editPlaylistId: Long = -1
    private var hasChanges = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.setCoverImagePath(it.toString())
            loadImage(it)
            hasChanges = true
        }
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isEditMode) {
                navigateBack()
            } else {
                handleBackPress()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isEditMode = it.getBoolean(ARG_EDIT_MODE, false)
            editPlaylistId = it.getLong(ARG_PLAYLIST_ID, -1)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)

        (requireActivity() as MainActivity).showBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupObservers()
        setupClickListeners()
        setupTextWatchers()

        if (isEditMode && editPlaylistId != -1L) {
            viewModel.loadPlaylistForEditing(editPlaylistId)
            binding.toolbar.title = getString(R.string.edit_playlist_title)
            binding.createBtn.text = getString(R.string.save)
        } else {
            binding.toolbar.title = getString(R.string.new_playlist)
            binding.createBtn.text = getString(R.string.create)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            if (isEditMode) {
                navigateBack()
            } else {
                handleBackPress()
            }
        }
    }

    private fun setupObservers() {
        viewModel.playlistName.observe(viewLifecycleOwner) { name ->
            if (binding.playlistNameEditText.text.toString() != name) {
                binding.playlistNameEditText.setText(name)
                binding.playlistNameEditText.setSelection(name.length)
            }
            updateCreateButtonState()
        }

        viewModel.playlistDescription.observe(viewLifecycleOwner) { description ->
            if (binding.playlistDescriptionEditText.text.toString() != (description ?: "")) {
                binding.playlistDescriptionEditText.setText(description ?: "")
            }
        }

        viewModel.playlistCoverPath.observe(viewLifecycleOwner) { coverPath ->
            coverPath?.let {
                val uri = Uri.parse(it)
                loadImage(uri)
            }
        }

        viewModel.isCreateButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.createBtn.isEnabled = isEnabled
            if (isEnabled) {
                binding.createBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue_theme)
            } else {
                binding.createBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray_background)
            }
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupClickListeners() {
        binding.playlistImage.setOnClickListener {
            checkPermissionsAndOpenGallery()
        }

        binding.createBtn.setOnClickListener {
            saveChanges()
        }
    }

    private fun setupTextWatchers() {
        binding.playlistNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                hasChanges = true
                updateCreateButtonState()
            }
        })

        binding.playlistDescriptionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                hasChanges = true
            }
        })
    }

    private fun handleBackPress() {
        if (isEditMode) {
            navigateBack()
        } else {
            val name = binding.playlistNameEditText.text.toString()
            val description = binding.playlistDescriptionEditText.text.toString()
            val hasCover = viewModel.playlistCoverPath.value != null

            if (hasChanges || name.isNotBlank() || description.isNotBlank() || hasCover) {
                showExitDialog()
            } else {
                navigateBack()
            }
        }
    }

    private fun saveChanges() {
        val name = binding.playlistNameEditText.text.toString()
        val description = binding.playlistDescriptionEditText.text.toString()
        val coverPath = viewModel.playlistCoverPath.value

        if (name.isNotBlank()) {
            if (isEditMode) {
                viewModel.updatePlaylist(editPlaylistId, name, description, coverPath)
            } else {
                viewModel.createPlaylist(name, description, coverPath)
            }
        }
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }

    private fun checkPermissionsAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun loadImage(uri: Uri) {
        val cornerRadius = resources.getDimensionPixelSize(R.dimen.image_corner_radius)

        Glide.with(requireContext())
            .load(uri)
            .transform(RoundedCorners(cornerRadius))
            .placeholder(R.drawable.placeholder_big)
            .into(binding.playlistImage)
    }

    private fun updateCreateButtonState() {
        val name = binding.playlistNameEditText.text.toString()
        viewModel.validateName(name)
    }

    private fun showExitDialog() {
        val title = if (isEditMode) {
            R.string.exit_dialog_title
        } else {
            R.string.exit_dialog_title
        }

        val message = if (isEditMode) {
            R.string.exit_dialog_message
        } else {
            R.string.exit_dialog_message
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(title))
            .setMessage(getString(message))
            .setNegativeButton(getString(R.string.exit_dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.exit_dialog_confirm)) { dialog, _ ->
                navigateBack()
                dialog.dismiss()
            }
            .show()
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).showBottomNav()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_EDIT_MODE = "edit_mode"
        private const val ARG_PLAYLIST_ID = "playlist_id"

        fun newInstance(): CreatePlayListFragment {
            return CreatePlayListFragment()
        }

        fun newEditInstance(playlistId: Long): CreatePlayListFragment {
            val fragment = CreatePlayListFragment()
            val args = Bundle().apply {
                putBoolean(ARG_EDIT_MODE, true)
                putLong(ARG_PLAYLIST_ID, playlistId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}