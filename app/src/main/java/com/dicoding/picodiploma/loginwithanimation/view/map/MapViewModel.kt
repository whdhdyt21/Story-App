package com.dicoding.picodiploma.loginwithanimation.view.map

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository

class MapViewModel(private val storyRepository: UserRepository): ViewModel() {
    fun getStoriesWithLocation() = storyRepository.getStoriesWithLocation()
}