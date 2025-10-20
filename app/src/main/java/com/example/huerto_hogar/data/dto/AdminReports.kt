package com.example.huerto_hogar.data.dto

import com.example.huerto_hogar.data.model.Product

data class AdminReports(
    val totalUsers: Int,
    val totalProducts: Int,
    val activeProducts: Int,
    val totalOrders: Int,
    val completedOrders: Int,
    val totalSales: Double,
    val inventoryValue: Double,
    val lowStockProducts: List<Product>,
    val topProducts: List<Product>
)