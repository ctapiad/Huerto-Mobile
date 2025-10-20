package com.example.huerto_hogar.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.huerto_hogar.data.enums.UserRole
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String,
    val registrationDate: Date,
    val address: String?,
    val phone: Long?,
    val comunaId: Long,
    val role: UserRole
)