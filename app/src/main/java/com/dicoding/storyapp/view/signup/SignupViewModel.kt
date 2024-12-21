package com.dicoding.storyapp.view.signup

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.AuthRepository

class SignupViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) = authRepository.register(name, email, password)
}