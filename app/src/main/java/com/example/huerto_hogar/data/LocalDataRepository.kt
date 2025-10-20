package com.example.huerto_hogar.data

import android.content.Context
import com.example.huerto_hogar.data.enums.*
import com.example.huerto_hogar.data.model.*
import com.example.huerto_hogar.data.dto.*
import com.example.huerto_hogar.database.repository.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import java.util.Date
import java.util.concurrent.atomic.AtomicLong

object LocalDataRepository {
    
    // Instancia del repository de base de datos
    private var databaseRepository: DatabaseRepository? = null
    
    // Flag para determinar si usar base de datos o datos en memoria
    private var useDatabaseMode = false
    
    fun initialize(context: Context, useDatabase: Boolean = false) {
        if (useDatabase) {
            databaseRepository = DatabaseRepository(context)
            useDatabaseMode = true
        }
    }
    
    // Método para poblar la base de datos con los datos del LocalDataRepository
    suspend fun populateDatabaseFromLocalData() {
        databaseRepository?.initializeWithLocalData()
    }

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
        // NOTA: El carrito ahora se maneja en CartViewModel
        // Si necesitas limpiar el carrito al cerrar sesión, hazlo desde CartViewModel
    }

    // --- MÉTODOS DELEGADOS AL DATABASE REPOSITORY ---
    
    suspend fun login(email: String, password: String): User? {
        return databaseRepository?.loginUser(email, password)
    }
    
    suspend fun updateUser(user: User): Boolean {
        return try {
            databaseRepository?.updateUser(user)
            setCurrentUser(user) // Actualizar el usuario actual en memoria
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getCurrentUserOrders(): List<Pedido> {
        val user = currentUser.value ?: return emptyList()
        return databaseRepository?.let { repo ->
            // Como getOrdersByUser retorna Flow, necesitamos obtener el primer valor
            repo.getOrdersByUser(user.id).first()
        } ?: emptyList()
    }
    
    suspend fun getOrderById(orderId: Long): Pedido? {
        return databaseRepository?.getOrderById(orderId)
    }
    
    suspend fun getOrderDetails(orderId: Long): List<DetallePedido> {
        return databaseRepository?.getOrderDetails(orderId) ?: emptyList()
    }
    
    suspend fun getProductById(productId: String): Product? {
        return databaseRepository?.getProductById(productId)
    }
    
    suspend fun getOrderDeliveryFee(orderId: Long): Double {
        // Lógica de negocio: tarifa de entrega
        val order = getOrderById(orderId) ?: return 0.0
        return if (order.total >= 50000) 0.0 else 3000.0
    }
}  