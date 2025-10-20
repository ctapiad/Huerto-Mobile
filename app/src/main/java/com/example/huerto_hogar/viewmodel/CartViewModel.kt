package com.example.huerto_hogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar.data.LocalDataRepository
import com.example.huerto_hogar.data.dto.CartItem
import com.example.huerto_hogar.data.enums.OrderStatus
import com.example.huerto_hogar.data.model.*
import com.example.huerto_hogar.database.repository.DatabaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class CartViewModel(application: Application) : AndroidViewModel(application) {
    
    private val databaseRepository = DatabaseRepository(application)
    
    // Estados del carrito - AHORA MANEJADOS AQUÍ
    private val _shoppingCart = MutableStateFlow<MutableMap<String, CartItem>>(mutableMapOf())
    val cartItems = _shoppingCart.asStateFlow()
    
    // Usuario actual (sigue viniendo de LocalDataRepository)
    val currentUser = LocalDataRepository.currentUser
    
    // Métodos del carrito - AHORA IMPLEMENTADOS AQUÍ
    fun addToCart(product: Product, quantity: Int): Result<Unit> {
        if (product.stock < quantity) return Result.failure(Exception("Stock insuficiente."))
        
        // Crear una copia completamente nueva del carrito
        val currentCart = _shoppingCart.value.toMutableMap()
        val existingItem = currentCart[product.id]
        
        if (existingItem != null) {
            // Valida que la nueva cantidad no supere el stock
            if (product.stock < existingItem.quantity + quantity) {
                return Result.failure(Exception("No puedes agregar más de ${product.stock} unidades."))
            }
            // Crear nuevo CartItem en lugar de modificar el existente
            currentCart[product.id] = CartItem(existingItem.product, existingItem.quantity + quantity)
        } else {
            currentCart[product.id] = CartItem(product, quantity)
        }
        
        // Asignar directamente el nuevo valor
        _shoppingCart.value = currentCart
        return Result.success(Unit)
    }
    
    fun updateCartItemQuantity(productId: String, newQuantity: Int): Result<Unit> {
        val currentCart = _shoppingCart.value.toMutableMap()
        
        if (newQuantity <= 0) {
            return removeFromCart(productId)
        }
        
        val cartItem = currentCart[productId] ?: return Result.failure(Exception("Producto no encontrado en el carrito"))
        
        if (newQuantity > cartItem.product.stock) {
            return Result.failure(Exception("Stock insuficiente. Disponible: ${cartItem.product.stock}"))
        }
        
        currentCart[productId] = CartItem(cartItem.product, newQuantity)
        _shoppingCart.value = currentCart
        
        return Result.success(Unit)
    }
    
    fun removeFromCart(productId: String): Result<Unit> {
        val currentCart = _shoppingCart.value.toMutableMap()
        currentCart.remove(productId)
        _shoppingCart.value = currentCart
        return Result.success(Unit)
    }
    
    fun clearCart() {
        _shoppingCart.value = mutableMapOf()
    }
    
    fun getCartTotal(): Double {
        return _shoppingCart.value.values.sumOf { it.product.price * it.quantity }
    }
    
    fun getCartItemCount(): Int {
        return _shoppingCart.value.values.sumOf { it.quantity }
    }
    
    fun getCartItems(): List<CartItem> {
        return _shoppingCart.value.values.toList()
    }
    
    // Método para crear pedido usando SQLite
    fun createOrderFromCart(deliveryAddress: String, callback: (Result<Long>) -> Unit) {
        val user = currentUser.value
        if (user == null) {
            callback(Result.failure(Exception("Usuario no logueado")))
            return
        }
        
        val cartItemsList = _shoppingCart.value.values.toList()
        if (cartItemsList.isEmpty()) {
            callback(Result.failure(Exception("Carrito vacío")))
            return
        }
        
        viewModelScope.launch {
            try {
                // Calcular totales
                val subtotal = cartItemsList.sumOf { it.product.price * it.quantity }
                val deliveryFee = if (subtotal >= 50000) 0.0 else 3000.0
                val total = subtotal + deliveryFee
                
                // Crear pedido
                val newOrder = Pedido(
                    id = 0, // Se auto-genera en la base de datos
                    orderDate = Date(),
                    deliveryDate = null,
                    total = total,
                    deliveryAddress = deliveryAddress,
                    userId = user.id,
                    status = OrderStatus.PENDIENTE
                )
                
                // Insertar pedido en SQLite
                val orderId = insertOrderWithDetails(newOrder, cartItemsList)
                
                if (orderId > 0) {
                    // Limpiar carrito después de crear el pedido
                    clearCart()
                    callback(Result.success(orderId))
                } else {
                    callback(Result.failure(Exception("Error al crear el pedido")))
                }
                
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }
    
    private suspend fun insertOrderWithDetails(order: Pedido, cartItems: List<CartItem>): Long {
        return try {
            // Crear detalles del pedido
            val orderDetails = cartItems.map { cartItem ->
                DetallePedido(
                    pedidoId = 0, // Se asignará automáticamente
                    productId = cartItem.product.id,
                    cantidad = cartItem.quantity,
                    precioUnitario = cartItem.product.price,
                    subtotal = cartItem.product.price * cartItem.quantity
                )
            }
            
            // Crear pedido completo usando DatabaseRepository
            databaseRepository.createOrderWithDetails(order, orderDetails)
        } catch (e: Exception) {
            throw e
        }
    }
}