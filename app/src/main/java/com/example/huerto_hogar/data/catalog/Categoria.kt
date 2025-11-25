package com.example.huerto_hogar.data.catalog

/**
 * Representa una categoría de productos
 */
data class Categoria(
    val id: Long,
    val name: String,
    val description: String? = null
)
