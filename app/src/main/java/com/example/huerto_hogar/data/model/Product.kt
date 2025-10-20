package com.example.huerto_hogar.data.model

import java.util.Date

data class Product(
    val id: String,
    var name: String,
    var imageName: String?,
    var description: String?,
    var price: Double,
    var stock: Int,
    var origin: String?,
    var isOrganic: Boolean,
    var isActive: Boolean,
    val entryDate: Date,
    val categoryId: Long,
    var priceUnit: String = "kg" // Unidad de precio por defecto
)