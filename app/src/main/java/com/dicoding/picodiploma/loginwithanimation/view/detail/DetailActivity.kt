package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import coil.ImageLoader
import coil.request.ImageRequest
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.loginwithanimation.data.response.StoryItem
import com.dicoding.picodiploma.loginwithanimation.R

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureToolbar()
        displayStory(getStoryFromIntent())
    }

    private fun configureToolbar() {
        with(binding.toolbarDetail) {
            setSupportActionBar(this)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                title = getString(R.string.detail_story_title)
                this.title = HtmlCompat.fromHtml(
                    "<font color='#FFFFFF'>${getString(R.string.create_story)}</font>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
            navigationIcon?.setTint(ContextCompat.getColor(this@DetailActivity, android.R.color.white))
            setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun getStoryFromIntent(): StoryItem {
        return StoryItem(
            id = intent.getStringExtra("id") ?: "",
            name = intent.getStringExtra("name") ?: "",
            description = intent.getStringExtra("description") ?: "",
            photoUrl = intent.getStringExtra("photo_url") ?: "",
            lat = intent.getDoubleExtra("lat", 0.0),
            lon = intent.getDoubleExtra("lon", 0.0),
            createdAt = intent.getStringExtra("createdAt") ?: ""
        )
    }

    private fun displayStory(story: StoryItem) {
        with(binding) {
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description

            val request = ImageRequest.Builder(this@DetailActivity)
                .data(story.photoUrl)
                .target(ivDetailPhoto)
                .build()

            ImageLoader(this@DetailActivity).enqueue(request)
        }
    }
}
