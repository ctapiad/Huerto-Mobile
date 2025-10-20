package com.example.huerto_hogar.data.dto

import com.example.huerto_hogar.data.model.Product

data class CartItem(
    val product: Product,
    var quantity: Int
)