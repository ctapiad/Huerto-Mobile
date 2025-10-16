package com.example.huerto_hogar.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.huerto_hogar.data.*
import com.example.huerto_hogar.util.FormatUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Estados de pedido
 */
enum class OrderStatus {
    PREPARANDO,
    EN_CAMINO,
    ENTREGADO,
    CANCELADO
}

/**
 * Clase para representar un pedido
 */
data class Order(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val total: Double,
    val status: OrderStatus,
    val orderDate: Date,
    val deliveryAddress: String,
    val estimatedDelivery: String
)

/**
 * Pantalla de pedidos con historial
 */
@Composable
fun OrdersScreen(
    onLoginRequired: () -> Unit = {}
) {
    val currentUser by LocalDataRepository.currentUser.collectAsState()

    if (currentUser == null) {
        NoUserOrdersCard(onLoginRequired = onLoginRequired)
        return
    }

    // Crear productos de ejemplo para los pedidos
    val exampleProducts = remember {
        listOf(
            Product(id = "P1", name = "Tomate Cherry", imageName = "tomate_cherry", 
                   description = "Tomates frescos", price = 2200.0, stock = 10, origin = "Local",
                   isOrganic = true, isActive = true, entryDate = Date(), categoryId = 2, priceUnit = "kg"),
            Product(id = "P2", name = "Lechuga Hidropónica", imageName = "lechuga", 
                   description = "Lechuga fresca", price = 1800.0, stock = 15, origin = "Local",
                   isOrganic = true, isActive = true, entryDate = Date(), categoryId = 2, priceUnit = "kg"),
            Product(id = "P3", name = "Orégano Fresco", imageName = "oregano", 
                   description = "Orégano aromático", price = 1500.0, stock = 20, origin = "Local",
                   isOrganic = true, isActive = true, entryDate = Date(), categoryId = 2, priceUnit = "kg"),
            Product(id = "P4", name = "Albahaca Orgánica", imageName = "albahaca", 
                   description = "Albahaca orgánica", price = 1700.0, stock = 12, origin = "Local",
                   isOrganic = true, isActive = true, entryDate = Date(), categoryId = 2, priceUnit = "kg"),
            Product(id = "P5", name = "Pepino Orgánico", imageName = "pepino", 
                   description = "Pepino fresco", price = 1900.0, stock = 8, origin = "Local",
                   isOrganic = true, isActive = true, entryDate = Date(), categoryId = 2, priceUnit = "kg")
        )
    }

    // Pedidos de ejemplo para demostración
    val orders = remember {
        listOf(
            Order(
                id = "ORD001",
                userId = currentUser!!.id.toString(),
                items = listOf(
                    CartItem(exampleProducts[0], 2),
                    CartItem(exampleProducts[1], 1)
                ),
                total = 6200.0,
                status = OrderStatus.EN_CAMINO,
                orderDate = Calendar.getInstance().apply { 
                    add(Calendar.DAY_OF_YEAR, -1) 
                }.time,
                deliveryAddress = "Av. Las Condes 123, Las Condes",
                estimatedDelivery = "Hoy 15:00 - 18:00"
            ),
            Order(
                id = "ORD002",
                userId = currentUser!!.id.toString(),
                items = listOf(
                    CartItem(exampleProducts[2], 1),
                    CartItem(exampleProducts[3], 2)
                ),
                total = 4900.0,
                status = OrderStatus.ENTREGADO,
                orderDate = Calendar.getInstance().apply { 
                    add(Calendar.DAY_OF_YEAR, -3) 
                }.time,
                deliveryAddress = "Av. Las Condes 123, Las Condes",
                estimatedDelivery = "Entregado el 15/01/2024"
            ),
            Order(
                id = "ORD003",
                userId = currentUser!!.id.toString(),
                items = listOf(
                    CartItem(exampleProducts[4], 3)
                ),
                total = 5700.0,
                status = OrderStatus.PREPARANDO,
                orderDate = Date(),
                deliveryAddress = "Av. Las Condes 123, Las Condes",
                estimatedDelivery = "Mañana 10:00 - 13:00"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título de pedidos
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4CAF50).copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.ShoppingBag,
                    contentDescription = "Mis Pedidos",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📦 Mis Pedidos",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Historial y estado de tus pedidos",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                Badge(
                    containerColor = Color.White,
                    contentColor = Color(0xFF4CAF50)
                ) {
                    Text(
                        text = orders.size.toString(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (orders.isEmpty()) {
            EmptyOrdersCard()
        } else {
            // Lista de pedidos
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderCard(order = order)
                }
            }
        }
    }
}

@Composable
fun NoUserOrdersCard(onLoginRequired: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.ShoppingBag,
                contentDescription = "Sin pedidos",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Inicia sesión para ver tus pedidos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Necesitas iniciar sesión para acceder a tu historial de pedidos.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onLoginRequired,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Icon(Icons.Filled.Login, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Sesión")
            }
        }
    }
}

@Composable
fun EmptyOrdersCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.Receipt,
                contentDescription = "Sin pedidos",
                modifier = Modifier.size(48.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Aún no tienes pedidos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¡Explora nuestros productos y haz tu primer pedido!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Encabezado del pedido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pedido #${order.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(order.orderDate),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                
                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Estado y entrega
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = order.deliveryAddress,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = order.estimatedDelivery,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Text(
                    text = FormatUtils.formatPrice(order.total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón expandir/contraer
            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isExpanded) "Ocultar detalles" else "Ver detalles")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }

            // Detalles expandibles
            if (isExpanded) {
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Productos ordenados:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                order.items.forEach { item ->
                    OrderItemRow(item = item)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                
                // Total del pedido
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = FormatUtils.formatPrice(order.total),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val (color, text, icon) = when (status) {
        OrderStatus.PREPARANDO -> Triple(
            Color(0xFFFF9800),
            "Preparando",
            Icons.Filled.Kitchen
        )
        OrderStatus.EN_CAMINO -> Triple(
            Color(0xFF2196F3),
            "En Camino",
            Icons.Filled.LocalShipping
        )
        OrderStatus.ENTREGADO -> Triple(
            Color(0xFF4CAF50),
            "Entregado",
            Icons.Filled.CheckCircle
        )
        OrderStatus.CANCELADO -> Triple(
            Color(0xFFF44336),
            "Cancelado",
            Icons.Filled.Cancel
        )
    }

    Badge(
        containerColor = color,
        contentColor = Color.White
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OrderItemRow(item: CartItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Cantidad: ${item.quantity}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        
        Text(
            text = FormatUtils.formatPrice(item.product.price * item.quantity),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )
    }
}