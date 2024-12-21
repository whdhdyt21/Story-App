package com.dicoding.storyapp.view.createstory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.ActivityCreateStoryBinding
import com.dicoding.storyapp.view.ViewModelFactory
import com.dicoding.storyapp.view.main.MainActivity
import com.dicoding.storyapp.uriToFile
import com.dicoding.storyapp.reduceFileImage
import com.dicoding.storyapp.getImageUri
import com.google.android.material.snackbar.Snackbar

class CreateStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoryBinding
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<CreateStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupActionListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            title = getString(R.string.story_image)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupActionListeners() {
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { addStory() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        if (currentImageUri != null) {
            launcherIntentCamera.launch(currentImageUri!!)
        } else {
            // Handle the case when `getImageUri` returns `null`
            // For instance, you could show a toast or log a message
            Log.e("CreateStoryActivity", "Failed to get image URI")
        }
    }


    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) showImage() else currentImageUri = null
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let { currentImageUri = it; showImage() }
    }

    private fun addStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()

            viewModel.addStory(description, imageFile).observe(this) { result ->
                handleStoryResult(result)
            }

        } ?: showToast(getString(R.string.error_no_image_selected))
    }

    private fun handleStoryResult(result: Result<*>) {
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> handleSuccess()
            is Result.Error -> handleError(result.error)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.uploadButton.isEnabled = !isLoading
    }

    private fun handleSuccess() {
        showLoading(false)
        showToast("Story added")
        navigateToMainActivity()
    }

    private fun handleError(errorMessage: String) {
        showLoading(false)
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showImage() {
        currentImageUri?.let { binding.ivPhoto.setImageURI(it) }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}