package com.dicoding.storyapp.view.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.DetailStory
import com.dicoding.storyapp.databinding.ActivityDetailBinding
import com.dicoding.storyapp.loadImage
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        loadStoryDetails()
        setupWindowInsetsListener()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun loadStoryDetails() {
        val story = intent.getParcelableExtra<DetailStory>(EXTRA_STORY)
        if (story != null) {
            displayStoryDetails(story)
        } else {
            showErrorSnackbar()
        }
    }

    private fun displayStoryDetails(story: DetailStory) {
        binding.apply {
            progressBar.visibility = View.GONE
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            ivDetailPhoto.loadImage(story.photoUrl)
        }

        supportActionBar?.title = getString(R.string.story_title, story.name)
    }

    private fun showErrorSnackbar() {
        Snackbar.make(
            binding.root,
            getString(R.string.story_load_failed),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun setupWindowInsetsListener() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}