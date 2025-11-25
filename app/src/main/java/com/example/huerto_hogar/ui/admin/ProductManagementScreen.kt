package com.example.huerto_hogar.ui.admin

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import coil.compose.AsyncImage
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
import com.example.huerto_hogar.data.model.Product
import com.example.huerto_hogar.network.repository.ProductRepository
import com.example.huerto_hogar.network.ApiResult
import com.example.huerto_hogar.network.ProductoDto
import com.example.huerto_hogar.viewmodel.ProductoViewModel
import com.example.huerto_hogar.service.ImageUploadService
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Date

/**
 * Pantalla de gestión de productos para administradores
 * Ahora consume datos de la API REST en lugar de la base de datos local
 */
@Composable
fun ProductManagementScreen() {
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
            productosExistentes = apiProducts,
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
            productosExistentes = apiProducts,
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
                // Imagen del producto (desde URL o recurso local)
                AsyncImage(
                    model = producto.linkImagen?.takeIf { it.startsWith("http") } ?: R.drawable.huertohogarfondo,
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.huertohogarfondo),
                    placeholder = painterResource(R.drawable.huertohogarfondo)
                )

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiProductDialog(
    producto: ProductoDto?,
    productosExistentes: List<ProductoDto>,
    onDismiss: () -> Unit,
    onSave: (ProductoDto) -> Unit
) {
    val context = LocalContext.current
    val imageUploadService = remember { ImageUploadService(context) }
    val viewModel: ProductoViewModel = viewModel { ProductoViewModel(imageUploadService) }
    val estado by viewModel.estado.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uploadingImage by viewModel.uploadingImage.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    // Launcher para seleccionar imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onImageSelected(it)
        }
    }
    
    // Cargar datos del producto si estamos editando
    LaunchedEffect(producto) {
        if (producto != null) {
            viewModel.cargarProducto(producto)
        } else {
            viewModel.limpiarFormulario()
        }
    }
    
    // Función para generar ID según categoría
    fun generarIdProducto(categoria: Int): String {
        val prefijo = when(categoria) {
            1 -> "FR" // Frutas
            2 -> "VR" // Verduras
            3 -> "OR" // Orgánicos
            4 -> "PL" // Lácteos (Productos Lácteos)
            5 -> "GR" // Granos
            else -> "PR" // Producto genérico
        }
        
        // Obtener productos con el mismo prefijo
        val productosConPrefijo = productosExistentes.filter { 
            it.idProducto.startsWith(prefijo) 
        }
        
        // Encontrar el número más alto
        val maxNumero = productosConPrefijo.mapNotNull { 
            it.idProducto.substring(2).toIntOrNull() 
        }.maxOrNull() ?: 0
        
        // Generar siguiente número con formato de 3 dígitos
        val siguienteNumero = (maxNumero + 1).toString().padStart(3, '0')
        return "$prefijo$siguienteNumero"
    }

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
                    value = estado.nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Nombre del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
                    isError = estado.errores.nombre != null,
                    supportingText = estado.errores.nombre?.let { { Text(it) } }
                )

                OutlinedTextField(
                    value = estado.descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    leadingIcon = { Icon(Icons.Filled.Description, contentDescription = null) },
                    isError = estado.errores.descripcion != null,
                    supportingText = estado.errores.descripcion?.let { { Text(it) } }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = if (estado.precio == 0) "" else estado.precio.toString(),
                        onValueChange = { viewModel.onPrecioChange(it) },
                        label = { Text("Precio") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = null) },
                        isError = estado.errores.precio != null,
                        supportingText = estado.errores.precio?.let { { Text(it) } }
                    )

                    OutlinedTextField(
                        value = if (estado.stock == 0) "" else estado.stock.toString(),
                        onValueChange = { viewModel.onStockChange(it) },
                        label = { Text("Stock") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Filled.Inventory, contentDescription = null) },
                        isError = estado.errores.stock != null,
                        supportingText = estado.errores.stock?.let { { Text(it) } }
                    )
                }

                OutlinedTextField(
                    value = estado.origen,
                    onValueChange = { viewModel.onOrigenChange(it) },
                    label = { Text("Origen (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Valparaíso, Región Metropolitana") },
                    leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                    isError = estado.errores.origen != null,
                    supportingText = estado.errores.origen?.let { { Text(it) } }
                )

                // Selector de imagen con vista previa
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Imagen del producto",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (estado.errores.linkImagen != null) Color.Red else Color.Gray
                    )
                    
                    // Vista previa de la imagen
                    if (selectedImageUri != null || estado.linkImagen.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = selectedImageUri ?: estado.linkImagen,
                                    contentDescription = "Vista previa",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                
                                // Botón para quitar imagen
                                IconButton(
                                    onClick = { viewModel.clearImage() },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color.White.copy(alpha = 0.7f)
                                    )
                                ) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "Quitar imagen",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                    
                    // Botones de acción para imagen
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            enabled = !uploadingImage && !isLoading
                        ) {
                            Icon(Icons.Filled.Image, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (selectedImageUri != null || estado.linkImagen.isNotEmpty()) "Cambiar" else "Seleccionar")
                        }
                        
                        if (selectedImageUri != null && estado.linkImagen.isEmpty()) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val success = viewModel.uploadSelectedImage()
                                        if (!success) {
                                            Toast.makeText(
                                                context,
                                                "Error al subir la imagen",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Imagen subida exitosamente",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !uploadingImage && !isLoading
                            ) {
                                if (uploadingImage) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                } else {
                                    Icon(Icons.Filled.Upload, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Subir")
                                }
                            }
                        }
                    }
                    
                    // Mensaje de error
                    if (estado.errores.linkImagen != null) {
                        Text(
                            text = estado.errores.linkImagen!!,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Dropdown para categoría
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    val categoriaLabel = when (estado.idCategoria) {
                        1 -> "Frutas"
                        2 -> "Verduras"
                        3 -> "Lácteos"
                        4 -> "Granos"
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
                        leadingIcon = { Icon(Icons.Filled.Category, contentDescription = null) },
                        isError = estado.errores.idCategoria != null,
                        supportingText = estado.errores.idCategoria?.let { { Text(it) } }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf(
                            Pair(1, "Frutas"),
                            Pair(2, "Verduras"),
                            Pair(3, "Lácteos"),
                            Pair(4, "Granos")
                        ).forEach { (id, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    viewModel.onIdCategoriaChange(id)
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
                        checked = estado.certificacionOrganica,
                        onCheckedChange = { viewModel.onCertificacionOrganicaChange(it) }
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
                            if (estado.estaActivo) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = if (estado.estaActivo) Color(0xFF4CAF50) else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Producto Activo")
                    }
                    Switch(
                        checked = estado.estaActivo,
                        onCheckedChange = { viewModel.onEstaActivoChange(it) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            // Validar formulario antes de guardar
                            val esEdicion = producto != null
                            if (viewModel.validarFormulario(esEdicion)) {
                                val productoToSave = ProductoDto(
                                    idProducto = producto?.idProducto ?: generarIdProducto(estado.idCategoria),
                                    nombre = estado.nombre,
                                    linkImagen = if (estado.linkImagen.isNotEmpty()) estado.linkImagen else producto?.linkImagen,
                                    descripcion = estado.descripcion,
                                    precio = estado.precio,
                                    stock = estado.stock,
                                    origen = estado.origen.ifEmpty { null },
                                    certificacionOrganica = estado.certificacionOrganica,
                                    estaActivo = estado.estaActivo,
                                    fechaIngreso = producto?.fechaIngreso,
                                    idCategoria = estado.idCategoria
                                )
                                onSave(productoToSave)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(if (producto == null) "Crear" else "Actualizar")
                        }
                    }
                }
            }
        }
    }
}