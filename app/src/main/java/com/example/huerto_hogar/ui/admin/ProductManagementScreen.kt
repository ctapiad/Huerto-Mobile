package com.example.huerto_hogar.ui.admin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.huerto_hogar.R
import com.example.huerto_hogar.data.*
import java.util.Date

/**
 * Pantalla de gestión de productos para administradores
 */
@Composable
fun ProductManagementScreen() {
    val products by LocalDataRepository.products.collectAsState()
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
                        text = "Total: ${products.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Activos: ${products.count { it.isActive }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }

        // Lista de productos
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onEdit = { 
                        selectedProduct = product
                        showEditDialog = true
                    },
                    onToggleActive = { productToToggle ->
                        LocalDataRepository.updateProduct(
                            productToToggle.copy(isActive = !productToToggle.isActive)
                        )
                        Toast.makeText(
                            context, 
                            if (productToToggle.isActive) "Producto desactivado" else "Producto activado",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onDelete = { productToDelete ->
                        LocalDataRepository.deleteProduct(productToDelete.id)
                        Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Diálogos
    if (showCreateDialog) {
        ProductDialog(
            product = null,
            onDismiss = { showCreateDialog = false },
            onSave = { newProduct ->
                LocalDataRepository.createProduct(newProduct)
                showCreateDialog = false
                Toast.makeText(context, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showEditDialog && selectedProduct != null) {
        ProductDialog(
            product = selectedProduct,
            onDismiss = { 
                showEditDialog = false
                selectedProduct = null
            },
            onSave = { updatedProduct ->
                LocalDataRepository.updateProduct(updatedProduct)
                showEditDialog = false
                selectedProduct = null
                Toast.makeText(context, "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show()
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