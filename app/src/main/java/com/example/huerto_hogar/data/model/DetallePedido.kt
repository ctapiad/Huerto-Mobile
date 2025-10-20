package com.example.huerto_hogar.data.model

data class DetallePedido(
    val pedidoId: Long,
    val productId: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)