package com.example.huerto_hogar.ui.admin

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.huerto_hogar.database.repository.DatabaseRepository
import com.example.huerto_hogar.data.model.User
import com.example.huerto_hogar.data.model.Product
import com.example.huerto_hogar.data.model.Pedido
import com.example.huerto_hogar.data.enums.UserRole
import com.example.huerto_hogar.data.enums.OrderStatus
import com.example.huerto_hogar.util.FormatUtils
import kotlin.math.*

/**
 * Pantalla de reportes y estadísticas para administradores
 */
@Composable
fun ReportsScreen(
    databaseRepository: DatabaseRepository = DatabaseRepository(LocalContext.current)
) {
    val users by databaseRepository.getAllUsers().collectAsState(initial = emptyList())
    val products by databaseRepository.getAllProducts().collectAsState(initial = emptyList())
    val pedidos by databaseRepository.getAllOrders().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Resumen general
        item {
            GeneralSummaryCard(users, products, pedidos)
        }

        // Estadísticas de usuarios
        item {
            UserStatsCard(users)
        }

        // Estadísticas de productos
        item {
            ProductStatsCard(products)
        }

        // Productos con stock bajo
        item {
            LowStockAlert(products)
        }

        // Productos más vendidos (simulado)
        item {
            TopProductsCard(products)
        }

        // Ventas recientes (solo pedidos entregados)
        item {
            RecentSalesCard(users, pedidos.filter { it.status == OrderStatus.ENTREGADO })
        }
    }
}

@Composable
fun GeneralSummaryCard(users: List<User>, products: List<Product>, pedidos: List<Pedido>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E7D32).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📊 Resumen General",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    icon = Icons.Filled.People,
                    title = "Usuarios",
                    value = users.size.toString(),
                    subtitle = "${users.count { it.role == UserRole.CLIENTE }} clientes"
                )
                
                SummaryItem(
                    icon = Icons.Filled.Inventory,
                    title = "Productos",
                    value = products.size.toString(),
                    subtitle = "${products.count { it.isActive }} activos"
                )
                
                SummaryItem(
                    icon = Icons.Filled.ShoppingCart,
                    title = "Pedidos",
                    value = pedidos.size.toString(),
                    subtitle = "${pedidos.count { it.status == OrderStatus.ENTREGADO }} entregados"
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    subtitle: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.9f)
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun UserStatsCard(users: List<User>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "👥 Estadísticas de Usuarios",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val roleStats = users.groupBy { it.role }
            
            roleStats.forEach { (role, userList) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Badge(
                            containerColor = when (role) {
                                UserRole.ADMIN -> Color(0xFFE91E63)
                                UserRole.VENDEDOR -> Color(0xFF2196F3)
                                UserRole.CLIENTE -> Color(0xFF4CAF50)
                            }
                        ) {
                            Text(
                                text = role.name,
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    
                    Text(
                        text = "${userList.size} usuario${if (userList.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ProductStatsCard(products: List<Product>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🛍️ Estadísticas de Productos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProductStatItem("Activos", products.count { it.isActive }, Color(0xFF4CAF50))
                ProductStatItem("Inactivos", products.count { !it.isActive }, Color(0xFF757575))
                ProductStatItem("Orgánicos", products.count { it.isOrganic }, Color(0xFF8BC34A))
                ProductStatItem("Stock bajo", products.count { it.stock <= 10 }, Color(0xFFFF9800))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Valor total de inventario
            val totalInventoryValue = products.sumOf { it.price * it.stock }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💰 Valor Total del Inventario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${String.format("%.0f", totalInventoryValue)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductStatItem(label: String, value: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LowStockAlert(products: List<Product>) {
    val lowStockProducts = products.filter { it.stock <= 10 && it.isActive }
    
    if (lowStockProducts.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = "Advertencia",
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⚠️ Productos con Stock Bajo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                lowStockProducts.forEach { product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Badge(
                            containerColor = when {
                                product.stock <= 0 -> Color(0xFFE91E63)
                                product.stock <= 5 -> Color(0xFFFF9800)
                                else -> Color(0xFF4CAF50)
                            }
                        ) {
                            Text(
                                text = "Stock: ${product.stock}",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopProductsCard(products: List<Product>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🏆 Productos Destacados",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Simulamos productos "más vendidos" basado en stock restante (menos stock = más vendido)
            val topProducts = products
                .filter { it.isActive }
                .sortedBy { it.stock }
                .take(5)
            
            topProducts.forEachIndexed { index, product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Badge(
                            containerColor = when (index) {
                                0 -> Color(0xFFFFD700) // Oro
                                1 -> Color(0xFFC0C0C0) // Plata
                                2 -> Color(0xFFCD7F32) // Bronce
                                else -> Color(0xFF2196F3)
                            }
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = if (index < 3) Color.Black else Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                    
                    Text(
                        text = FormatUtils.formatPriceWithUnit(product.price, product.priceUnit),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        maxLines = 1,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
fun RecentSalesCard(users: List<User>, pedidos: List<Pedido>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "💳 Resumen de Ventas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (pedidos.isEmpty()) {
                Text(
                    text = "No hay pedidos entregados aún",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Los pedidos completados aparecerán aquí",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                // Estadísticas de pedidos entregados
                val totalVentas = pedidos.sumOf { it.total }
                val promedioVenta = totalVentas / pedidos.size
                val pedidosHoy = pedidos.count { 
                    val today = java.util.Calendar.getInstance()
                    val orderDate = java.util.Calendar.getInstance()
                    orderDate.time = it.orderDate
                    today.get(java.util.Calendar.YEAR) == orderDate.get(java.util.Calendar.YEAR) &&
                    today.get(java.util.Calendar.DAY_OF_YEAR) == orderDate.get(java.util.Calendar.DAY_OF_YEAR)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SalesStatItem("Total Ventas", FormatUtils.formatPrice(totalVentas), Color(0xFF4CAF50))
                    SalesStatItem("Promedio", FormatUtils.formatPrice(promedioVenta), Color(0xFF2196F3))
                    SalesStatItem("Entregados", pedidos.size.toString(), Color(0xFF9C27B0))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Lista de pedidos recientes (máximo 3)
                Text(
                    text = "🕐 Pedidos Recientes:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                pedidos.sortedByDescending { it.deliveryDate ?: it.orderDate }.take(3).forEach { pedido ->
                    // Buscar usuario correspondiente desde la lista de usuarios ya cargada
                    val user = users.find { it.id == pedido.userId }
                    val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("es", "CL"))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "#ORD-${pedido.id} - ${user?.name ?: "Cliente"}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Entregado: ${dateFormat.format(pedido.deliveryDate ?: pedido.orderDate)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Text(
                            text = FormatUtils.formatPrice(pedido.total),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SalesStatItem(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}