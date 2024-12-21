package com.dicoding.storyapp.view.createstory

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository
import java.io.File

class CreateStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun addStory(description: String, imageFile: File) =
        storyRepository.addStory(description, imageFile)
}