package com.example.huerto_hogar.model

data class ProductoErrores(
    val idProducto: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val precio: String? = null,
    val stock: String? = null,
    val linkImagen: String? = null,
    val origen: String? = null,
    val idCategoria: String? = null
)
