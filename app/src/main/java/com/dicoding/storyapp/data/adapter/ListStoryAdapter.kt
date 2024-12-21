package com.dicoding.storyapp.data.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.storyapp.data.DetailStory
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.databinding.ListItemBinding
import com.dicoding.storyapp.loadImage
import com.dicoding.storyapp.view.detail.DetailActivity

class ListStoryAdapter(private val listStory: List<ListStoryItem>) :
    RecyclerView.Adapter<ListStoryAdapter.ListStoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStoryViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListStoryViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size

    inner class ListStoryViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listStoryItem: ListStoryItem) {
            with(binding) {
                ivItemPhoto.loadImage(listStoryItem.photoUrl)
                tvItemName.text = listStoryItem.name
                tvItemDescription.text = listStoryItem.description

                itemView.setOnClickListener {
                    navigateToDetailActivity(itemView.context, listStoryItem)
                }
            }
        }

        private fun navigateToDetailActivity(context: Context, listStoryItem: ListStoryItem) {
            if (context is Activity) {
                val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context,
                    Pair(binding.ivItemPhoto, "photo"),
                    Pair(binding.tvItemName, "name"),
                    Pair(binding.tvItemDescription, "description")
                )

                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra(
                        DetailActivity.EXTRA_STORY,
                        DetailStory(
                            listStoryItem.id,
                            listStoryItem.name,
                            listStoryItem.description,
                            listStoryItem.photoUrl
                        )
                    )
                }

                context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }
}
