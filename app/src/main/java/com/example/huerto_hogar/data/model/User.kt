package com.example.huerto_hogar.data.model

import com.example.huerto_hogar.data.enums.UserRole
import java.util.Date

data class User(
    val id: Long,
    var name: String,
    val email: String,
    var password: String,
    val registrationDate: Date,
    var address: String?,
    var phone: Int?,
    var comunaId: Long,
    val role: UserRole
)