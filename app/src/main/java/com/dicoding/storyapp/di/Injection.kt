package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.AppExecutors
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.pref.dataStore
import com.dicoding.storyapp.data.repository.AuthRepository
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.dicoding.storyapp.data.db.StoryRoomDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(userPreference)
    }

    fun provideAuthRepository(): AuthRepository {
        return AuthRepository.getInstance(ApiConfig.getApiService())
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val appExecutors = AppExecutors()
        val storyDao = StoryRoomDatabase.getDatabase(context).storyDao()

        return StoryRepository.getInstance(apiService, appExecutors, storyDao)
    }
}
