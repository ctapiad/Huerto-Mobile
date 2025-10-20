package com.example.huerto_hogar.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.huerto_hogar.data.enums.OrderStatus
import java.util.Date

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderDate: Date,
    val deliveryDate: Date?,
    val total: Double,
    val deliveryAddress: String,
    val userId: Long,
    val status: OrderStatus
)