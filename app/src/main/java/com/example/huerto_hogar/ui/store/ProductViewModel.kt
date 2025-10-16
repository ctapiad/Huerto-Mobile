package com.example.huerto_hogar.ui.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar.data.CartItem
import com.example.huerto_hogar.data.LocalDataRepository
import com.example.huerto_hogar.data.Product
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// 1. Estado de la UI para la pantalla de productos
data class ProductUiState(
    val products: List<Product> = emptyList(),
    val cart: Map<String, CartItem> = emptyMap()
)

class ProductViewModel : ViewModel() {

    // 2. Usamos 'stateIn' para combinar múltiples flujos del repositorio en un solo estado de UI
    val uiState: StateFlow<ProductUiState> =
        combine(
            LocalDataRepository.products,
            LocalDataRepository.shoppingCart
        ) { productList, cart ->
            ProductUiState(products = productList, cart = cart)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Inicia el flujo cuando la UI está visible
            initialValue = ProductUiState()
        )

    // 3. Funciones que delegan la lógica al repositorio
    fun addToCart(product: Product, quantity: Int): Result<Unit> {
        return LocalDataRepository.addToCart(product, quantity)
    }
}
