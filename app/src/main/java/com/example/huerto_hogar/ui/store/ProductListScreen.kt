package com.example.huerto_hogar.ui.store

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huerto_hogar.R
import com.example.huerto_hogar.data.LocalDataRepository
import com.example.huerto_hogar.data.model.Product
import com.example.huerto_hogar.util.FormatUtils
import com.example.huerto_hogar.viewmodel.CartViewModel
import com.example.huerto_hogar.viewmodel.ProductsViewModel

/**
 * Pantalla principal de productos con filtros por categoría y FAB del carrito
 */
@Composable
fun ProductListScreen(
    onLoginRequired: () -> Unit,
    onNavigateToCart: () -> Unit = {},
    productsViewModel: ProductsViewModel = viewModel(),
    cartViewModel: CartViewModel
) {
    val productUiState by productsViewModel.uiState.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val currentUser by LocalDataRepository.currentUser.collectAsState()
    val context = LocalContext.current
    
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    
    // Calcular el total de productos en el carrito
    val cartItemCount = cartItems.values.sumOf { it.quantity }
    
    // Filtrar productos según la categoría seleccionada
    val filteredProducts = if (selectedCategoryId == null) {
        productUiState.products.filter { it.isActive }
    } else {
        productUiState.products.filter { it.isActive && it.categoryId == selectedCategoryId }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Título principal
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🌱 Nuestros Productos",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Productos frescos y orgánicos para tu hogar",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Sección de categorías
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    item {
                        FilterChip(
                            onClick = { selectedCategoryId = null },
                            label = { Text("Todos") },
                            selected = selectedCategoryId == null
                        )
                    }
                    items(productUiState.categories) { category ->
                        FilterChip(
                            onClick = { selectedCategoryId = category.id },
                            label = { Text(category.name) },
                            selected = selectedCategoryId == category.id
                        )
                    }
                }
            }

            // Contador de productos
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = if (selectedCategoryId == null) "Todos los productos" else "Productos filtrados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2E7D32).copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = "${filteredProducts.size} productos",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            // Lista de productos
            items(filteredProducts) { product ->
                SimpleProductCard(
                    product = product,
                    isLoggedIn = currentUser != null,
                    onAddToCart = { productToAdd, quantity ->
                        // Usar CartViewModel para actualizaciones en tiempo real
                        val result = cartViewModel.addToCart(productToAdd, quantity)
                        result.onSuccess {
                            Toast.makeText(context, "${quantity} ${productToAdd.priceUnit}${if (quantity != 1) "s" else ""} de ${productToAdd.name} añadido al carrito", Toast.LENGTH_SHORT).show()
                        }
                        result.onFailure { error ->
                            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                        }
                    },
                    onLoginRequired = onLoginRequired
                )
            }

            // Mensaje si no hay productos
            if (filteredProducts.isEmpty()) {
                item {
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
                                Icons.Filled.SearchOff,
                                contentDescription = "No hay productos",
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay productos en esta categoría",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        
        // Botón flotante del carrito (siempre visible)
        FloatingActionButton(
            onClick = onNavigateToCart,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF4CAF50)
        ) {
            Box {
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = "Ver carrito",
                    tint = Color.White
                )
                
                // Badge con número solo si hay productos
                if (cartItemCount > 0) {
                    Badge(
                        containerColor = Color.Red,
                        contentColor = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleProductCard(
    product: Product,
    isLoggedIn: Boolean,
    onAddToCart: (Product, Int) -> Unit,
    onLoginRequired: () -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    
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
            // Imagen del producto
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Imagen (desde URL o recurso local)
                Card(
                    modifier = Modifier.size(80.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    AsyncImage(
                        model = product.imageName?.takeIf { it.startsWith("http") } ?: R.drawable.huertohogarfondo,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(R.drawable.huertohogarfondo),
                        placeholder = painterResource(R.drawable.huertohogarfondo)
                    )
                }
                
                // Información del producto
                Column(modifier = Modifier.weight(1f)) {
                    // Nombre y precio
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = FormatUtils.formatPrice(product.price) + "/${product.priceUnit}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    
                    // Stock disponible
                    Text(
                        text = "Stock: ${product.stock} ${product.priceUnit}${if (product.stock != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (product.stock > 5) Color.Gray else Color.Red
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Descripción
            Text(
                text = product.description ?: "Producto fresco de calidad",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Selector de cantidad (siempre disponible)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { if (quantity > 1) quantity-- }
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = "Disminuir")
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = "$quantity ${product.priceUnit}${if (quantity != 1) "s" else ""}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(
                    onClick = { 
                        if (quantity < product.stock) quantity++
                    },
                    enabled = quantity < product.stock
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Aumentar cantidad",
                        tint = if (quantity < product.stock) Color(0xFF2196F3) else Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Botón de añadir al carrito (siempre disponible)
            Button(
                onClick = { 
                    onAddToCart(product, quantity)
                },
                enabled = product.stock > 0,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (product.stock <= 0) "Agotado" else "Añadir ${quantity} al carrito"
                )
            }
        }
    }
}