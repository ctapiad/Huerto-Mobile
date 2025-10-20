package com.example.huerto_hogar.ui.store

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huerto_hogar.R
import com.example.huerto_hogar.data.LocalDataRepository
import com.example.huerto_hogar.data.model.Product
import com.example.huerto_hogar.data.dto.CartItem
import com.example.huerto_hogar.data.enums.OrderStatus
import com.example.huerto_hogar.data.model.DetallePedido
import com.example.huerto_hogar.util.FormatUtils
import com.example.huerto_hogar.viewmodel.CartViewModel

/**
 * Pantalla del carrito de compras
 */
@Composable
fun CartScreen(
    onCheckout: () -> Unit = {},
    onLoginRequired: () -> Unit = {},
    onPaymentSuccess: (String, String, Long) -> Unit = { _, _, _ -> }
) {
    val cartViewModel: CartViewModel = viewModel()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val currentUser by cartViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    // Verificar si el usuario está logueado
    if (currentUser == null) {
        NoUserLoggedCard(onLoginRequired = onLoginRequired)
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título del carrito
        item {
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
                        Icons.Filled.ShoppingCart,
                        contentDescription = "Carrito",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🛒 Mi Carrito",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${cartItems.size} producto${if (cartItems.size != 1) "s" else ""} en tu carrito",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        if (cartItems.isEmpty()) {
            // Carrito vacío
            item {
                EmptyCartCard()
            }
        } else {
            // Items del carrito
            items(cartItems.values.toList()) { cartItem ->
                CartItemCard(
                    cartItem = cartItem,
                    onUpdateQuantity = { newQuantity ->
                        if (newQuantity <= 0) {
                            val result = cartViewModel.removeFromCart(cartItem.product.id)
                            if (result.isSuccess) {
                                Toast.makeText(context, "${cartItem.product.name} eliminado del carrito", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val result = cartViewModel.updateCartItemQuantity(cartItem.product.id, newQuantity)
                            if (result.isFailure) {
                                Toast.makeText(context, result.exceptionOrNull()?.message ?: "Error al actualizar", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onRemove = {
                        val result = cartViewModel.removeFromCart(cartItem.product.id)
                        if (result.isSuccess) {
                            Toast.makeText(context, "${cartItem.product.name} eliminado del carrito", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Resumen del pedido
            item {
                OrderSummaryCard(
                    cartItems = cartItems.values.toList(),
                    isUserLoggedIn = currentUser != null,
                    onCheckout = {
                        currentUser?.let { user ->
                            // Usuario logueado - procesar pago
                            val deliveryAddress = if (user.address.isNullOrEmpty()) {
                                "Dirección por defecto - Santiago, Chile"
                            } else {
                                user.address!!
                            }
                            
                            // Crear pedido real en SQLite
                            cartViewModel.createOrderFromCart(deliveryAddress) { result ->
                                result.fold(
                                    onSuccess = { orderId ->
                                        // Calcular total para mostrar
                                        val subtotal = cartItems.values.sumOf { cartItem ->
                                            cartItem.product.price * cartItem.quantity
                                        }
                                        val deliveryFee = if (subtotal >= 50000) 0.0 else 3000.0
                                        val total = subtotal + deliveryFee
                                        val totalFormatted = FormatUtils.formatPrice(total)
                                        
                                        // Mostrar pantalla de confirmación de pago
                                        onPaymentSuccess("ORD-$orderId", totalFormatted, orderId)
                                    },
                                    onFailure = { exception ->
                                        Toast.makeText(
                                            context,
                                            "Error al procesar el pedido: ${exception.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                )
                            }
                        } ?: run {
                            // Usuario no logueado - redirigir al login
                            onLoginRequired()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NoUserLoggedCard(onLoginRequired: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.Login,
                contentDescription = "Login requerido",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF2196F3)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Inicia sesión para ver tu carrito",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Necesitas iniciar sesión para añadir productos al carrito y hacer pedidos.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onLoginRequired,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
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
fun EmptyCartCard() {
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
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.RemoveShoppingCart,
                contentDescription = "Carrito vacío",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tu carrito está vacío",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Explora nuestros productos y añade algunos a tu carrito para empezar a comprar.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF2E7D32),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Imagen del producto
            val imageResId = context.resources.getIdentifier(
                cartItem.product.imageName,
                "drawable",
                context.packageName
            )

            Image(
                painter = painterResource(id = if (imageResId != 0) imageResId else R.drawable.huertohogarfondo),
                contentDescription = cartItem.product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información del producto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = FormatUtils.formatPriceWithUnit(cartItem.product.price, cartItem.product.priceUnit),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Controles de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onUpdateQuantity(cartItem.quantity - 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Remove,
                            contentDescription = "Disminuir",
                            tint = Color(0xFF2196F3)
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = "${cartItem.quantity}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { onUpdateQuantity(cartItem.quantity + 1) },
                        enabled = cartItem.quantity < cartItem.product.stock,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Aumentar",
                            tint = if (cartItem.quantity < cartItem.product.stock) Color(0xFF2196F3) else Color.Gray
                        )
                    }

                    // Subtotal
                    Text(
                        text = FormatUtils.formatPrice(cartItem.product.price * cartItem.quantity),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )

                    // Botón eliminar
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Eliminar",
                            tint = Color(0xFFE91E63)
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar producto") },
            text = { Text("¿Estás seguro de que deseas eliminar ${cartItem.product.name} del carrito?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemove()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFE91E63))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun OrderSummaryCard(
    cartItems: List<CartItem>,
    isUserLoggedIn: Boolean,
    onCheckout: () -> Unit
) {
    val totalQuantity = cartItems.sumOf { it.quantity }
    val subtotal = cartItems.sumOf { it.product.price * it.quantity }
    val deliveryFee = if (subtotal >= 50000) 0.0 else 3000.0 // Envío gratis sobre $50.000
    val total = subtotal + deliveryFee

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
                text = "📋 Resumen del Pedido",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Detalles del resumen
            SummaryRow("Productos:", "${totalQuantity} artículo${if (totalQuantity != 1) "s" else ""}")
            SummaryRow("Subtotal:", FormatUtils.formatPrice(subtotal))
            SummaryRow(
                "Envío:", 
                if (deliveryFee > 0) FormatUtils.formatPrice(deliveryFee) else "¡Gratis!"
            )
            
            if (deliveryFee == 0.0) {
                Text(
                    text = "🎉 Envío gratis por compras sobre ${FormatUtils.formatPrice(50000.0)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.White.copy(alpha = 0.3f)
            )

            SummaryRow(
                "Total:", 
                FormatUtils.formatPrice(total),
                isTotal = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de checkout
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isUserLoggedIn) Color(0xFF4CAF50) else Color(0xFF2196F3)
                )
            ) {
                Icon(
                    imageVector = if (isUserLoggedIn) Icons.Filled.Payment else Icons.Filled.Login,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUserLoggedIn) "Proceder al Pago" else "Inicia sesión para proceder al pago",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = Color.White
        )
        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}