package com.example.huerto_hogar.ui.auth

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar.data.LocalDataRepository
import com.example.huerto_hogar.data.model.User
import com.example.huerto_hogar.network.ApiResult
import com.example.huerto_hogar.network.repository.UserRepository
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

    private val userRepository = UserRepository()

    // 2. Crea el StateFlow para el estado de la UI
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    // 3. Propiedades para los campos de texto (con valores por defecto para facilitar testing)
    var email by mutableStateOf("admin@profesor.duoc.cl")
    var password by mutableStateOf("Admin*123")

    // 4. Función que ejecuta la acción de login
    fun login() {
        viewModelScope.launch {
            try {
                // Poner la UI en estado de carga
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                Log.d("LoginViewModel", "=== INICIO LOGIN ===")
                Log.d("LoginViewModel", "Email: $email")
                Log.d("LoginViewModel", "URL API: ${com.example.huerto_hogar.network.ApiConfig.USER_SERVICE_BASE_URL}")
                
                // Buscar usuario por email en MongoDB
                when (val result = userRepository.getUserByEmail(email)) {
                    is ApiResult.Success -> {
                        val usuario = result.data
                        Log.d("LoginViewModel", "✅ Usuario encontrado: ${usuario.nombre}, Tipo: ${usuario.idTipoUsuario}")
                        
                        // Verificar contraseña
                        if (usuario.password == password) {
                            // Convertir Usuario API a User local para compatibilidad
                            val userRole = when(usuario.idTipoUsuario) {
                                1 -> com.example.huerto_hogar.data.enums.UserRole.ADMIN
                                2 -> com.example.huerto_hogar.data.enums.UserRole.VENDEDOR
                                else -> com.example.huerto_hogar.data.enums.UserRole.CLIENTE
                            }
                            
                            val user = User(
                                id = usuario.id?.toLongOrNull() ?: 0L,
                                name = usuario.nombre,
                                email = usuario.email,
                                password = usuario.password,
                                registrationDate = java.util.Date(),
                                address = usuario.direccion,
                                phone = usuario.telefono,
                                comunaId = usuario.idComuna?.toLong() ?: 1L,
                                role = userRole
                            )
                            
                            // Establecer el usuario actual en el repositorio global
                            LocalDataRepository.setCurrentUser(user)
                            
                            Log.d("LoginViewModel", "Login exitoso para: ${user.name}")
                            
                            // Actualizar la UI con el éxito
                            _uiState.update { it.copy(isLoading = false, loginSuccess = user) }
                        } else {
                            Log.w("LoginViewModel", "Contraseña incorrecta")
                            _uiState.update { it.copy(isLoading = false, errorMessage = "Contraseña incorrecta") }
                        }
                    }
                    is ApiResult.Error -> {
                        Log.e("LoginViewModel", "❌ Error al buscar usuario")
                        Log.e("LoginViewModel", "Mensaje: ${result.message}")
                        Log.e("LoginViewModel", "Código: ${result.code}")
                        
                        val errorMsg = when (result.code) {
                            404 -> "Usuario no encontrado. Verifica el email."
                            500 -> "Error en el servidor. Intenta más tarde."
                            else -> "Error: ${result.message}"
                        }
                        
                        _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                    }
                    else -> {
                        Log.e("LoginViewModel", "Estado desconocido")
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error desconocido") }
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "❌ Excepción en login: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    // 5. Función para limpiar el estado después de una navegación
    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
