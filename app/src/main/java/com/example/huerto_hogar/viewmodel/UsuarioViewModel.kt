package com.example.huerto_hogar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar.model.UsuarioErrores
import com.example.huerto_hogar.model.UsuarioUIState
import com.example.huerto_hogar.network.Usuario
import com.example.huerto_hogar.network.repository.UserRepository
import com.example.huerto_hogar.network.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsuarioViewModel : ViewModel() {

    private val _estado = MutableStateFlow(UsuarioUIState())
    val estado: StateFlow<UsuarioUIState> = _estado

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _operationResult = MutableStateFlow<String?>(null)
    val operationResult: StateFlow<String?> = _operationResult

    private val userRepository = UserRepository()

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onEmailChange(valor: String) {
        _estado.update { it.copy(email = valor, errores = it.errores.copy(email = null)) }
    }

    fun onPasswordChange(valor: String) {
        _estado.update { it.copy(password = valor, errores = it.errores.copy(password = null)) }
    }

    fun onDireccionChange(valor: String) {
        _estado.update {
            it.copy(
                direccion = valor,
                errores = it.errores.copy(direccion = null)
            )
        }
    }

    fun onTelefonoChange(valor: String) {
        val telefonoInt = valor.toIntOrNull() ?: 0
        _estado.update {
            it.copy(
                telefono = telefonoInt,
                errores = it.errores.copy(telefono = null)
            )
        }
    }

    fun onIdComunaChange(valor: Int) {
        _estado.update {
            it.copy(
                idComuna = valor,
                errores = it.errores.copy(idComuna = null)
            )
        }
    }

    fun onIdTipoUsuarioChange(valor: Int) {
        _estado.update {
            it.copy(
                idTipoUsuario = valor,
                errores = it.errores.copy(idTipoUsuario = null)
            )
        }
    }

    fun validarFormulario(esEdicion: Boolean = false): Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            nombre = when {
                estadoActual.nombre.isBlank() -> "El nombre no puede estar vacío"
                estadoActual.nombre.length < 3 -> "El nombre debe tener al menos 3 caracteres"
                else -> null
            },
            email = when {
                estadoActual.email.isBlank() -> "El email no puede estar vacío"
                !estadoActual.email.contains("@") -> "Email inválido"
                !estadoActual.email.contains(".") -> "Email inválido"
                else -> null
            },
            password = when {
                // Para edición, password puede estar vacío (mantiene el actual)
                !esEdicion && estadoActual.password.isBlank() -> "La contraseña no puede estar vacía"
                estadoActual.password.isNotBlank() && estadoActual.password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
                else -> null
            },
            direccion = null, // Opcional
            telefono = when {
                estadoActual.telefono.toString().isNotEmpty() && estadoActual.telefono <= 0 -> "Teléfono inválido"
                else -> null
            },
            idComuna = null, // Opcional - se usa valor por defecto
            idTipoUsuario = when {
                estadoActual.idTipoUsuario <= 0 -> "Debe seleccionar un tipo de usuario"
                else -> null
            }
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.email,
            errores.password,
            errores.direccion,
            errores.telefono,
            errores.idComuna,
            errores.idTipoUsuario
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }

        return !hayErrores
    }

    fun cargarUsuario(usuario: Usuario) {
        _estado.value = UsuarioUIState(
            nombre = usuario.nombre,
            email = usuario.email,
            password = "", // No cargamos el password por seguridad
            direccion = usuario.direccion ?: "",
            telefono = usuario.telefono ?: 0,
            idComuna = usuario.idComuna ?: 1,
            idTipoUsuario = usuario.idTipoUsuario ?: 3,
            errores = UsuarioErrores()
        )
    }

    fun limpiarFormulario() {
        _estado.value = UsuarioUIState()
        _operationResult.value = null
    }

    fun crearUsuario(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!validarFormulario(esEdicion = false)) {
            onError("Por favor corrija los errores en el formulario")
            return
        }

        val estadoActual = _estado.value
        
        viewModelScope.launch {
            _isLoading.value = true
            
            when (val result = userRepository.createUser(
                nombre = estadoActual.nombre,
                email = estadoActual.email,
                password = estadoActual.password,
                direccion = estadoActual.direccion.ifEmpty { null },
                telefono = if (estadoActual.telefono > 0) estadoActual.telefono else null,
                idComuna = estadoActual.idComuna,
                idTipoUsuario = estadoActual.idTipoUsuario
            )) {
                is ApiResult.Success -> {
                    _operationResult.value = "Usuario creado exitosamente"
                    limpiarFormulario()
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _operationResult.value = result.message
                    onError(result.message)
                }
                else -> {}
            }
            
            _isLoading.value = false
        }
    }

    fun actualizarUsuario(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!validarFormulario(esEdicion = true)) {
            onError("Por favor corrija los errores en el formulario")
            return
        }

        val estadoActual = _estado.value
        
        viewModelScope.launch {
            _isLoading.value = true
            
            when (val result = userRepository.updateUser(
                id = id,
                nombre = estadoActual.nombre,
                email = estadoActual.email,
                password = if (estadoActual.password.isNotEmpty()) estadoActual.password else null,
                direccion = estadoActual.direccion.ifEmpty { null },
                telefono = if (estadoActual.telefono > 0) estadoActual.telefono else null,
                idComuna = estadoActual.idComuna,
                idTipoUsuario = estadoActual.idTipoUsuario
            )) {
                is ApiResult.Success -> {
                    _operationResult.value = "Usuario actualizado exitosamente"
                    limpiarFormulario()
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _operationResult.value = result.message
                    onError(result.message)
                }
                else -> {}
            }
            
            _isLoading.value = false
        }
    }
}
