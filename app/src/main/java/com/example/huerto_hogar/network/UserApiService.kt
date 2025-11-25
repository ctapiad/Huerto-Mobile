package com.example.huerto_hogar.network

import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz de servicios REST para el microservicio de Usuarios
 * Documentación: http://localhost:8081/api-docs
 */
interface UserApiService {
    
    /**
     * Obtener todos los usuarios
     */
    @GET("api/usuarios")
    suspend fun getAllUsers(): Response<List<Usuario>>
    
    /**
     * Obtener usuario por ID
     */
    @GET("api/usuarios/{id}")
    suspend fun getUserById(@Path("id") userId: String): Response<Usuario>
    
    /**
     * Obtener DTO de usuario por ID (sin password)
     */
    @GET("api/usuarios/{id}/dto")
    suspend fun getUserDtoById(@Path("id") userId: String): Response<UsuarioDto>
    
    /**
     * Obtener usuario por email
     */
    @GET("api/usuarios/email/{email}")
    suspend fun getUserByEmail(@Path(value = "email", encoded = false) email: String): Response<Usuario>
    
    /**
     * Buscar usuarios por nombre
     */
    @GET("api/usuarios/buscar/{nombre}")
    suspend fun searchUsersByName(@Path("nombre") name: String): Response<List<Usuario>>
    
    /**
     * Obtener usuarios por tipo
     */
    @GET("api/usuarios/tipo/{idTipoUsuario}")
    suspend fun getUsersByType(@Path("idTipoUsuario") userTypeId: Int): Response<List<Usuario>>
    
    /**
     * Crear nuevo usuario
     */
    @POST("api/usuarios")
    suspend fun createUser(@Body user: Usuario): Response<String>
    
    /**
     * Modificar usuario existente
     */
    @PUT("api/usuarios")
    suspend fun updateUser(@Body user: Usuario): Response<String>
    
    /**
     * Eliminar usuario por ID
     */
    @DELETE("api/usuarios/{id}")
    suspend fun deleteUser(@Path("id") userId: String): Response<String>
}

// DTOs según la API real
data class Usuario(
    val id: String? = null,
    val nombre: String,
    val email: String,
    val password: String,
    val fechaRegistro: String? = null,
    val direccion: String? = null,
    val telefono: Int? = null,
    val idComuna: Int? = null,
    val idTipoUsuario: Int? = null
)

data class UsuarioDto(
    val id: String,
    val nombre: String,
    val email: String,
    val fechaRegistro: String? = null,
    val direccion: String? = null,
    val telefono: Int? = null,
    val idComuna: Int? = null,
    val idTipoUsuario: Int? = null
)
