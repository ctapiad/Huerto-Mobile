package com.example.huerto_hogar.ui.admin

import android.util.Log
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.huerto_hogar.data.model.User
import com.example.huerto_hogar.data.enums.UserRole
import com.example.huerto_hogar.network.repository.UserRepository
import com.example.huerto_hogar.network.ApiResult
import com.example.huerto_hogar.network.Usuario
import com.example.huerto_hogar.viewmodel.UsuarioViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Date

/**
 * Pantalla de gestión de usuarios para administradores
 * Ahora consume datos de la API REST en lugar de la base de datos local
 */
@Composable
fun UserManagementScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userRepository = remember { UserRepository() }
    
    // Estado para usuarios de la API
    var apiUsers by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<Usuario?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    
    // Cargar usuarios de la API
    LaunchedEffect(Unit) {
        isLoading = true
        when (val result = userRepository.getAllUsers()) {
            is ApiResult.Success -> {
                apiUsers = result.data
                errorMessage = null
                Log.d("UserManagement", "Loaded ${result.data.size} users from API")
            }
            is ApiResult.Error -> {
                errorMessage = result.message
                Log.e("UserManagement", "Error loading users: ${result.message}")
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
                        text = "⚠️ Error al cargar usuarios",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = error, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            when (val result = userRepository.getAllUsers()) {
                                is ApiResult.Success -> {
                                    apiUsers = result.data
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
        
        // Botón para crear nuevo usuario
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
                Icon(Icons.Filled.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Usuario")
            }

            // Estadísticas rápidas
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = "Total: ${apiUsers.size}",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Lista de usuarios desde la API
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(apiUsers) { usuario ->
                ApiUserCard(
                    usuario = usuario,
                    onEdit = { 
                        selectedUser = usuario
                        showEditDialog = true
                    },
                    onDelete = { usuarioToDelete ->
                        coroutineScope.launch {
                            when (userRepository.deleteUser(usuarioToDelete.id ?: "")) {
                                is ApiResult.Success -> {
                                    // Recargar lista
                                    when (val result = userRepository.getAllUsers()) {
                                        is ApiResult.Success -> {
                                            apiUsers = result.data
                                        }
                                        else -> {}
                                    }
                                    Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                                }
                                is ApiResult.Error -> {
                                    Toast.makeText(context, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
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
        ApiUserDialog(
            usuario = null,
            onDismiss = { showCreateDialog = false },
            onSave = { nuevoUsuario ->
                coroutineScope.launch {
                    when (val result = userRepository.createUser(
                        nombre = nuevoUsuario.nombre,
                        email = nuevoUsuario.email,
                        password = nuevoUsuario.password,
                        direccion = nuevoUsuario.direccion,
                        telefono = nuevoUsuario.telefono,
                        idComuna = nuevoUsuario.idComuna,
                        idTipoUsuario = nuevoUsuario.idTipoUsuario
                    )) {
                        is ApiResult.Success -> {
                            // Recargar lista
                            when (val listResult = userRepository.getAllUsers()) {
                                is ApiResult.Success -> {
                                    apiUsers = listResult.data
                                }
                                else -> {}
                            }
                            showCreateDialog = false
                            Toast.makeText(context, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
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

    if (showEditDialog && selectedUser != null) {
        ApiUserDialog(
            usuario = selectedUser,
            onDismiss = { 
                showEditDialog = false
                selectedUser = null
            },
            onSave = { usuarioActualizado ->
                coroutineScope.launch {
                    // Validación previa
                    if (usuarioActualizado.id.isNullOrEmpty()) {
                        Toast.makeText(context, "Error: ID de usuario no válido", Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    
                    if (usuarioActualizado.nombre.isEmpty() || usuarioActualizado.email.isEmpty()) {
                        Toast.makeText(context, "Error: Nombre y email son obligatorios", Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    
                    Log.d("UserManagement", "=== Actualizando usuario ===")
                    Log.d("UserManagement", "ID: ${usuarioActualizado.id}")
                    Log.d("UserManagement", "Nombre: ${usuarioActualizado.nombre}")
                    Log.d("UserManagement", "Email: ${usuarioActualizado.email}")
                    Log.d("UserManagement", "Password length: ${usuarioActualizado.password.length}")
                    Log.d("UserManagement", "Dirección: ${usuarioActualizado.direccion}")
                    Log.d("UserManagement", "Teléfono: ${usuarioActualizado.telefono}")
                    Log.d("UserManagement", "ID Comuna: ${usuarioActualizado.idComuna}")
                    Log.d("UserManagement", "ID Tipo Usuario: ${usuarioActualizado.idTipoUsuario}")
                    
                    when (val result = userRepository.updateUser(
                        id = usuarioActualizado.id,
                        nombre = usuarioActualizado.nombre,
                        email = usuarioActualizado.email,
                        password = if (usuarioActualizado.password.isNotEmpty()) usuarioActualizado.password else null,
                        direccion = usuarioActualizado.direccion,
                        telefono = usuarioActualizado.telefono,
                        idComuna = usuarioActualizado.idComuna,
                        idTipoUsuario = usuarioActualizado.idTipoUsuario
                    )) {
                        is ApiResult.Success -> {
                            val response = result.data
                            
                            // Verificar si hay error de email duplicado
                            if (response.contains("Ya existe otro usuario con ese email", ignoreCase = true)) {
                                Toast.makeText(context, "Este email ya está registrado por otro usuario", Toast.LENGTH_LONG).show()
                            } else if (response.contains("modificado correctamente", ignoreCase = true)) {
                                // Recargar lista
                                when (val listResult = userRepository.getAllUsers()) {
                                    is ApiResult.Success -> {
                                        apiUsers = listResult.data
                                        Log.d("UserManagement", "✅ Usuario actualizado y lista recargada")
                                    }
                                    else -> {}
                                }
                                showEditDialog = false
                                selectedUser = null
                                Toast.makeText(context, "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
                            }
                        }
                        is ApiResult.Error -> {
                            Log.e("UserManagement", "❌ Error al actualizar usuario")
                            Log.e("UserManagement", "Mensaje: ${result.message}")
                            Log.e("UserManagement", "Usuario ID: ${usuarioActualizado.id}")
                            Toast.makeText(
                                context, 
                                "Error al actualizar usuario:\n${result.message}", 
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {}
                    }
                }
            }
        )
    }
}

@Composable
fun ApiUserCard(
    usuario: Usuario,
    onEdit: () -> Unit,
    onDelete: (Usuario) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = usuario.nombre ?: "Sin nombre",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = usuario.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    
                    // Badge del tipo de usuario
                    val userType = when (usuario.idTipoUsuario) {
                        1 -> "ADMIN"
                        2 -> "VENDEDOR"
                        3 -> "CLIENTE"
                        else -> "DESCONOCIDO"
                    }
                    Badge(
                        containerColor = when (usuario.idTipoUsuario) {
                            1 -> Color(0xFFE91E63)  // ADMIN
                            2 -> Color(0xFF2196F3)  // VENDEDOR
                            3 -> Color(0xFF4CAF50)  // CLIENTE
                            else -> Color.Gray
                        }
                    ) {
                        Text(
                            text = userType,
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Editar",
                            tint = Color(0xFF2196F3)
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

            if (usuario.direccion != null || usuario.telefono != null) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                
                usuario.direccion?.let { direccion ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = direccion,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                
                usuario.telefono?.let { telefono ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = telefono.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Mostrar ID de MongoDB (útil para debugging)
            usuario.id?.let { id ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ID: ${id.take(8)}...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray.copy(alpha = 0.6f),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar al usuario ${usuario.nombre}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(usuario)
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
fun ApiUserDialog(
    usuario: Usuario?,
    onDismiss: () -> Unit,
    onSave: (Usuario) -> Unit
) {
    val viewModel: UsuarioViewModel = viewModel()
    val estado by viewModel.estado.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    
    // Cargar datos del usuario si estamos editando
    LaunchedEffect(usuario) {
        if (usuario != null) {
            viewModel.cargarUsuario(usuario)
        } else {
            viewModel.limpiarFormulario()
        }
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (usuario == null) "Crear Usuario" else "Editar Usuario",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = estado.nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    isError = estado.errores.nombre != null,
                    supportingText = if (estado.errores.nombre != null) {
                        { Text(estado.errores.nombre!!) }
                    } else null
                )

                OutlinedTextField(
                    value = estado.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                    isError = estado.errores.email != null,
                    supportingText = if (estado.errores.email != null) {
                        { Text(estado.errores.email!!) }
                    } else null
                )

                OutlinedTextField(
                    value = estado.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text(if (usuario == null) "Contraseña" else "Nueva contraseña (dejar vacío para mantener)") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    isError = estado.errores.password != null,
                    supportingText = if (estado.errores.password != null) {
                        { Text(estado.errores.password!!) }
                    } else null
                )

                // Dropdown para tipo de usuario
                val userTypeLabel = when (estado.idTipoUsuario) {
                    1 -> "ADMIN"
                    2 -> "VENDEDOR"
                    3 -> "CLIENTE"
                    else -> "Seleccione tipo"
                }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = userTypeLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Usuario") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
                        isError = estado.errores.idTipoUsuario != null,
                        supportingText = if (estado.errores.idTipoUsuario != null) {
                            { Text(estado.errores.idTipoUsuario!!) }
                        } else null
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf(
                            Pair(1, "ADMIN"),
                            Pair(2, "VENDEDOR"),
                            Pair(3, "CLIENTE")
                        ).forEach { (id, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    viewModel.onIdTipoUsuarioChange(id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = estado.direccion,
                    onValueChange = { viewModel.onDireccionChange(it) },
                    label = { Text("Dirección (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    isError = estado.errores.direccion != null,
                    supportingText = if (estado.errores.direccion != null) {
                        { Text(estado.errores.direccion!!) }
                    } else null
                )

                OutlinedTextField(
                    value = if (estado.telefono == 0) "" else estado.telefono.toString(),
                    onValueChange = { viewModel.onTelefonoChange(it) },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                    isError = estado.errores.telefono != null,
                    supportingText = if (estado.errores.telefono != null) {
                        { Text(estado.errores.telefono!!) }
                    } else null
                )

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
                            val esEdicion = usuario != null
                            if (viewModel.validarFormulario(esEdicion)) {
                                val usuarioToSave = Usuario(
                                    id = usuario?.id,
                                    nombre = estado.nombre,
                                    email = estado.email,
                                    password = if (usuario != null && estado.password.isEmpty()) {
                                        usuario.password
                                    } else if (estado.password.isNotEmpty()) {
                                        estado.password
                                    } else {
                                        ""
                                    },
                                    direccion = if (estado.direccion.isNotEmpty()) estado.direccion else null,
                                    telefono = if (estado.telefono > 0) estado.telefono else null,
                                    idComuna = estado.idComuna,
                                    idTipoUsuario = estado.idTipoUsuario,
                                    fechaRegistro = usuario?.fechaRegistro
                                )
                                onSave(usuarioToSave)
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
                            Text(if (usuario == null) "Crear" else "Actualizar")
                        }
                    }
                }
            }
        }
    }
}
