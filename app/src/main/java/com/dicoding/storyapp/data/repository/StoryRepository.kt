package com.dicoding.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.storyapp.AppExecutors
import com.dicoding.storyapp.data.db.Story
import com.dicoding.storyapp.data.db.StoryDao
import com.dicoding.storyapp.data.response.*
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val appExecutors: AppExecutors,
    private val storyDao: StoryDao
) {

    private val getStoriesResult = MediatorLiveData<Result<StoryResponse>>()
    private val getDetailStoryResult = MediatorLiveData<Result<DetailStoryResponse>>()
    private val addStoryResult = MediatorLiveData<Result<AddStoryResponse>>()

    fun getStories(): LiveData<Result<StoryResponse>> {
        getStoriesResult.value = Result.Loading
        val client = apiService.getStories()
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val stories = response.body()?.listStory ?: emptyList()
                    val storiesList = stories.map {
                        Story(storyId = it.id, name = it.name, description = it.description, photoUrl = it.photoUrl)
                    }
                    appExecutors.diskIO.execute {
                        storyDao.deleteAllStories()
                        storyDao.insertAll(storiesList)
                    }
                } else {
                    val errorBody = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                    getStoriesResult.value = Result.Error(errorBody.message ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                getStoriesResult.value = Result.Error(t.message ?: "Unknown error")
            }
        })

        val localData = storyDao.getAllStories()
        getStoriesResult.addSource(localData) {
            getStoriesResult.value = Result.Success(StoryResponse(listStory = it.map { story ->
                ListStoryItem(id = story.storyId, name = story.name, description = story.description, photoUrl = story.photoUrl)
            }))
        }

        return getStoriesResult
    }

    fun getDetailStory(id: Int): LiveData<Result<DetailStoryResponse>> {
        getDetailStoryResult.value = Result.Loading
        val client = apiService.getDetailStory(id)
        client.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(call: Call<DetailStoryResponse>, response: Response<DetailStoryResponse>) {
                if (response.isSuccessful) {
                    getDetailStoryResult.value = Result.Success(response.body()!!)
                } else {
                    val errorBody = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                    getDetailStoryResult.value = Result.Error(errorBody.message ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                getDetailStoryResult.value = Result.Error(t.message ?: "Unknown error")
            }
        })
        return getDetailStoryResult
    }

    fun addStory(description: String, imageFile: File): LiveData<Result<AddStoryResponse>> {
        addStoryResult.value = Result.Loading
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

        val client = apiService.addStory(requestBody, multipartBody)
        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(call: Call<AddStoryResponse>, response: Response<AddStoryResponse>) {
                if (response.isSuccessful) {
                    addStoryResult.value = Result.Success(response.body()!!)
                } else {
                    val errorBody = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                    addStoryResult.value = Result.Error(errorBody.message ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                addStoryResult.value = Result.Error(t.message ?: "Unknown error")
            }
        })

        return addStoryResult
    }

    companion object {
        fun getInstance(apiService: ApiService, appExecutors: AppExecutors, storyDao: StoryDao) =
            StoryRepository(apiService, appExecutors, storyDao)
    }
}