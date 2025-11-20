package com.example.huerto_hogar.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz de servicios REST para el microservicio de Productos
 * Documentación: http://localhost:8082/v3/api-docs
 */
interface ProductApiService {
    
    /**
     * Health check del servicio
     */
    @GET("api/productos/health")
    suspend fun healthCheck(): Response<String>
    
    /**
     * Obtener todos los productos
     */
    @GET("api/productos")
    suspend fun getAllProducts(): Response<List<ProductoDto>>
    
    /**
     * Obtener productos activos
     */
    @GET("api/productos/activos")
    suspend fun getActiveProducts(): Response<List<ProductoDto>>
    
    /**
     * Obtener productos disponibles (activos y con stock)
     */
    @GET("api/productos/disponibles")
    suspend fun getAvailableProducts(): Response<List<ProductoDto>>
    
    /**
     * Obtener productos orgánicos
     */
    @GET("api/productos/organicos")
    suspend fun getOrganicProducts(): Response<List<ProductoDto>>
    
    /**
     * Obtener productos con stock bajo
     * @param stockMinimo Stock mínimo para considerar como bajo (default: 10)
     */
    @GET("api/productos/stock-bajo")
    suspend fun getLowStockProducts(
        @Query("stockMinimo") minStock: Int = 10
    ): Response<List<ProductoDto>>
    
    /**
     * Obtener productos por categoría
     * @param idCategoria ID de la categoría
     */
    @GET("api/productos/categoria/{idCategoria}")
    suspend fun getProductsByCategory(
        @Path("idCategoria") categoryId: Int
    ): Response<List<ProductoDto>>
    
    /**
     * Obtener productos por rango de precio
     * @param precioMin Precio mínimo
     * @param precioMax Precio máximo
     */
    @GET("api/productos/precio")
    suspend fun getProductsByPriceRange(
        @Query("precioMin") minPrice: Int,
        @Query("precioMax") maxPrice: Int
    ): Response<List<ProductoDto>>
    
    /**
     * Buscar productos por nombre
     * @param nombre Nombre o parte del nombre del producto
     */
    @GET("api/productos/buscar")
    suspend fun searchProductsByName(
        @Query("nombre") name: String
    ): Response<List<ProductoDto>>
    
    /**
     * Obtener producto por ID
     */
    @GET("api/productos/{id}")
    suspend fun getProductById(@Path("id") productId: String): Response<ProductoDto>
    
    /**
     * Crear nuevo producto
     */
    @POST("api/productos")
    suspend fun createProduct(@Body product: CrearProductoDto): Response<ProductoDto>
    
    /**
     * Actualizar producto existente
     */
    @PUT("api/productos/{id}")
    suspend fun updateProduct(
        @Path("id") productId: String,
        @Body product: ActualizarProductoDto
    ): Response<ProductoDto>
    
    /**
     * Actualizar stock de un producto
     */
    @PATCH("api/productos/{id}/stock")
    suspend fun updateStock(
        @Path("id") productId: String,
        @Query("stock") newStock: Int
    ): Response<Unit>
    
    /**
     * Desactivar un producto
     */
    @PATCH("api/productos/{id}/desactivar")
    suspend fun deactivateProduct(@Path("id") productId: String): Response<Unit>
    
    /**
     * Eliminar un producto
     */
    @DELETE("api/productos/{id}")
    suspend fun deleteProduct(@Path("id") productId: String): Response<Unit>
    
    /**
     * Generar URL prefirmada para subir imagen a S3
     */
    @POST("api/productos/upload-url")
    suspend fun getPresignedUploadUrl(@Body request: UploadUrlRequest): Response<UploadUrlResponse>
}

// DTO para solicitar URL de subida
data class UploadUrlRequest(
    val fileName: String,
    val contentType: String
)

// DTO para respuesta de URL de subida
data class UploadUrlResponse(
    val uploadUrl: String,    // URL prefirmada para hacer PUT
    val imageUrl: String,     // URL pública de la imagen
    val key: String,          // Key del objeto en S3
    val expiresIn: String     // Tiempo de expiración en segundos
)

// DTOs según la API real
data class CrearProductoDto(
    val idProducto: String,           // Patrón: ^[A-Z]{2}[0-9]{3}$ (ej: PR001)
    val nombre: String,               // Max 100 caracteres
    val linkImagen: String? = null,   // Max 255 caracteres, URL válida o null
    val descripcion: String,          // Max 500 caracteres
    val precio: Int,                  // Mínimo 1
    val stock: Int,                   // Mínimo 0
    val origen: String? = null,       // Max 100 caracteres
    val certificacionOrganica: Boolean = false,
    val estaActivo: Boolean = true,
    val idCategoria: Int
)

data class ActualizarProductoDto(
    val nombre: String? = null,
    val linkImagen: String? = null,
    val descripcion: String? = null,
    val precio: Int? = null,
    val stock: Int? = null,
    val origen: String? = null,
    val certificacionOrganica: Boolean? = null,
    val estaActivo: Boolean? = null,
    val idCategoria: Int? = null
)

data class ProductoDto(
    val idProducto: String,
    val nombre: String,
    val linkImagen: String?,          // URL válida o null
    val descripcion: String,
    val precio: Int,
    val stock: Int,
    val origen: String?,
    val certificacionOrganica: Boolean,
    val estaActivo: Boolean,
    val fechaIngreso: String?,        // ISO 8601 date-time
    val idCategoria: Int,
    @SerializedName("createdAt")
    val createdAt: String? = null,    // Timestamp de MongoDB
    @SerializedName("updatedAt")
    val updatedAt: String? = null     // Timestamp de MongoDB
)
