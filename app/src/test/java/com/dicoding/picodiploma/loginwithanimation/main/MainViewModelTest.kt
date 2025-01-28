package com.dicoding.picodiploma.loginwithanimation.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.StoryItem
import com.dicoding.picodiploma.loginwithanimation.utils.MainDispatcherRule
import com.dicoding.picodiploma.loginwithanimation.utils.SampleDataGenerator
import com.dicoding.picodiploma.loginwithanimation.utils.getOrAwaitValue
import com.dicoding.picodiploma.loginwithanimation.view.adapter.StoryAdapter
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel
import com.dicoding.picodiploma.loginwithanimation.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repository: UserRepository

    @Mock
    private lateinit var userPreference: UserPreference

    private val dummyStories = SampleDataGenerator.createSampleStories().listStory

    @Test
    fun `when getStories Should Not Null and Return Success`() = runTest {
        // Arrange
        val loginResponse = SampleDataGenerator.createSampleLoginResponse()
        val fakeUser = UserModel(
            email = loginResponse.loginResult.userId,
            token = loginResponse.loginResult.token,
            isLogin = true
        )
        val data: PagingData<StoryItem> = StoryPagingSource.snapshot(dummyStories)
        val expectedStories = MutableLiveData<PagingData<StoryItem>>()
        expectedStories.value = data

        Mockito.`when`(repository.getStoriesPagingData(fakeUser.token)).thenReturn(flowOf(data))
        Mockito.`when`(userPreference.getSession()).thenReturn(flowOf(fakeUser))

        val mainViewModel = MainViewModel(repository, userPreference)
        val actualStories: PagingData<StoryItem> = mainViewModel.stories.getOrAwaitValue()
        val differ = createAsyncPagingDataDiffer()
        differ.submitData(actualStories)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories, differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when no stories should return empty list`() = runTest {
        val emptyData: PagingData<StoryItem> = PagingData.from(emptyList())
        val loginResponse = SampleDataGenerator.createSampleLoginResponse()
        val fakeUser = UserModel(
            email = loginResponse.loginResult.userId,
            token = loginResponse.loginResult.token,
            isLogin = true
        )

        // Mock repository behavior
        Mockito.`when`(userPreference.getSession()).thenReturn(flowOf(fakeUser))
        Mockito.`when`(repository.getStoriesPagingData(fakeUser.token)).thenReturn(flowOf(emptyData))

        // Act
        val mainViewModel = MainViewModel(repository, userPreference)
        val actualStories: PagingData<StoryItem> = mainViewModel.stories.getOrAwaitValue()

        val differ = createAsyncPagingDataDiffer()
        differ.submitData(actualStories)
        val snapshot = differ.snapshot()
        Assert.assertEquals(0, snapshot.size)
    }

    @Test
    fun `test logout success`() = runTest {
        val loginResponse = SampleDataGenerator.createSampleLoginResponse()
        val fakeUser = UserModel(
            email = loginResponse.loginResult.userId,
            token = loginResponse.loginResult.token,
            isLogin = true
        )
        Mockito.`when`(userPreference.getSession()).thenReturn(flowOf(fakeUser))

        val result = MutableLiveData<Result<Unit>>().apply { value = Result.Success(Unit) }
        Mockito.`when`(repository.logout()).thenReturn(result)
        val mainViewModel = MainViewModel(repository, userPreference)
        mainViewModel.logout()
        val logoutStatus = mainViewModel.logoutStatus.getOrAwaitValue()
        Assert.assertTrue(logoutStatus)
    }

    private fun createAsyncPagingDataDiffer(): AsyncPagingDataDiffer<StoryItem> {
        return AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.StoryDiffCallback(),
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
    }
}

class StoryPagingSource : PagingSource<Int, StoryItem>() {
    companion object {
        fun snapshot(items: List<StoryItem>): PagingData<StoryItem> = PagingData.from(items)
    }

    override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
        return LoadResult.Page(emptyList(), null, null)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
