package com.dicoding.picodiploma.loginwithanimation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.response.StoryItem

class StoryAdapter(private val onItemClick: (StoryItem) -> Unit) :
    PagingDataAdapter<StoryItem, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.tv_item_name)
        private val photoImage: ImageView = itemView.findViewById(R.id.iv_item_photo)
        private val descriptionText: TextView = itemView.findViewById(R.id.tv_description)

        fun bind(story: StoryItem) {
            nameText.text = story.name
            descriptionText.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .placeholder(R.drawable.ic_place_holder)
                .into(photoImage)
            itemView.setOnClickListener { onItemClick(story) }
        }
    }

    class StoryDiffCallback : DiffUtil.ItemCallback<StoryItem>() {
        override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem) = oldItem == newItem
    }
}
