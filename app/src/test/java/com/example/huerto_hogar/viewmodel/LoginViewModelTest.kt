package com.example.huerto_hogar.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.huerto_hogar.ui.auth.LoginViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests simplificados para LoginViewModel
 * 
 * NOTA: Estos tests verifican la estructura y estado inicial del ViewModel.
 * Los tests de integración completos con el UserRepository requieren 
 * configuración avanzada de mocking que está fuera del alcance de tests unitarios básicos.
 * 
 * Para probar la funcionalidad completa de login, se recomienda usar tests de integración
 * o instrumentados que se conecten a una API de prueba.
 */
@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var viewModel: LoginViewModel
    private lateinit var mockApplication: Application

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockApplication = mockk(relaxed = true)
        viewModel = LoginViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `estado inicial del viewmodel es correcto`() {
        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.loginSuccess)
        assertNull(state.errorMessage)
    }

    @Test
    fun `email y password tienen valores por defecto`() {
        // Then
        assertEquals("admin@profesor.duoc.cl", viewModel.email)
        assertEquals("Admin*123", viewModel.password)
    }

    @Test
    fun `se pueden actualizar email y password`() {
        // When
        viewModel.email = "nuevo@email.com"
        viewModel.password = "nuevaPassword"

        // Then
        assertEquals("nuevo@email.com", viewModel.email)
        assertEquals("nuevaPassword", viewModel.password)
    }

    @Test
    fun `resetState limpia el estado correctamente`() = runTest {
        // When
        viewModel.resetState()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.loginSuccess)
        assertNull(state.errorMessage)
    }
}
