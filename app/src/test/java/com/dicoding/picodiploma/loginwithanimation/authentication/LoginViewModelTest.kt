package com.dicoding.picodiploma.loginwithanimation.authentication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResult
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginViewModel
import com.dicoding.picodiploma.loginwithanimation.data.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockUserRepository: UserRepository

    private lateinit var loginViewModelUnderTest: LoginViewModel

    @Before
    fun initialize() {
        MockitoAnnotations.openMocks(this)
        loginViewModelUnderTest = LoginViewModel(mockUserRepository)
    }

    @Test
    fun `successful login should emit Success result`() {
        val emailInput = "test@example.com"
        val passwordInput = "password123"
        val loginData = LoginResult("user123", "token_abc", "test@example.com")
        val response = LoginResponse(false, loginData, "Login successful")
        val expectedResult = Result.Success(response)
        val liveDataMock = MutableLiveData<Result<LoginResponse>>().apply { value = expectedResult }

        Mockito.`when`(mockUserRepository.login(emailInput, passwordInput)).thenReturn(liveDataMock)

        val observer = Observer<Result<LoginResponse>> { result ->
            Assert.assertTrue(result is Result.Success)
            val success = result as Result.Success
            Assert.assertEquals(response, success.data)
            Assert.assertEquals("token_abc", success.data.loginResult.token)
        }

        loginViewModelUnderTest.login(emailInput, passwordInput).observeForever(observer)
    }

    @Test
    fun `failed login should emit Error result`() {
        val emailInput = "wrong@example.com"
        val passwordInput = "wrongpassword"
        val errorMsg = "Invalid credentials"
        val errorResult = Result.Error(errorMsg)

        val liveDataMock = MutableLiveData<Result<LoginResponse>>().apply { value = errorResult }

        Mockito.`when`(mockUserRepository.login(emailInput, passwordInput)).thenReturn(liveDataMock)

        val observer = Observer<Result<LoginResponse>> { result ->
            Assert.assertTrue(result is Result.Error)
            val error = result as Result.Error
            Assert.assertEquals(errorMsg, error.error)
        }

        loginViewModelUnderTest.login(emailInput, passwordInput).observeForever(observer)
    }

    @Test
    fun `saveSession should invoke saveSession in repository`() {
        val userSession = UserModel(
            email = "test@example.com",
            token = "token_abc",
            isLogin = true
        )

        loginViewModelUnderTest.saveSession(userSession)

        Mockito.verify(mockUserRepository).saveSession(userSession)
    }

    @Test
    fun `loading state should emit Loading result`() {
        val loadingResult = Result.Loading

        val liveDataMock = MutableLiveData<Result<LoginResponse>>().apply { value = loadingResult }

        Mockito.`when`(mockUserRepository.login("test@example.com", "password123")).thenReturn(liveDataMock)

        val observer = Observer<Result<LoginResponse>> { result ->
            Assert.assertTrue(result is Result.Loading)
        }

        loginViewModelUnderTest.login("test@example.com", "password123").observeForever(observer)
    }
}
