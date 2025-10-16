package com.example.huerto_hogar.data

import java.util.Date

// Tablas de catálogo y enumeraciones
enum class UserRole(val id: Long) {
    ADMIN(1), VENDEDOR(2), CLIENTE(3)
}

enum class OrderStatus(val id: Long) {
    PENDIENTE(1), PREPARACION(2), ENVIADO(3), ENTREGADO(4), CANCELADO(5)
}

data class Region(
    val id: Long,
    val name: String
)

data class Comuna(
    val id: Long,
    val name: String,
    val regionId: Long
)

data class Categoria(
    val id: Long,
    val name: String,
    val description: String?
)

// Tablas principales
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

data class Pedido(
    val id: Long,
    val orderDate: Date,
    var deliveryDate: Date?,
    val total: Double,
    val deliveryAddress: String,
    val userId: Long,
    var status: OrderStatus
)

data class DetallePedido(
    val pedidoId: Long,
    val productId: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)

// --- CLASES PARA LA LÓGICA DE LA APP ---

// Ítem dentro del carrito de compras
data class CartItem(
    val product: Product,
    var quantity: Int
)

// Clase de datos para reportes de administrador
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
