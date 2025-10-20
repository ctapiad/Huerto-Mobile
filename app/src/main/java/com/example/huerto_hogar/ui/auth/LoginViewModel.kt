package com.example.huerto_hogar.ui.auth

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar.database.repository.DatabaseRepository
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

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val databaseRepository = DatabaseRepository(application)

    // 2. Crea el StateFlow para el estado de la UI
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    // 3. Propiedades para los campos de texto
    var email by mutableStateOf("admin@profesor.duoc.cl")
    var password by mutableStateOf("Admin*123")

    // 4. Función que ejecuta la acción de login
    fun login() {
        viewModelScope.launch {
            try {
                // Poner la UI en estado de carga
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                // Inicializar la base de datos si es necesario
                databaseRepository.initializeWithLocalData()

                // Intentar login con la base de datos
                val user = databaseRepository.loginUser(email, password)

                if (user != null) {
                    // Establecer el usuario actual en el repositorio global
                    LocalDataRepository.setCurrentUser(user)
                    // Actualizar la UI con el éxito
                    _uiState.update { it.copy(isLoading = false, loginSuccess = user) }
                } else {
                    // Actualizar la UI con un error
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Email o contraseña incorrectos.") }
                }
            } catch (e: Exception) {
                // Manejar errores de base de datos
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al conectar con la base de datos: ${e.message}") }
            }
        }
    }

    // 5. Función para limpiar el estado después de una navegación
    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
