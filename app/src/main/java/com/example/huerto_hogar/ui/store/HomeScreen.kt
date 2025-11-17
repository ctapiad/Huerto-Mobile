package com.example.huerto_hogar.ui.store

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.huerto_hogar.R
import com.example.huerto_hogar.data.model.Product
import com.example.huerto_hogar.util.FormatUtils
import com.example.huerto_hogar.viewmodel.CartViewModel

/**
 * La pantalla de inicio renovada con carrusel de productos destacados,
 * información de la aplicación y footer con contacto.
 */
@Composable
fun HomeScreen(
    onNavigateToCart: () -> Unit = {},
    onNavigateToApiTest: () -> Unit = {}, // 🧪 Nueva navegación
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel
) {
    val productUiState by productViewModel.uiState.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    
    // Calcular el total de productos en el carrito
    val cartItemCount = cartItems.values.sumOf { it.quantity }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Sección de bienvenida e información de la app
            item {
                WelcomeSection()
            }
            
            // 🧪 Botón de prueba de APIs (temporal para desarrollo)
            item {
                ApiTestButton(onClick = onNavigateToApiTest)
            }
            
            // Carrusel de productos destacados
            item {
                FeaturedProductsCarousel(
                    products = productUiState.products,
                    onAddToCart = { product, quantity ->
                        cartViewModel.addToCart(product, quantity)
                    }
                )
            }
            
            // Información adicional sobre la aplicación
            item {
                AppInfoSection()
            }
            
            // Footer con contacto y redes sociales
            item {
                FooterSection()
            }
        }

        // FAB con contador de carrito
        FloatingActionButton(
            onClick = { onNavigateToCart() },
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
fun WelcomeSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌿 ¡Bienvenido a Huerto Hogar! 🌿",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Productos orgánicos frescos directo desde el campo hasta tu mesa. Cultivamos con amor y respeto por la naturaleza para ofrecerte lo mejor en cada cosecha.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color(0xFF424242),
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun FeaturedProductsCarousel(
    products: List<Product>,
    onAddToCart: (Product, Int) -> Result<Unit>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Filtrar productos destacados (primeros 4 productos como ejemplo)
    val featuredProducts = products.take(4)
    
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // Título con fondo blanco centrado
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "🌟 Productos Destacados 🌟",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        
        // Auto-scroll carousel con bucle infinito
        val lazyListState = rememberLazyListState()
        var currentIndex by remember { mutableIntStateOf(0) }
        
        LaunchedEffect(featuredProducts) {
            if (featuredProducts.isNotEmpty()) {
                while (true) {
                    delay(3000) // 3 segundos entre cambios
                    currentIndex = (currentIndex + 1) % featuredProducts.size
                    lazyListState.animateScrollToItem(currentIndex)
                }
            }
        }
        
        LazyRow(
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(featuredProducts.size) { index ->
                val product = featuredProducts[index]
                FeaturedProductCard(
                    product = product,
                    onAddToCart = { quantity ->
                        val result = onAddToCart(product, quantity)
                        result.onSuccess {
                            Toast.makeText(context, "${product.name} (x$quantity) añadido al carrito", Toast.LENGTH_SHORT).show()
                        }
                        result.onFailure { error ->
                            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Indicadores de posición con bucle infinito
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(featuredProducts.size) { index ->
                val isSelected = currentIndex == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 12.dp else 8.dp)
                        .padding(2.dp)
                        .background(
                            color = if (isSelected) Color(0xFF4CAF50) else Color.Gray.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
fun FeaturedProductCard(
    product: Product,
    onAddToCart: (quantity: Int) -> Unit
) {
    val context = LocalContext.current
    var quantity by remember { mutableIntStateOf(1) }
    
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(320.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen del producto
            val imageResId = context.resources.getIdentifier(
                product.imageName, 
                "drawable", 
                context.packageName
            )
            
            Image(
                painter = painterResource(id = if (imageResId != 0) imageResId else R.drawable.huertohogarfondo),
                contentDescription = product.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Nombre del producto
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Precio con unidad
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = FormatUtils.formatPriceWithUnit(product.price, product.priceUnit),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6F00),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Selectores de cantidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón disminuir
                IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Disminuir cantidad",
                        tint = Color(0xFF2E7D32)
                    )
                }
                
                // Cantidad actual
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                
                // Botón aumentar
                IconButton(
                    onClick = { quantity++ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Aumentar cantidad",
                        tint = Color(0xFF2E7D32)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botón agregar al carrito
            Button(
                onClick = { onAddToCart(quantity) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Agregar", fontSize = 13.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun AppInfoSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "¿Por qué elegir Huerto Hogar?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            InfoItem("🌱", "Productos 100% orgánicos")
            InfoItem("🚛", "Entrega fresca a domicilio")
            InfoItem("👩‍🌾", "Apoyamos a agricultores locales")
            InfoItem("♻️", "Compromiso con el medio ambiente")
        }
    }
}

@Composable
fun InfoItem(icon: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 20.sp,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun FooterSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📞 Contacto",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ContactItem(Icons.Default.Phone, "+56 9 1234 5678")
                Spacer(modifier = Modifier.height(4.dp))
                ContactItem(Icons.Default.Email, "contacto@huertohogar.cl")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "🌐 Síguenos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    SocialMediaItem("�", "Instagram")
                    SocialMediaItem("📱", "WhatsApp")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "© 2025 Huerto Hogar - Productos orgánicos con amor",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ContactItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

@Composable
fun SocialMediaItem(emoji: String, name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontSize = 10.sp
        )
    }
}

/**
 * 🧪 Botón para acceder a la pantalla de pruebas de API
 * (Temporal para desarrollo)
 */
@Composable
fun ApiTestButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2196F3).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "🧪 Prueba de APIs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Verifica la conexión con microservicios",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ir a pruebas",
                tint = Color.White
            )
        }
    }
}