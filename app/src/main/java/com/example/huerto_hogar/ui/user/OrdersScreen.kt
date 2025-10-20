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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.flowOf
import com.example.huerto_hogar.database.repository.DatabaseRepository
import com.example.huerto_hogar.data.model.Product
import com.example.huerto_hogar.data.model.Pedido
import com.example.huerto_hogar.data.model.DetallePedido
import com.example.huerto_hogar.data.enums.OrderStatus
import com.example.huerto_hogar.viewmodel.AuthViewModel
import com.example.huerto_hogar.util.FormatUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de pedidos con historial
 */
@Composable
fun OrdersScreen(
    onLoginRequired: () -> Unit = {},
    databaseRepository: DatabaseRepository = DatabaseRepository(LocalContext.current),
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    if (currentUser == null) {
        NoUserOrdersCard(onLoginRequired = onLoginRequired)
        return
    }

    // Obtener pedidos del usuario desde la base de datos
    val userOrders by remember(currentUser!!.id) {
        databaseRepository.getOrdersByUser(currentUser!!.id)
    }.collectAsState(initial = emptyList())
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
                        text = userOrders.size.toString(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (userOrders.isEmpty()) {
            EmptyOrdersCard()
        } else {
            // Lista de pedidos
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(userOrders) { order ->
                    OrderCard(order = order, databaseRepository = databaseRepository)
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
fun OrderCard(
    order: Pedido,
    databaseRepository: DatabaseRepository
) {
    var isExpanded by remember { mutableStateOf(false) }
    var orderDetails by remember { mutableStateOf<List<DetallePedido>>(emptyList()) }
    var products by remember { mutableStateOf<Map<String, Product>>(emptyMap()) }
    
    // Cargar detalles del pedido cuando se expande
    LaunchedEffect(order.id, isExpanded) {
        if (isExpanded && orderDetails.isEmpty()) {
            orderDetails = databaseRepository.getOrderDetails(order.id)
            val productMap = mutableMapOf<String, Product>()
            orderDetails.forEach { detail ->
                databaseRepository.getProductById(detail.productId)?.let { product ->
                    productMap[detail.productId] = product
                }
            }
            products = productMap
        }
    }
    
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
                        text = "Pedido #ORD-${order.id}",
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
                            text = "",
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
                
                orderDetails.forEach { detail ->
                    products[detail.productId]?.let { product ->
                        OrderItemRow(detail = detail, product = product)
                    }
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
fun OrderStatusBadge(status: com.example.huerto_hogar.data.enums.OrderStatus) {
    val (color, text, icon) = when (status) {
        com.example.huerto_hogar.data.enums.OrderStatus.PENDIENTE -> Triple(
            Color(0xFFFF9800),
            "Pendiente",
            Icons.Filled.Schedule
        )
        com.example.huerto_hogar.data.enums.OrderStatus.PREPARACION -> Triple(
            Color(0xFFFF9800),
            "Preparando",
            Icons.Filled.Kitchen
        )
        com.example.huerto_hogar.data.enums.OrderStatus.ENVIADO -> Triple(
            Color(0xFF2196F3),
            "En Camino",
            Icons.Filled.LocalShipping
        )
        com.example.huerto_hogar.data.enums.OrderStatus.ENTREGADO -> Triple(
            Color(0xFF4CAF50),
            "Entregado",
            Icons.Filled.CheckCircle
        )
        com.example.huerto_hogar.data.enums.OrderStatus.CANCELADO -> Triple(
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
fun OrderItemRow(detail: DetallePedido, product: Product) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Cantidad: ${detail.cantidad}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        
        Text(
            text = FormatUtils.formatPrice(detail.subtotal),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )
    }
}