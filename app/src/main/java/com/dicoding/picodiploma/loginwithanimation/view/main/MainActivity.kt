package com.dicoding.picodiploma.loginwithanimation.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.adapter.StoryAdapter
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailActivity
import com.dicoding.picodiploma.loginwithanimation.view.map.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupSwipeRefresh()
        setupRecyclerView()
        setupActions()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        handleCreateActivityIntent()
    }

    private fun setupToolbar() {
        with(binding.toolbarmain) {
            setSupportActionBar(this)
            supportActionBar?.title = getString(R.string.app_name)
            setTitleTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorWhite))
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupRecyclerView() {
        adapter = StoryAdapter { storyItem ->
            startActivity(Intent(this, DetailActivity::class.java).apply {
                putExtra("id", storyItem.id)
                putExtra("name", storyItem.name)
                putExtra("description", storyItem.description)
                putExtra("photo_url", storyItem.photoUrl)
            })
        }
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            binding.loadingIndicator.visibility = if (loadState.refresh is LoadState.Loading) View.VISIBLE else View.GONE
            handleEmptyState()
        }
    }

    private fun handleEmptyState() {
        val isNetworkAvailable = isNetworkAvailable()
        val isEmpty = adapter.itemCount == 0

        with(binding.emptyMessage) {
            visibility = if (isEmpty) View.VISIBLE else View.GONE
            text = if (!isNetworkAvailable) {
                getString(R.string.no_internet_connection)
            } else {
                getString(R.string.no_data_available)
            }
        }
        binding.rvStories.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun setupActions() {
        binding.AddStoryButton.setOnClickListener {
            startActivity(Intent(this, CreateActivity::class.java))
        }
        binding.mapButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.session.observe(this) { user ->
            if (user.token.isEmpty()) navigateToWelcome()
        }

        viewModel.stories.observe(this) { pagingData ->
            lifecycleScope.launch {
                adapter.submitData(pagingData)
                handleEmptyState()
            }
        }

        viewModel.logoutStatus.observe(this) { isLoggedOut ->
            if (isLoggedOut) navigateToWelcome()
            else showToast(getString(R.string.logout_failed))
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout_confirmation))
            .setMessage(getString(R.string.logout_confirmation_message))
            .setPositiveButton(getString(R.string.btn_yes)) { _, _ ->
                viewModel.logout()
            }
            .setNegativeButton(getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun navigateToWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }

    private fun handleCreateActivityIntent() {
        if (intent.getBooleanExtra("isFromCreateActivity", false)) {
            adapter.refresh()
            adapter.addLoadStateListener { loadState ->
                if (loadState.refresh is LoadState.NotLoading) {
                    binding.rvStories.scrollToPosition(0)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}