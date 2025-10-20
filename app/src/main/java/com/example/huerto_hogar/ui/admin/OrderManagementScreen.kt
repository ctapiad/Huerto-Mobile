package com.example.huerto_hogar.ui.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.huerto_hogar.database.repository.DatabaseRepository
import com.example.huerto_hogar.data.enums.OrderStatus
import com.example.huerto_hogar.data.model.User
import com.example.huerto_hogar.data.model.Product
import com.example.huerto_hogar.data.model.Pedido
import com.example.huerto_hogar.data.model.DetallePedido
import com.example.huerto_hogar.util.FormatUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderManagementScreen(
    databaseRepository: DatabaseRepository = DatabaseRepository(LocalContext.current)
) {
    var selectedFilter by remember { mutableStateOf<OrderStatus?>(null) }
    
    val allOrders by databaseRepository.getAllOrders().collectAsState(initial = emptyList())
    
    val filteredOrders = if (selectedFilter != null) {
        allOrders.filter { it.status == selectedFilter }
    } else {
        allOrders
    }

    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2E7D32)
                ),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Gestión de Pedidos",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Administrar estados de pedidos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        
                        // Estadísticas rápidas
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${allOrders.size}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Total Pedidos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            // Filtros por estado
            OrderStatusFilters(
                selectedFilter = selectedFilter,
                onFilterChanged = { selectedFilter = it },
                orders = allOrders
            )

            // Lista de pedidos
            if (filteredOrders.isEmpty()) {
                EmptyOrdersMessage(selectedFilter)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders) { order ->
                        AdminOrderCard(
                            order = order,
                            databaseRepository = databaseRepository,
                            onStatusChange = { _, _ ->
                                // La lógica de actualización se maneja dentro de AdminOrderCard
                                // Este callback se puede usar para refrescar la UI si es necesario
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusFilters(
    selectedFilter: OrderStatus?,
    onFilterChanged: (OrderStatus?) -> Unit,
    orders: List<Pedido>
) {
    val statusCounts = orders.groupBy { it.status }.mapValues { it.value.size }
    
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Filtro "Todos"
        item {
            FilterChip(
                onClick = { onFilterChanged(null) },
                label = { Text("Todos (${orders.size})") },
                selected = selectedFilter == null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2E7D32),
                    selectedLabelColor = Color.White
                )
            )
        }
        
        // Filtros por estado
        items(OrderStatus.values()) { status ->
            val count = statusCounts[status] ?: 0
            if (count > 0) {
                FilterChip(
                    onClick = { onFilterChanged(status) },
                    label = { Text("${getStatusDisplayName(status)} ($count)") },
                    selected = selectedFilter == status,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = getStatusColor(status),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderCard(
    order: Pedido,
    databaseRepository: DatabaseRepository,
    onStatusChange: (Long, OrderStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale("es", "CL"))
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Obtener información del usuario
    var user by remember { mutableStateOf<User?>(null) }
    
    LaunchedEffect(order.userId) {
        user = databaseRepository.getUserById(order.userId)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header del pedido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pedido #ORD-${order.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "Cliente: ${user?.name ?: "Usuario desconocido"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dateFormat.format(order.orderDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    OrderStatusChip(status = order.status)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = FormatUtils.formatPrice(order.total),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botones de acción y expansión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón cambiar estado
                OutlinedButton(
                    onClick = { showStatusDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2E7D32)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Cambiar estado",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cambiar Estado")
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Detalles expandibles
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Información del cliente
                Text(
                    text = "📧 Contacto del cliente:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = user?.email ?: "Email no disponible",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp)
                )
                if (user?.phone != null) {
                    Text(
                        text = "📱 ${user?.phone}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                // Dirección de entrega
                Text(
                    text = "📍 Dirección de entrega:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = order.deliveryAddress,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Productos del pedido
                Text(
                    text = "🛒 Productos:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                var orderDetails by remember { mutableStateOf<List<DetallePedido>>(emptyList()) }
                var products by remember { mutableStateOf<Map<String, Product>>(emptyMap()) }
                
                LaunchedEffect(order.id) {
                    orderDetails = databaseRepository.getOrderDetails(order.id)
                    val productMap = mutableMapOf<String, Product>()
                    orderDetails.forEach { detail ->
                        databaseRepository.getProductById(detail.productId)?.let { product ->
                            productMap[detail.productId] = product
                        }
                    }
                    products = productMap
                }
                
                orderDetails.forEach { detail ->
                    products[detail.productId]?.let { prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${detail.cantidad}x ${prod.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = FormatUtils.formatPrice(detail.subtotal),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog para cambiar estado
    if (showStatusDialog) {
        StatusChangeDialog(
            currentStatus = order.status,
            onStatusSelected = { newStatus ->
                coroutineScope.launch {
                    val success = databaseRepository.updateOrderStatus(order.id, newStatus)
                    if (success) {
                        Toast.makeText(
                            context, 
                            "Estado actualizado a ${getStatusDisplayName(newStatus)}", 
                            Toast.LENGTH_SHORT
                        ).show()
                        onStatusChange(order.id, newStatus) // Notificar al parent si es necesario
                    } else {
                        Toast.makeText(
                            context, 
                            "Error al actualizar el estado", 
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                showStatusDialog = false
            },
            onDismiss = { showStatusDialog = false }
        )
    }
}

@Composable
fun StatusChangeDialog(
    currentStatus: OrderStatus,
    onStatusSelected: (OrderStatus) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Estado del Pedido") },
        text = {
            Column {
                Text("Estado actual: ${getStatusDisplayName(currentStatus)}")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Selecciona el nuevo estado:")
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OrderStatus.values().forEach { status ->
                    if (status != currentStatus) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { onStatusSelected(status) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = getStatusColor(status)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = getStatusDisplayName(status),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EmptyOrdersMessage(selectedFilter: OrderStatus?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = "Sin pedidos",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (selectedFilter == null) {
                    "No hay pedidos en el sistema"
                } else {
                    "No hay pedidos con estado '${getStatusDisplayName(selectedFilter)}'"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = if (selectedFilter == null) {
                    "Los pedidos aparecerán aquí cuando los clientes realicen compras."
                } else {
                    "Prueba con otro filtro para ver más pedidos."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// Funciones auxiliares
fun getStatusDisplayName(status: OrderStatus): String = when (status) {
    OrderStatus.PENDIENTE -> "Pendiente"
    OrderStatus.PREPARACION -> "En Preparación"
    OrderStatus.ENVIADO -> "Enviado"
    OrderStatus.ENTREGADO -> "Entregado"
    OrderStatus.CANCELADO -> "Cancelado"
}

fun getStatusColor(status: OrderStatus): Color = when (status) {
    OrderStatus.PENDIENTE -> Color(0xFFFFEB3B)
    OrderStatus.PREPARACION -> Color(0xFF2196F3)
    OrderStatus.ENVIADO -> Color(0xFFFF9800)
    OrderStatus.ENTREGADO -> Color(0xFF4CAF50)
    OrderStatus.CANCELADO -> Color(0xFFf44336)
}

@Composable
fun OrderStatusChip(status: OrderStatus) {
    val (backgroundColor, textColor, statusText) = when (status) {
        OrderStatus.PENDIENTE -> Triple(Color(0xFFFFEB3B), Color(0xFF5D4037), "Pendiente")
        OrderStatus.PREPARACION -> Triple(Color(0xFF2196F3), Color.White, "En Preparación")
        OrderStatus.ENVIADO -> Triple(Color(0xFFFF9800), Color.White, "Enviado")
        OrderStatus.ENTREGADO -> Triple(Color(0xFF4CAF50), Color.White, "Entregado")
        OrderStatus.CANCELADO -> Triple(Color(0xFFf44336), Color.White, "Cancelado")
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}