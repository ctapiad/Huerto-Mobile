package com.example.huerto_hogar.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.huerto_hogar.model.UsuarioUIState
import com.example.huerto_hogar.network.ApiResult
import com.example.huerto_hogar.network.repository.UserRepository
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
class UsuarioViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: UsuarioViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkConstructor(UserRepository::class)
        viewModel = UsuarioViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `estado inicial debe estar vacio`() {
        val estado = viewModel.estado.value
        assertEquals("", estado.nombre)
        assertEquals("", estado.email)
        assertEquals("", estado.password)
        assertEquals("", estado.direccion)
        assertEquals(0, estado.telefono)
        assertEquals(1, estado.idComuna)
        assertEquals(0, estado.idTipoUsuario)  // Valor inicial es 0, no 3
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `onNombreChange debe actualizar nombre`() {
        viewModel.onNombreChange("Juan Pérez")
        assertEquals("Juan Pérez", viewModel.estado.value.nombre)
    }

    @Test
    fun `onEmailChange debe actualizar email`() {
        viewModel.onEmailChange("juan@example.com")
        assertEquals("juan@example.com", viewModel.estado.value.email)
    }

    @Test
    fun `onPasswordChange debe actualizar password`() {
        viewModel.onPasswordChange("password123")
        assertEquals("password123", viewModel.estado.value.password)
    }

    @Test
    fun `onDireccionChange debe actualizar direccion`() {
        viewModel.onDireccionChange("Calle 123")
        assertEquals("Calle 123", viewModel.estado.value.direccion)
    }

    @Test
    fun `onTelefonoChange debe actualizar telefono`() {
        viewModel.onTelefonoChange("912345678")
        assertEquals(912345678, viewModel.estado.value.telefono)
    }

    @Test
    fun `onIdComunaChange debe actualizar idComuna`() {
        viewModel.onIdComunaChange(5)
        assertEquals(5, viewModel.estado.value.idComuna)
    }

    @Test
    fun `onIdTipoUsuarioChange debe actualizar idTipoUsuario`() {
        viewModel.onIdTipoUsuarioChange(2)
        assertEquals(2, viewModel.estado.value.idTipoUsuario)
    }

    @Test
    fun `validarFormulario debe fallar con nombre vacio`() {
        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("123456")
        viewModel.onIdTipoUsuarioChange(2)
        
        val resultado = viewModel.validarFormulario()
        
        assertFalse(resultado)
        assertNotNull(viewModel.estado.value.errores.nombre)
    }

    @Test
    fun `validarFormulario debe fallar con email invalido`() {
        viewModel.onNombreChange("Juan Pérez")
        viewModel.onEmailChange("email_invalido")
        viewModel.onPasswordChange("123456")
        viewModel.onIdTipoUsuarioChange(2)
        
        val resultado = viewModel.validarFormulario()
        
        assertFalse(resultado)
        assertNotNull(viewModel.estado.value.errores.email)
    }

    @Test
    fun `validarFormulario debe fallar con password corto`() {
        viewModel.onNombreChange("Juan Pérez")
        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("123")
        viewModel.onIdTipoUsuarioChange(2)
        
        val resultado = viewModel.validarFormulario()
        
        assertFalse(resultado)
        assertNotNull(viewModel.estado.value.errores.password)
    }

    @Test
    fun `validarFormulario debe fallar con tipo de usuario invalido`() {
        viewModel.onNombreChange("Juan Pérez")
        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("123456")
        viewModel.onIdTipoUsuarioChange(0)
        
        val resultado = viewModel.validarFormulario()
        
        assertFalse(resultado)
        assertNotNull(viewModel.estado.value.errores.idTipoUsuario)
    }

    @Test
    fun `limpiarFormulario debe resetear estado`() {
        viewModel.onNombreChange("Juan Pérez")
        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("123456")
        
        viewModel.limpiarFormulario()
        
        val estado = viewModel.estado.value
        assertEquals("", estado.nombre)
        assertEquals("", estado.email)
        assertEquals("", estado.password)
    }

    @Test
    fun `crear usuario debe validar formulario primero`() = runTest {
        var errorCalled = false
        
        // No llenar datos (formulario inválido)
        viewModel.crearUsuario(
            onSuccess = { },
            onError = { errorCalled = true }
        )
        
        assertTrue(errorCalled)
    }

    @Test
    fun `actualizar usuario debe validar formulario primero`() = runTest {
        var errorCalled = false
        
        // No llenar datos (formulario inválido)
        viewModel.actualizarUsuario(
            id = "123",
            onSuccess = { },
            onError = { errorCalled = true }
        )
        
        assertTrue(errorCalled)
    }
}
