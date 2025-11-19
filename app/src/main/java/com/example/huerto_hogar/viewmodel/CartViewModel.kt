package com.example.huerto_hogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar.data.LocalDataRepository
import com.example.huerto_hogar.data.dto.CartItem
import com.example.huerto_hogar.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * CartViewModel - Carrito FICTICIO sin persistencia
 * 
 * Este ViewModel maneja el carrito de compras en memoria únicamente.
 * NO guarda pedidos en ninguna base de datos (ni Room ni MongoDB).
 * Simula la creación de un pedido exitoso sin almacenarlo.
 */
class CartViewModel(application: Application) : AndroidViewModel(application) {
    
    // Estados del carrito - SOLO EN MEMORIA
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
    
    /**
     * Método FICTICIO para crear pedido - NO PERSISTE DATOS
     * 
     * Simula la creación exitosa de un pedido sin guardarlo en ninguna base de datos.
     * Retorna un ID ficticio para mostrar en la UI de confirmación.
     */
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
                // Simular un pequeño delay de procesamiento
                delay(500)
                
                // Calcular totales (solo para mostrar en UI)
                val subtotal = cartItemsList.sumOf { it.product.price * it.quantity }
                val deliveryFee = if (subtotal >= 50000) 0.0 else 3000.0
                val total = subtotal + deliveryFee
                
                // Generar un ID ficticio aleatorio para el "pedido"
                val fictitiousOrderId = (1000..9999).random().toLong()
                
                // Limpiar carrito después de "crear" el pedido
                clearCart()
                
                // Retornar éxito con el ID ficticio
                callback(Result.success(fictitiousOrderId))
                
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }
}