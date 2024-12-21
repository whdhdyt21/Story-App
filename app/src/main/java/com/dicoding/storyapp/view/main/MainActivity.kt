package com.dicoding.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.adapter.ListStoryAdapter
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.view.ViewModelFactory
import com.dicoding.storyapp.view.createstory.CreateStoryActivity
import com.dicoding.storyapp.view.welcome.WelcomeActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeSession()
        setupActionBar()
        observeStories()
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                navigateToWelcomeActivity()
            }
        }
    }

    private fun navigateToWelcomeActivity() {
        Intent(this, WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }.also { startActivity(it) }
    }

    private fun setupActionBar() {
        binding.buttonAdd.setOnClickListener {
            startActivity(Intent(this, CreateStoryActivity::class.java))
        }
    }

    private fun observeStories() {
        viewModel.getStories().observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> showStories(result.data.listStory)
                is Result.Error -> showError(result.error)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showStories(stories: List<ListStoryItem>) {
        showLoading(false)
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = ListStoryAdapter(stories)
    }

    private fun showError(error: String) {
        showLoading(false)
        Snackbar.make(binding.root, error, Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.button_retry)) {
                observeStories() // Retry loading stories
            }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> handleLogout()
            R.id.action_language -> openLanguageSettings()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleLogout() {
        viewModel.logout()
        navigateToWelcomeActivity()
    }

    private fun openLanguageSettings() {
        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }
}