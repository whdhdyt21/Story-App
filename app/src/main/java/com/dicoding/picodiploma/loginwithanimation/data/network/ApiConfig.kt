package com.dicoding.picodiploma.loginwithanimation.data.network

import android.content.Context
import android.content.SharedPreferences
import com.dicoding.picodiploma.loginwithanimation.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private fun createHttpClient(authToken: String?): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val builder = OkHttpClient.Builder().addInterceptor(logger)

        authToken?.takeIf { it.isNotBlank() }?.let {
            builder.addInterceptor(AuthInterceptor(it))
        }

        return builder.build()
    }

    fun getApiService(context: Context): ApiService {
        val preferences: SharedPreferences = context.getSharedPreferences("onSignIn", Context.MODE_PRIVATE)
        val token = preferences.getString("token", null)
        val client = createHttpClient(token)

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
