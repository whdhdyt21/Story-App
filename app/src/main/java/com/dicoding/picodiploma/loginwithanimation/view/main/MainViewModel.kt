package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.StoryItem
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: UserRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus: LiveData<Boolean> get() = _logoutStatus

    val session: LiveData<UserModel> = userPreference.getSession().asLiveData()

    val stories: LiveData<PagingData<StoryItem>> = session
        .map { it.token }
        .switchMap { token ->
            repository.getStoriesPagingData(token)
                .cachedIn(viewModelScope)
                .onStart {}
                .catch { emit(PagingData.empty()) }
                .asLiveData()
        }

    fun logout() {
        viewModelScope.launch {
            runCatching {
                repository.logout()
                userPreference.logout()
            }.onSuccess {
                _logoutStatus.postValue(true)
            }.onFailure { e ->
                _logoutStatus.postValue(false)
                e.printStackTrace()
            }
        }
    }
}