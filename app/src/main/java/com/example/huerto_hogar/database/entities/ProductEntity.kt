package com.example.huerto_hogar.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val imageName: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val origin: String?,
    val isOrganic: Boolean,
    val isActive: Boolean,
    val entryDate: Date,
    val categoryId: Long,
    val priceUnit: String
)