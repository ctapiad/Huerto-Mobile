package com.example.huerto_hogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar.data.catalog.Categoria
import com.example.huerto_hogar.data.model.Product
import com.example.huerto_hogar.network.ApiResult
import com.example.huerto_hogar.network.ProductoDto
import com.example.huerto_hogar.network.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

// Estado de la UI para la pantalla de productos
data class ProductUiState(
    val products: List<Product> = emptyList(),
    val categories: List<Categoria> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val productRepository = ProductRepository()

    // Estados privados
    private val _uiState = MutableStateFlow(ProductUiState(isLoading = true))
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    // Categorías hardcodeadas (no hay API de categorías)
    private val hardcodedCategories = listOf(
        Categoria(id = 1, name = "Frutas", description = "Frutas frescas y de temporada"),
        Categoria(id = 2, name = "Verduras", description = "Verduras frescas y orgánicas"),
        Categoria(id = 3, name = "Carnes", description = "Carnes y pollo de calidad"),
        Categoria(id = 4, name = "Lácteos", description = "Productos lácteos frescos"),
        Categoria(id = 5, name = "Granos", description = "Granos y legumbres")
    )

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                when (val result = productRepository.getAllProducts()) {
                    is ApiResult.Success -> {
                        val products = result.data.map { dto -> dto.toProduct() }
                        _uiState.update {
                            it.copy(
                                products = products,
                                categories = hardcodedCategories,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                products = emptyList(),
                                categories = hardcodedCategories,
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                    is ApiResult.Loading -> {
                        // Ya está en estado loading desde el inicio
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        products = emptyList(),
                        categories = hardcodedCategories,
                        isLoading = false,
                        error = "Error al cargar productos: ${e.message}"
                    )
                }
            }
        }
    }

    fun refreshProducts() {
        loadProducts()
    }

    // Conversión de ProductoDto a Product
    private fun ProductoDto.toProduct(): Product {
        return Product(
            id = this.idProducto,
            name = this.nombre,
            imageName = this.linkImagen,
            description = this.descripcion,
            price = this.precio.toDouble(),
            stock = this.stock,
            origin = this.origen,
            isOrganic = this.certificacionOrganica,
            isActive = this.estaActivo,
            entryDate = Date(),
            categoryId = this.idCategoria.toLong(),
            priceUnit = "kg"
        )
    }

}