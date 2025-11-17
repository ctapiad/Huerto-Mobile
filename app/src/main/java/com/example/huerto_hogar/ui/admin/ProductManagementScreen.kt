package com.example.huerto_hogar.ui.admin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.huerto_hogar.R
import com.example.huerto_hogar.database.repository.DatabaseRepository
import com.example.huerto_hogar.data.model.Product
import com.example.huerto_hogar.network.repository.ProductRepository
import com.example.huerto_hogar.network.ApiResult
import com.example.huerto_hogar.network.ProductoDto
import java.util.Date

/**
 * Pantalla de gestión de productos para administradores
 * Ahora consume datos de la API REST en lugar de la base de datos local
 */
@Composable
fun ProductManagementScreen(
    databaseRepository: DatabaseRepository = DatabaseRepository(LocalContext.current)
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val productRepository = remember { ProductRepository() }
    
    // Estado para productos de la API
    var apiProducts by remember { mutableStateOf<List<ProductoDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<ProductoDto?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    
    // Cargar productos de la API
    LaunchedEffect(Unit) {
        isLoading = true
        when (val result = productRepository.getAllProducts()) {
            is ApiResult.Success -> {
                apiProducts = result.data
                errorMessage = null
                Log.d("ProductManagement", "✅ Productos cargados: ${result.data.size}")
                result.data.forEach { product ->
                    Log.d("ProductManagement", "  - ${product.nombre} (Stock: ${product.stock}) - Activo: ${product.estaActivo}")
                }
            }
            is ApiResult.Error -> {
                errorMessage = result.message
                Log.e("ProductManagement", "❌ Error al cargar productos: ${result.message}")
            }
            else -> {}
        }
        isLoading = false
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Indicador de carga o error
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }
        
        errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚠️ Error al cargar productos",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = error, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            when (val result = productRepository.getAllProducts()) {
                                is ApiResult.Success -> {
                                    apiProducts = result.data
                                    errorMessage = null
                                }
                                is ApiResult.Error -> {
                                    errorMessage = result.message
                                }
                                else -> {}
                            }
                            isLoading = false
                        }
                    }) {
                        Text("Reintentar")
                    }
                }
            }
        }
        
        // Botones de acción y estadísticas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Producto")
            }

            // Estadísticas
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total: ${apiProducts.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Activos: ${apiProducts.count { it.estaActivo }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }

        // Lista de productos desde la API
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(apiProducts) { producto ->
                ApiProductCard(
                    producto = producto,
                    onEdit = { 
                        selectedProduct = producto
                        showEditDialog = true
                    },
                    onToggleActive = { productoToToggle ->
                        coroutineScope.launch {
                            // Crear producto actualizado con el estado invertido
                            val productoActualizado = productoToToggle.copy(
                                estaActivo = !productoToToggle.estaActivo
                            )
                            
                            when (productRepository.updateProduct(productoActualizado)) {
                                is ApiResult.Success -> {
                                    // Recargar lista
                                    when (val result = productRepository.getAllProducts()) {
                                        is ApiResult.Success -> {
                                            apiProducts = result.data
                                        }
                                        else -> {}
                                    }
                                    Toast.makeText(
                                        context, 
                                        if (productoToToggle.estaActivo) "Producto desactivado" else "Producto activado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                is ApiResult.Error -> {
                                    Toast.makeText(context, "Error al actualizar producto", Toast.LENGTH_SHORT).show()
                                }
                                else -> {}
                            }
                        }
                    },
                    onDelete = { productoToDelete ->
                        coroutineScope.launch {
                            when (productRepository.deleteProduct(productoToDelete.idProducto)) {
                                is ApiResult.Success -> {
                                    // Recargar lista
                                    when (val result = productRepository.getAllProducts()) {
                                        is ApiResult.Success -> {
                                            apiProducts = result.data
                                        }
                                        else -> {}
                                    }
                                    Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()
                                }
                                is ApiResult.Error -> {
                                    Toast.makeText(context, "Error al eliminar producto", Toast.LENGTH_SHORT).show()
                                }
                                else -> {}
                            }
                        }
                    }
                )
            }
        }
    }

    // Diálogos
    if (showCreateDialog) {
        ApiProductDialog(
            producto = null,
            onDismiss = { showCreateDialog = false },
            onSave = { nuevoProducto ->
                coroutineScope.launch {
                    when (val result = productRepository.createProduct(
                        idProducto = nuevoProducto.idProducto,
                        nombre = nuevoProducto.nombre,
                        linkImagen = nuevoProducto.linkImagen,
                        descripcion = nuevoProducto.descripcion,
                        precio = nuevoProducto.precio,
                        stock = nuevoProducto.stock,
                        idCategoria = nuevoProducto.idCategoria,
                        origen = nuevoProducto.origen,
                        certificacionOrganica = nuevoProducto.certificacionOrganica,
                        estaActivo = nuevoProducto.estaActivo
                    )) {
                        is ApiResult.Success -> {
                            // Recargar lista
                            when (val listResult = productRepository.getAllProducts()) {
                                is ApiResult.Success -> {
                                    apiProducts = listResult.data
                                }
                                else -> {}
                            }
                            showCreateDialog = false
                            Toast.makeText(context, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                        }
                        is ApiResult.Error -> {
                            Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
            }
        )
    }

    if (showEditDialog && selectedProduct != null) {
        ApiProductDialog(
            producto = selectedProduct,
            onDismiss = { 
                showEditDialog = false
                selectedProduct = null
            },
            onSave = { productoActualizado ->
                coroutineScope.launch {
                    when (val result = productRepository.updateProduct(productoActualizado)) {
                        is ApiResult.Success -> {
                            // Recargar lista
                            when (val listResult = productRepository.getAllProducts()) {
                                is ApiResult.Success -> {
                                    apiProducts = listResult.data
                                }
                                else -> {}
                            }
                            showEditDialog = false
                            selectedProduct = null
                            Toast.makeText(context, "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show()
                        }
                        is ApiResult.Error -> {
                            Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
            }
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onEdit: () -> Unit,
    onToggleActive: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (product.isActive) 
                Color.White.copy(alpha = 0.9f) 
            else 
                Color.Gray.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${product.price}/${product.priceUnit}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Badge de stock
                            Badge(
                                containerColor = when {
                                    product.stock <= 0 -> Color(0xFFE91E63)
                                    product.stock <= 10 -> Color(0xFFFF9800)
                                    else -> Color(0xFF4CAF50)
                                }
                            ) {
                                Text(
                                    text = "Stock: ${product.stock}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            
                            // Badge orgánico
                            if (product.isOrganic) {
                                Badge(containerColor = Color(0xFF8BC34A)) {
                                    Text(
                                        text = "🌿 Orgánico",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            
                            // Badge estado
                            Badge(
                                containerColor = if (product.isActive) Color(0xFF4CAF50) else Color(0xFF757575)
                            ) {
                                Text(
                                    text = if (product.isActive) "Activo" else "Inactivo",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        
                        if (product.description != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = product.description!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                maxLines = 2
                            )
                        }
                    }

                    // Botones de acción
                    Column {
                        IconButton(onClick = onEdit) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Editar",
                                tint = Color(0xFF2196F3)
                            )
                        }
                        IconButton(onClick = { onToggleActive(product) }) {
                            Icon(
                                if (product.isActive) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (product.isActive) "Desactivar" else "Activar",
                                tint = if (product.isActive) Color(0xFFFF9800) else Color(0xFF4CAF50)
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
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
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar el producto ${product.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(product)
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
fun ApiProductCard(
    producto: ProductoDto,
    onEdit: () -> Unit,
    onToggleActive: (ProductoDto) -> Unit,
    onDelete: (ProductoDto) -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (producto.estaActivo) 
                Color.White 
            else 
                Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Placeholder para imagen del producto
                Card(
                    modifier = Modifier.size(80.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (producto.estaActivo) 
                            Color(0xFFE8F5E9) 
                        else 
                            Color(0xFFEEEEEE)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = if (producto.estaActivo) 
                                Color(0xFF4CAF50) 
                            else 
                                Color(0xFF9E9E9E)
                        )
                    }
                }

                // Información del producto
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = producto.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (producto.estaActivo) Color.Black else Color.Gray,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Badge de estado
                        if (!producto.estaActivo) {
                            Badge(containerColor = Color(0xFF9E9E9E)) {
                                Text(
                                    text = "Inactivo",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = producto.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (producto.estaActivo) Color.DarkGray else Color.Gray,
                        maxLines = 2
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Precio
                        Badge(containerColor = Color(0xFF4CAF50)) {
                            Text(
                                text = "$${producto.precio}",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Stock
                        Badge(
                            containerColor = when {
                                producto.stock == 0 -> Color(0xFFE53935)
                                producto.stock <= 10 -> Color(0xFFFF9800)
                                else -> Color(0xFF2196F3)
                            }
                        ) {
                            Text(
                                text = "Stock: ${producto.stock}",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        
                        // Orgánico
                        if (producto.certificacionOrganica) {
                            Badge(containerColor = Color(0xFF8BC34A)) {
                                Text(
                                    text = "🌱 Orgánico",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    
                    // Origen si existe
                    producto.origen?.let { origen ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📍 $origen",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Botones de acción
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = onEdit) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar", color = Color(0xFF2196F3))
                }
                
                TextButton(onClick = { onToggleActive(producto) }) {
                    Icon(
                        if (producto.estaActivo) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (producto.estaActivo) "Desactivar" else "Activar",
                        tint = if (producto.estaActivo) Color(0xFFFF9800) else Color(0xFF4CAF50),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (producto.estaActivo) "Desactivar" else "Activar",
                        color = if (producto.estaActivo) Color(0xFFFF9800) else Color(0xFF4CAF50)
                    )
                }
                
                TextButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar", color = Color(0xFFE91E63))
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar el producto ${producto.nombre}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(producto)
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
fun ProductDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "") }
    var priceUnit by remember { mutableStateOf(product?.priceUnit ?: "kg") }
    var origin by remember { mutableStateOf(product?.origin ?: "") }
    var isOrganic by remember { mutableStateOf(product?.isOrganic ?: false) }
    var isActive by remember { mutableStateOf(product?.isActive ?: true) }
    var imageName by remember { mutableStateOf(product?.imageName ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = if (product == null) "Crear Producto" else "Editar Producto",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del producto") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) }
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        leadingIcon = { Icon(Icons.Filled.Description, contentDescription = null) }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Precio") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = null) }
                        )

                        OutlinedTextField(
                            value = priceUnit,
                            onValueChange = { priceUnit = it },
                            label = { Text("Unidad") },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("kg, litro, unidad") }
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Filled.Inventory, contentDescription = null) }
                    )
                }

                item {
                    OutlinedTextField(
                        value = origin,
                        onValueChange = { origin = it },
                        label = { Text("Origen (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null) }
                    )
                }

                item {
                    OutlinedTextField(
                        value = imageName,
                        onValueChange = { imageName = it },
                        label = { Text("Nombre de imagen (sin extensión)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("ej: manzana_funji") },
                        leadingIcon = { Icon(Icons.Filled.Image, contentDescription = null) }
                    )
                }

                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isOrganic,
                                onCheckedChange = { isOrganic = it }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Producto orgánico")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isActive,
                                onCheckedChange = { isActive = it }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Producto activo")
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                val productToSave = if (product == null) {
                                    // Crear nuevo producto
                                    Product(
                                        id = "PR${System.currentTimeMillis()}",
                                        name = name,
                                        imageName = imageName.ifEmpty { null },
                                        description = description.ifEmpty { null },
                                        price = price.toDoubleOrNull() ?: 0.0,
                                        stock = stock.toIntOrNull() ?: 0,
                                        origin = origin.ifEmpty { null },
                                        isOrganic = isOrganic,
                                        isActive = isActive,
                                        entryDate = Date(),
                                        categoryId = 1,
                                        priceUnit = priceUnit
                                    )
                                } else {
                                    // Actualizar producto existente
                                    product.copy(
                                        name = name,
                                        imageName = imageName.ifEmpty { null },
                                        description = description.ifEmpty { null },
                                        price = price.toDoubleOrNull() ?: product.price,
                                        stock = stock.toIntOrNull() ?: product.stock,
                                        origin = origin.ifEmpty { null },
                                        isOrganic = isOrganic,
                                        isActive = isActive,
                                        priceUnit = priceUnit
                                    )
                                }
                                onSave(productToSave)
                            },
                            modifier = Modifier.weight(1f),
                            enabled = name.isNotEmpty() && price.isNotEmpty() && stock.isNotEmpty()
                        ) {
                            Text(if (product == null) "Crear" else "Actualizar")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiProductDialog(
    producto: ProductoDto?,
    onDismiss: () -> Unit,
    onSave: (ProductoDto) -> Unit
) {
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(producto?.descripcion ?: "") }
    var precio by remember { mutableStateOf(producto?.precio?.toString() ?: "") }
    var stock by remember { mutableStateOf(producto?.stock?.toString() ?: "") }
    var origen by remember { mutableStateOf(producto?.origen ?: "") }
    var linkImagen by remember { mutableStateOf(producto?.linkImagen ?: "") }
    var certificacionOrganica by remember { mutableStateOf(producto?.certificacionOrganica ?: false) }
    var estaActivo by remember { mutableStateOf(producto?.estaActivo ?: true) }
    var idCategoria by remember { mutableStateOf(producto?.idCategoria?.toString() ?: "1") }
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (producto == null) "Crear Producto" else "Editar Producto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) }
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    leadingIcon = { Icon(Icons.Filled.Description, contentDescription = null) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Filled.Inventory, contentDescription = null) }
                    )
                }

                OutlinedTextField(
                    value = origen,
                    onValueChange = { origen = it },
                    label = { Text("Origen (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Valparaíso, Región Metropolitana") },
                    leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) }
                )

                OutlinedTextField(
                    value = linkImagen,
                    onValueChange = { linkImagen = it },
                    label = { Text("URL de imagen (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("https://...") },
                    leadingIcon = { Icon(Icons.Filled.Image, contentDescription = null) }
                )

                // Dropdown para categoría
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    val categoriaLabel = when (idCategoria.toIntOrNull()) {
                        1 -> "Frutas"
                        2 -> "Verduras"
                        3 -> "Lácteos"
                        4 -> "Carnes"
                        5 -> "Granos"
                        else -> "Seleccionar categoría"
                    }
                    
                    OutlinedTextField(
                        value = categoriaLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        leadingIcon = { Icon(Icons.Filled.Category, contentDescription = null) }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf(
                            Pair(1, "Frutas"),
                            Pair(2, "Verduras"),
                            Pair(3, "Lácteos"),
                            Pair(4, "Carnes"),
                            Pair(5, "Granos")
                        ).forEach { (id, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    idCategoria = id.toString()
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Switch para certificación orgánica
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Eco,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Certificación Orgánica")
                    }
                    Switch(
                        checked = certificacionOrganica,
                        onCheckedChange = { certificacionOrganica = it }
                    )
                }

                // Switch para estado activo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (estaActivo) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = if (estaActivo) Color(0xFF4CAF50) else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Producto Activo")
                    }
                    Switch(
                        checked = estaActivo,
                        onCheckedChange = { estaActivo = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val productoToSave = ProductoDto(
                                idProducto = producto?.idProducto ?: "PROD${System.currentTimeMillis()}",
                                nombre = nombre,
                                linkImagen = if (linkImagen.isNotEmpty()) linkImagen else producto?.linkImagen,
                                descripcion = descripcion,
                                precio = precio.toIntOrNull() ?: 0,
                                stock = stock.toIntOrNull() ?: 0,
                                origen = origen.ifEmpty { null },
                                certificacionOrganica = certificacionOrganica,
                                estaActivo = estaActivo,
                                fechaIngreso = producto?.fechaIngreso,
                                idCategoria = idCategoria.toIntOrNull() ?: 1
                            )
                            onSave(productoToSave)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = nombre.isNotEmpty() && descripcion.isNotEmpty() && 
                                 precio.isNotEmpty() && stock.isNotEmpty()
                    ) {
                        Text(if (producto == null) "Crear" else "Actualizar")
                    }
                }
            }
        }
    }
}