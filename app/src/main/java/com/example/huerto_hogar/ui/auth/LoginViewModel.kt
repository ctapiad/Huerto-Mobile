package com.example.huerto_hogar.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar.data.LocalDataRepository
import com.example.huerto_hogar.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Define el estado de la UI (lo que la pantalla necesita saber)
data class LoginUiState(
    val isLoading: Boolean = false,
    val loginSuccess: User? = null,
    val errorMessage: String? = null
)

class LoginViewModel : ViewModel() {

    // 2. Crea el StateFlow para el estado de la UI
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    // 3. Propiedades para los campos de texto
    var email by mutableStateOf("admin@profesor.duoc.cl")
    var password by mutableStateOf("Admin*123")

    // 4. Función que ejecuta la acción de login
    fun login() {
        viewModelScope.launch {
            // Poner la UI en estado de carga
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val user = LocalDataRepository.login(email, password)

            if (user != null) {
                // Actualizar la UI con el éxito
                _uiState.update { it.copy(isLoading = false, loginSuccess = user) }
            } else {
                // Actualizar la UI con un error
                _uiState.update { it.copy(isLoading = false, errorMessage = "Email o contraseña incorrectos.") }
            }
        }
    }

    // 5. Función para limpiar el estado después de una navegación
    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
