package com.example.huerto_hogar.ui.user

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.huerto_hogar.data.LocalDataRepository
import com.example.huerto_hogar.data.enums.UserRole
import com.example.huerto_hogar.viewmodel.AuthViewModel

/**
 * Pantalla de perfil de usuario donde puede ver y editar sus datos
 */
@Composable
fun UserProfileScreen(
    onLoginRequired: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    currentUser?.let { user ->
        UserProfileContent(
            user = user,
            onLoginRequired = onLoginRequired,
            authViewModel = authViewModel
        )
    } ?: NoUserProfileCard(onLoginRequired = onLoginRequired)
}

@Composable
private fun UserProfileContent(
    user: com.example.huerto_hogar.data.model.User,
    onLoginRequired: () -> Unit,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(user.name) }
    var password by remember { mutableStateOf(user.password) }
    var address by remember { mutableStateOf(user.address ?: "") }
    var phone by remember { mutableStateOf(user.phone?.toString() ?: "") }

    // Reset fields when user changes or edit mode changes
    LaunchedEffect(user, isEditing) {
        if (!isEditing) {
            name = user.name
            password = user.password
            address = user.address ?: ""
            phone = user.phone?.toString() ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Título del perfil
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2196F3).copy(alpha = 0.9f)
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
                    Icons.Filled.Person,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "👤 Mi Perfil",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isEditing) "Editando información" else "Información personal",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Información del usuario
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Email (no editable)
                InfoField(
                    label = "Email",
                    value = user.email,
                    isEditable = false,
                    icon = Icons.Filled.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Rol (no editable)
                InfoField(
                    label = "Rol",
                    value = user.role.name,
                    isEditable = false,
                    icon = Icons.Filled.Badge,
                    badgeColor = when (user.role) {
                        UserRole.ADMIN -> Color(0xFFE91E63)
                        UserRole.VENDEDOR -> Color(0xFF2196F3)
                        UserRole.CLIENTE -> Color(0xFF4CAF50)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nombre
                EditableField(
                    label = "Nombre completo",
                    value = name,
                    onValueChange = { name = it },
                    isEditing = isEditing,
                    icon = Icons.Filled.Person
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Contraseña
                EditableField(
                    label = "Contraseña",
                    value = password,
                    onValueChange = { password = it },
                    isEditing = isEditing,
                    icon = Icons.Filled.Lock,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dirección
                EditableField(
                    label = "Dirección",
                    value = address,
                    onValueChange = { address = it },
                    isEditing = isEditing,
                    icon = Icons.Filled.Home,
                    placeholder = "Ingresa tu dirección"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Teléfono
                EditableField(
                    label = "Teléfono",
                    value = phone,
                    onValueChange = { phone = it },
                    isEditing = isEditing,
                    icon = Icons.Filled.Phone,
                    placeholder = "Ingresa tu teléfono",
                    keyboardType = KeyboardType.Phone
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isEditing) Arrangement.spacedBy(8.dp) else Arrangement.Center
                ) {
                    if (isEditing) {
                        // Botón cancelar
                        OutlinedButton(
                            onClick = { isEditing = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Cancel, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cancelar")
                        }

                        // Botón guardar
                        Button(
                            onClick = {
                                val updatedUser = user.copy(
                                    name = name,
                                    password = password,
                                    address = address.ifEmpty { null },
                                    phone = phone.toIntOrNull()
                                )
                                coroutineScope.launch {
                                    // Actualizar usuario en memoria (sin persistencia por ahora)
                                    LocalDataRepository.setCurrentUser(updatedUser)
                                    isEditing = false
                                    Toast.makeText(context, "Perfil actualizado en memoria (sin persistencia)", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = name.isNotEmpty() && password.isNotEmpty()
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar")
                        }
                    } else {
                        // Botón editar
                        Button(
                            onClick = { isEditing = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            )
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editar Perfil")
                        }
                    }
                }
            }
        }

        // Información de registro
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Gray.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "ℹ️ Información de Cuenta",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Usuario desde: ${java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(user.registrationDate)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun NoUserProfileCard(onLoginRequired: () -> Unit) {
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
                Icons.Filled.PersonOff,
                contentDescription = "No hay usuario",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF2196F3)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Inicia sesión para ver tu perfil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Necesitas iniciar sesión para ver y editar tu información personal.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onLoginRequired,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Sesión")
            }
        }
    }
}

@Composable
fun InfoField(
    label: String,
    value: String,
    isEditable: Boolean = false,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color? = null
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            
            if (badgeColor != null) {
                Badge(containerColor = badgeColor) {
                    Text(
                        text = value,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String = "",
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder) },
                leadingIcon = { Icon(icon, contentDescription = null) },
                visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (isPassword && value.isNotEmpty()) "••••••••" else value.ifEmpty { "No especificado" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value.isEmpty()) Color.Gray else Color.Black
                )
            }
        }
    }
}