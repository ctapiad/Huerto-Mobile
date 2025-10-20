package com.example.huerto_hogar.data.enums

enum class OrderStatus(val id: Long) {
    PENDIENTE(1), 
    PREPARACION(2), 
    ENVIADO(3), 
    ENTREGADO(4), 
    CANCELADO(5)
}