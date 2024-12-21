package com.dicoding.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.repository.AuthRepository
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.di.Injection
import com.dicoding.storyapp.view.createstory.CreateStoryViewModel
import com.dicoding.storyapp.view.login.LoginViewModel
import com.dicoding.storyapp.view.main.MainViewModel
import com.dicoding.storyapp.view.signup.SignupViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MainViewModel::class.java -> MainViewModel(userRepository, storyRepository) as T
            LoginViewModel::class.java -> LoginViewModel(userRepository, authRepository) as T
            SignupViewModel::class.java -> SignupViewModel(authRepository) as T
            CreateStoryViewModel::class.java -> CreateStoryViewModel(storyRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            val userRepository = Injection.provideRepository(context)
            val authRepository = Injection.provideAuthRepository()
            val storyRepository = Injection.provideStoryRepository(context)
            return ViewModelFactory(userRepository, authRepository, storyRepository)
        }
    }
}