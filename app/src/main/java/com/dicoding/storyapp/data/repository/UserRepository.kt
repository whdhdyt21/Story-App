package com.dicoding.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.pref.UserPreference

class UserRepository private constructor(
    private val userPreference: UserPreference
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): LiveData<UserModel> {
        return userPreference.getSession().asLiveData()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        fun getInstance(userPreference: UserPreference) = UserRepository(userPreference)
    }
}