package com.dicoding.storyapp.data.retrofit

import com.dicoding.storyapp.data.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("stories")
    fun getStories(): Call<StoryResponse>

    @GET("stories/{id}")
    fun getDetailStory(
        @Path("id") id: Int
    ): Call<DetailStoryResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<AddStoryResponse>
}