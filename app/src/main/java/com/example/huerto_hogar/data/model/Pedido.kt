package com.example.huerto_hogar.data.model

import com.example.huerto_hogar.data.enums.OrderStatus
import java.util.Date

data class Pedido(
    val id: Long,
    val orderDate: Date,
    var deliveryDate: Date?,
    val total: Double,
    val deliveryAddress: String,
    val userId: Long,
    var status: OrderStatus
)