package com.dicoding.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.storyapp.data.response.*
import com.dicoding.storyapp.data.retrofit.ApiService
import com.dicoding.storyapp.data.Result
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository private constructor(
    private val apiService: ApiService
) {

    private val registerResult = MediatorLiveData<Result<RegisterResponse>>()
    private val loginResult = MediatorLiveData<Result<LoginResponse>>()

    fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> {
        registerResult.value = Result.Loading
        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    registerResult.value = Result.Success(response.body()!!)
                } else {
                    val errorBody = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                    registerResult.value = Result.Error(errorBody.message ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registerResult.value = Result.Error(t.message ?: "Unknown error")
            }
        })
        return registerResult
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> {
        loginResult.value = Result.Loading
        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    loginResult.value = Result.Success(response.body()!!)
                } else {
                    val errorBody = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                    loginResult.value = Result.Error(errorBody.message ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult.value = Result.Error(t.message ?: "Unknown error")
            }
        })
        return loginResult
    }

    companion object {
        fun getInstance(apiService: ApiService) = AuthRepository(apiService)
    }
}