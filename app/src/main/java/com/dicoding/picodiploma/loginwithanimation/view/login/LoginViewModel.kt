package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.Result
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> {
        return userRepository.login(email, password)
    }

    fun saveSession(user: UserModel) {
        userRepository.saveSession(user)
    }
}
