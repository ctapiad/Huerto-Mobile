package com.example.huerto_hogar.ui.store

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar.data.LocalDataRepository
import com.example.huerto_hogar.data.dto.CartItem
import com.example.huerto_hogar.data.model.Product
import com.example.huerto_hogar.data.catalog.Categoria
import com.example.huerto_hogar.database.repository.DatabaseRepository
import kotlinx.coroutines.flow.*

// Estado de la UI para la pantalla de productos
data class ProductUiState(
    val products: List<Product> = emptyList(),
    val categories: List<Categoria> = emptyList(),
    val isLoading: Boolean = false
)

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val databaseRepository = DatabaseRepository(application)
    
    // Combinar productos y categorías de SQLite
    val uiState: StateFlow<ProductUiState> = combine(
        databaseRepository.getAllProducts(),
        databaseRepository.getAllCategories()
    ) { productList, categoryList ->
        ProductUiState(
            products = productList,
            categories = categoryList,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductUiState(isLoading = true)
    )

    // NOTA: Las operaciones del carrito ahora se manejan exclusivamente en CartViewModel
    // Este ViewModel se enfoca solo en la gestión de productos
}
