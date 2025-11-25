package com.example.huerto_hogar.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.huerto_hogar.data.LocalDataRepository
import com.example.huerto_hogar.data.model.User
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AuthViewModel
    private lateinit var mockApplication: Application

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockApplication = mockk(relaxed = true)
        mockkObject(LocalDataRepository)
        viewModel = AuthViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `currentUser debe reflejar usuario de LocalDataRepository`() {
        val currentUser = viewModel.currentUser
        assertNotNull(currentUser)
        assertEquals(LocalDataRepository.currentUser, currentUser)
    }

    @Test
    fun `logout debe llamar a LocalDataRepository logout`() {
        every { LocalDataRepository.logout() } just Runs
        
        viewModel.logout()
        
        verify { LocalDataRepository.logout() }
    }

    @Test
    fun `AuthViewModel debe inicializarse correctamente`() {
        assertNotNull(viewModel)
        assertNotNull(viewModel.currentUser)
    }
}
