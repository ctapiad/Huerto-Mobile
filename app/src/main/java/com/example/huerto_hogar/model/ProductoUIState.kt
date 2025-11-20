package com.example.huerto_hogar.model

data class ProductoUIState(
    val idProducto: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Int = 0,
    val stock: Int = 0,
    val linkImagen: String = "",
    val origen: String = "",
    val certificacionOrganica: Boolean = false,
    val estaActivo: Boolean = true,
    val idCategoria: Int = 1,  // Valor por defecto válido
    val errores: ProductoErrores = ProductoErrores()
)
