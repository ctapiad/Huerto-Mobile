package com.example.huerto_hogar.network.repository

import android.util.Log
import com.example.huerto_hogar.network.*

/**
 * Repositorio para operaciones de productos con la API REST
 */
class ProductRepository {
    
    private val apiService = ProductApiClient.apiService
    
    /**
     * Health check del servicio
     */
    suspend fun healthCheck(): ApiResult<String> {
        return safeApiCall {
            apiService.healthCheck()
        }
    }
    
    /**
     * Obtener todos los productos
     */
    suspend fun getAllProducts(): ApiResult<List<ProductoDto>> {
        return safeApiCall {
            apiService.getAllProducts()
        }
    }
    
    /**
     * Obtener productos activos
     */
    suspend fun getActiveProducts(): ApiResult<List<ProductoDto>> {
        return safeApiCall {
            apiService.getActiveProducts()
        }
    }
    
    /**
     * Obtener productos disponibles (activos y con stock)
     */
    suspend fun getAvailableProducts(): ApiResult<List<ProductoDto>> {
        return safeApiCall {
            apiService.getAvailableProducts()
        }
    }
    
    /**
     * Obtener productos orgánicos
     */
    suspend fun getOrganicProducts(): ApiResult<List<ProductoDto>> {
        return safeApiCall {
            apiService.getOrganicProducts()
        }
    }
    
    /**
     * Obtener productos con stock bajo
     */
    suspend fun getLowStockProducts(minStock: Int = 10): ApiResult<List<ProductoDto>> {
        return safeApiCall {
            apiService.getLowStockProducts(minStock)
        }
    }
    
    /**
     * Obtener productos por categoría
     */
    suspend fun getProductsByCategory(categoryId: Int): ApiResult<List<ProductoDto>> {
        return safeApiCall {
            apiService.getProductsByCategory(categoryId)
        }
    }
    
    /**
     * Obtener productos por rango de precio
     */
    suspend fun getProductsByPriceRange(minPrice: Int, maxPrice: Int): ApiResult<List<ProductoDto>> {
        return safeApiCall {
            apiService.getProductsByPriceRange(minPrice, maxPrice)
        }
    }
    
    /**
     * Buscar productos por nombre
     */
    suspend fun searchProducts(name: String): ApiResult<List<ProductoDto>> {
        return safeApiCall {
            apiService.searchProductsByName(name)
        }
    }
    
    /**
     * Obtener producto por ID
     */
    suspend fun getProductById(productId: String): ApiResult<ProductoDto> {
        return safeApiCall {
            apiService.getProductById(productId)
        }
    }
    
    /**
     * Crear nuevo producto
     */
    suspend fun createProduct(
        idProducto: String,
        nombre: String,
        linkImagen: String? = null,
        descripcion: String,
        precio: Int,
        stock: Int,
        idCategoria: Int,
        origen: String? = null,
        certificacionOrganica: Boolean = false,
        estaActivo: Boolean = true
    ): ApiResult<ProductoDto> {
        return safeApiCall {
            apiService.createProduct(
                CrearProductoDto(
                    idProducto = idProducto,
                    nombre = nombre,
                    linkImagen = linkImagen,
                    descripcion = descripcion,
                    precio = precio,
                    stock = stock,
                    idCategoria = idCategoria,
                    origen = origen,
                    certificacionOrganica = certificacionOrganica,
                    estaActivo = estaActivo
                )
            )
        }
    }
    
    /**
     * Actualizar producto completo
     */
    suspend fun updateProduct(producto: ProductoDto): ApiResult<ProductoDto> {
        Log.d("ProductRepository", "Actualizando producto completo: ${producto.idProducto}")
        Log.d("ProductRepository", "Datos: nombre=${producto.nombre}, precio=${producto.precio}, stock=${producto.stock}, activo=${producto.estaActivo}")
        
        val dto = ActualizarProductoDto(
            nombre = producto.nombre,
            linkImagen = producto.linkImagen,
            descripcion = producto.descripcion,
            precio = producto.precio,
            stock = producto.stock,
            idCategoria = producto.idCategoria,
            origen = producto.origen,
            certificacionOrganica = producto.certificacionOrganica,
            estaActivo = producto.estaActivo
        )
        
        Log.d("ProductRepository", "DTO a enviar: $dto")
        
        return safeApiCall {
            apiService.updateProduct(producto.idProducto, dto)
        }
    }
    
    /**
     * Actualizar producto existente con campos opcionales
     */
    suspend fun updateProduct(
        productId: String,
        nombre: String? = null,
        linkImagen: String? = null,
        descripcion: String? = null,
        precio: Int? = null,
        stock: Int? = null,
        idCategoria: Int? = null,
        origen: String? = null,
        certificacionOrganica: Boolean? = null,
        estaActivo: Boolean? = null
    ): ApiResult<ProductoDto> {
        return safeApiCall {
            apiService.updateProduct(
                productId,
                ActualizarProductoDto(
                    nombre = nombre,
                    linkImagen = linkImagen,
                    descripcion = descripcion,
                    precio = precio,
                    stock = stock,
                    idCategoria = idCategoria,
                    origen = origen,
                    certificacionOrganica = certificacionOrganica,
                    estaActivo = estaActivo
                )
            )
        }
    }
    
    /**
     * Actualizar solo el stock de un producto
     */
    suspend fun updateStock(productId: String, newStock: Int): ApiResult<Unit> {
        return safeApiCall {
            apiService.updateStock(productId, newStock)
        }
    }
    
    /**
     * Desactivar un producto
     */
    suspend fun deactivateProduct(productId: String): ApiResult<Unit> {
        return safeApiCall {
            apiService.deactivateProduct(productId)
        }
    }
    
    /**
     * Eliminar producto
     */
    suspend fun deleteProduct(productId: String): ApiResult<Unit> {
        return safeApiCall {
            apiService.deleteProduct(productId)
        }
    }
}
