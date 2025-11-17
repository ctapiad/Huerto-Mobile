package com.example.huerto_hogar.network.repository

import com.example.huerto_hogar.network.*

/**
 * Repositorio para operaciones de usuario con la API REST
 */
class UserRepository {
    
    private val apiService = UserApiClient.apiService
    
    /**
     * Obtener todos los usuarios
     */
    suspend fun getAllUsers(): ApiResult<List<Usuario>> {
        return safeApiCall {
            apiService.getAllUsers()
        }
    }
    
    /**
     * Obtener usuario por ID
     */
    suspend fun getUserById(userId: String): ApiResult<Usuario> {
        return safeApiCall {
            apiService.getUserById(userId)
        }
    }
    
    /**
     * Obtener DTO de usuario por ID (sin password)
     */
    suspend fun getUserDtoById(userId: String): ApiResult<UsuarioDto> {
        return safeApiCall {
            apiService.getUserDtoById(userId)
        }
    }
    
    /**
     * Obtener usuario por email
     */
    suspend fun getUserByEmail(email: String): ApiResult<Usuario> {
        return safeApiCall {
            apiService.getUserByEmail(email)
        }
    }
    
    /**
     * Login de usuario (buscar por email y validar password)
     */
    suspend fun login(email: String, password: String): ApiResult<Usuario> {
        return when (val result = safeApiCall { apiService.getUserByEmail(email) }) {
            is ApiResult.Success -> {
                val user = result.data
                if (user.password == password) {
                    ApiResult.Success(user)
                } else {
                    ApiResult.Error("Contraseña incorrecta")
                }
            }
            is ApiResult.Error -> result
            is ApiResult.Loading -> result
        }
    }
    
    /**
     * Buscar usuarios por nombre
     */
    suspend fun searchUsersByName(name: String): ApiResult<List<Usuario>> {
        return safeApiCall {
            apiService.searchUsersByName(name)
        }
    }
    
    /**
     * Obtener usuarios por tipo
     */
    suspend fun getUsersByType(userTypeId: Int): ApiResult<List<Usuario>> {
        return safeApiCall {
            apiService.getUsersByType(userTypeId)
        }
    }
    
    /**
     * Crear nuevo usuario (registro)
     */
    suspend fun createUser(
        nombre: String,
        email: String,
        password: String,
        direccion: String? = null,
        telefono: Int? = null,
        idComuna: Int? = null,
        idTipoUsuario: Int? = null
    ): ApiResult<String> {
        return safeApiCall {
            apiService.createUser(
                Usuario(
                    nombre = nombre,
                    email = email,
                    password = password,
                    direccion = direccion,
                    telefono = telefono,
                    idComuna = idComuna,
                    idTipoUsuario = idTipoUsuario
                )
            )
        }
    }
    
    /**
     * Actualizar usuario existente
     */
    suspend fun updateUser(
        id: String,
        nombre: String? = null,
        email: String? = null,
        password: String? = null,
        direccion: String? = null,
        telefono: Int? = null,
        idComuna: Int? = null,
        idTipoUsuario: Int? = null
    ): ApiResult<String> {
        // Primero obtener el usuario actual
        return when (val currentUser = safeApiCall { apiService.getUserById(id) }) {
            is ApiResult.Success -> {
                val user = currentUser.data
                safeApiCall {
                    apiService.updateUser(
                        Usuario(
                            id = id,
                            nombre = nombre ?: user.nombre,
                            email = email ?: user.email,
                            password = password ?: user.password,
                            direccion = direccion ?: user.direccion,
                            telefono = telefono ?: user.telefono,
                            idComuna = idComuna ?: user.idComuna,
                            idTipoUsuario = idTipoUsuario ?: user.idTipoUsuario,
                            fechaRegistro = user.fechaRegistro
                        )
                    )
                }
            }
            is ApiResult.Error -> currentUser
            is ApiResult.Loading -> currentUser
        }
    }
    
    /**
     * Eliminar usuario por ID
     */
    suspend fun deleteUser(userId: String): ApiResult<String> {
        return safeApiCall {
            apiService.deleteUser(userId)
        }
    }
}
