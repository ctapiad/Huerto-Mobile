package com.example.huerto_hogar.network.repository

import android.util.Log
import com.example.huerto_hogar.network.*

/**
 * Repositorio para operaciones de productos con la API REST
 */
class ProductRepository {
    
    private val apiService = ProductApiClient.apiService
    
    /**
     * Obtener todos los productos
     */
    suspend fun getAllProducts(): ApiResult<List<ProductoDto>> {
        return safeApiCall {
            apiService.getAllProducts()
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
     * Buscar productos por nombre
     */
    suspend fun searchProducts(name: String): ApiResult<List<ProductoDto>> {
        return safeApiCall {
            apiService.searchProductsByName(name)
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
     * Eliminar producto
     */
    suspend fun deleteProduct(productId: String): ApiResult<Unit> {
        return safeApiCall {
            apiService.deleteProduct(productId)
        }
    }

    /**
     * Obtener URL prefirmada para subir imagen a S3
     */
    suspend fun getPresignedUploadUrl(request: UploadUrlRequest): ApiResult<UploadUrlResponse> {
        return safeApiCall {
            apiService.getPresignedUploadUrl(request)
        }
    }
}

