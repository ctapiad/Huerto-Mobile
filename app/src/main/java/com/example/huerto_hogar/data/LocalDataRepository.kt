package com.example.huerto_hogar.data

import com.example.huerto_hogar.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * LocalDataRepository - Simplificado para solo manejar sesión de usuario
 * 
 * Ya no hay base de datos local (Room eliminada).
 * Solo mantiene el estado del usuario actual en memoria.
 */
object LocalDataRepository {

    // --- DATOS DE SESIÓN ---
    private val _currentUser = MutableStateFlow<User?>(null) // El usuario actual. null si es invitado.
    val currentUser = _currentUser.asStateFlow()

    // --- MÉTODOS DE SESIÓN ---
    
    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }
    
    fun logout() {
        // Al cerrar sesión, simplemente ponemos el usuario actual en null
        _currentUser.value = null
        // NOTA: El carrito se limpia automáticamente en CartViewModel si es necesario
    }
}  